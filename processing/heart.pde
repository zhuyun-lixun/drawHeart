int HeartSize = 5;

void setup()
{
  size(700,700,P2D);
  frameRate(60);
  background(0);
}

void draw()
{
  translate(350,350);
  stroke(250, 0, 0);
  strokeWeight(2);
  for (float t=0; t<=2*PI; t+=.01)
    point((-16*HeartSize*pow(sin(t),3)), (-(13*HeartSize*cos(t) -
    5*HeartSize*cos(2*t) - 2*HeartSize*cos(3*t) - cos(4*t))));
 }
