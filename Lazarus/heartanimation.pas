unit HeartAnimation;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, Graphics, Controls, Forms, ExtCtrls, Math, Dialogs;

type
  TMyPoint = record
    X, Y: Double;
    Color: TColor;
  end;

  { TForm1 }
  TForm1 = class(TForm)
    Timer1: TTimer;
    procedure FormCreate(Sender: TObject);
    procedure FormPaint(Sender: TObject);
    procedure Timer1Timer(Sender: TObject);
  private
    { private declarations }
    const
      xScreen = 1200;
      yScreen = 800;
      PI = 3.14159265359;
      e = 2.71828;
      averag_distance = 0.162;
      quantity = 506;
      circles = 210;
      frames = 20;
    var
      origin_points: array of TMyPoint;
      points: array of TMyPoint;
      images: array of TBitmap;
      currentFrame: Integer;
      extend: Boolean;
      colors: array[0..6] of TColor;
    procedure CreateData;
    function ScreenX(x: Double): Double;
    function ScreenY(y: Double): Double;
    function CreateRandom(x1, x2: Integer): Integer;
    procedure UpdateFrame;
  public
    { public declarations }
  end;

var
  Form1: TForm1;

implementation

{$R *.lfm}

{ TForm1 }

procedure TForm1.FormCreate(Sender: TObject);
begin
  ClientWidth := xScreen;
  ClientHeight := yScreen;
  DoubleBuffered := True;

  SetLength(origin_points, quantity);
  SetLength(points, circles * quantity);
  SetLength(images, frames);

  colors[0] := RGBToColor(255, Min(32 + 204, 255), 83);
  colors[1] := RGBToColor(252, Min(222 + 204, 255), 250);
  colors[2] := RGBToColor(255, Min(0 + 153, 255), Min(0 + 204, 255));
  colors[3] := RGBToColor(255, Min(0 + 153, 255), Min(0 + 204, 255));
  colors[4] := RGBToColor(255, 2, Min(2 + 204, 255));
  colors[5] := RGBToColor(255, Min(0 + 153, 255), Min(8 + 204, 255));
  colors[6] := RGBToColor(255, 5, Min(5 + 204, 255));

  Randomize;  // Random number generator initialization

  CreateData;

  Timer1.Interval := 20;
  Timer1.Enabled := True;
end;

procedure TForm1.CreateData;
var
  index: Integer;
  x, y, x1, y1, x2, y2, radian, size, success_p, distance, distance_increase, x_increase, y_increase: Double;
  i, frame: Integer;
  bmp: TBitmap;
begin
  index := 0;
  x1 := 0;
  y1 := 0;

  radian := 0.1;
  while radian <= 2 * PI do
  begin
    x2 := 16 * Power(Sin(radian), 3);
    y2 := 13 * Cos(radian) - 5 * Cos(2 * radian) - 2 * Cos(3 * radian) - Cos(4 * radian);
    distance := Sqrt(Power(x2 - x1, 2) + Power(y2 - y1, 2));

    if distance > averag_distance then
    begin
      x1 := x2;
      y1 := y2;
      origin_points[index].X := x2;
      origin_points[index].Y := y2;
      Inc(index);
    end;

    radian := radian + 0.005;// 0.1;
  end;

  index := 0;
  size := 0.1;
  while size <= 20 do
  begin
    success_p := 1 / (1 + Power(e, 8 - size / 2));

    for i := 0 to quantity - 1 do
    begin
      if success_p > CreateRandom(0, 100) / 100.0 then
      begin
        points[index].Color := colors[CreateRandom(0, 6)]; // 0부터 6까지의 인덱스를 랜덤으로 선택
        points[index].X := size * origin_points[i].X + CreateRandom(-4, 4);
        points[index].Y := size * origin_points[i].Y + CreateRandom(-4, 4);
        Inc(index);
      end;
    end;

    size := size + 0.1;
  end;

  for frame := 0 to frames - 1 do
  begin
    bmp := TBitmap.Create;
    bmp.SetSize(xScreen, yScreen);
    bmp.Canvas.Brush.Color := clBlack;
    bmp.Canvas.FillRect(0, 0, xScreen, yScreen);

    for i := 0 to index - 1 do
    begin
      x := points[i].X;
      y := points[i].Y;
      distance := Sqrt(Power(x, 2) + Power(y, 2));

      if distance <> 0 then
      begin
        distance_increase := -0.0009 * distance * distance + 0.35714 * distance + 5;
        x_increase := distance_increase * x / distance / frames;
        y_increase := distance_increase * y / distance / frames;

        points[i].X := points[i].X + x_increase;
        points[i].Y := points[i].Y + y_increase;
      end;

      bmp.Canvas.Brush.Color := points[i].Color;
      bmp.Canvas.FillRect(Round(ScreenX(points[i].X)), Round(ScreenY(points[i].Y)), Round(ScreenX(points[i].X)) + 1, Round(ScreenY(points[i].Y)) + 1);
    end;

    size := 17;
    while size <= 23 do
    begin
      for i := 0 to quantity - 1 do
      begin
        if ((CreateRandom(0, 100) / 100.0 > 0.6) and (size >= 20)) or
           ((size < 20) and (CreateRandom(0, 100) / 100.0 > 0.95)) then
        begin
          if size >= 20 then
          begin
            x := origin_points[i].X * size + CreateRandom(-frame * frame div 5 - 15, frame * frame div 5 + 15);
            y := origin_points[i].Y * size + CreateRandom(-frame * frame div 5 - 15, frame * frame div 5 + 15);
          end
          else
          begin
            x := origin_points[i].X * size + CreateRandom(-5, 5);
            y := origin_points[i].Y * size + CreateRandom(-5, 5);
          end;

          bmp.Canvas.Brush.Color := colors[CreateRandom(0, 6)]; // 0부터 6까지의 인덱스를 랜덤으로 선택
          bmp.Canvas.FillRect(Round(ScreenX(x)), Round(ScreenY(y)), Round(ScreenX(x)) + 1, Round(ScreenY(y)) + 1);
        end;
      end;

      size := size + 0.3; // 0.1;
    end;

    images[frame] := bmp;
  end;
end;

function TForm1.ScreenX(x: Double): Double;
begin
  Result := x + xScreen / 2;
end;

function TForm1.ScreenY(y: Double): Double;
begin
  Result := -y + yScreen / 2;
end;

function TForm1.CreateRandom(x1, x2: Integer): Integer;
begin
  Result := Random(x2 - x1 + 1) + x1;
end;

procedure TForm1.UpdateFrame;
begin
  if extend then
  begin
    if currentFrame = frames - 1 then
      extend := False
    else
      Inc(currentFrame);
  end
  else
  begin
    if currentFrame = 0 then
      extend := True
    else
      Dec(currentFrame);
  end;
end;

procedure TForm1.FormPaint(Sender: TObject);
begin
  Canvas.Draw(0, 0, images[currentFrame]);
end;

procedure TForm1.Timer1Timer(Sender: TObject);
begin
  UpdateFrame;
  Invalidate;
end;

end.

