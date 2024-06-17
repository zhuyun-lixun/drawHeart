using System;
using System.Drawing;
using System.Windows.Forms;

namespace HeartAnimation
{
    public partial class Form1 : Form
    {
        // heart 점 구조체
        private struct MyPoint
        {
            public double X, Y;
            public Color Color;
        }

        // 색상 배열 초기화
        private Color[] colors = { Color.FromArgb(255, Math.Min(32 + 204, 255), 83),
                                   Color.FromArgb(252, Math.Min(222 + 204, 255), 250),
                                   Color.FromArgb(255, Math.Min(0 + 153, 255), Math.Min(0 + 204, 255)),
                                   Color.FromArgb(255, Math.Min(0 + 153, 255), Math.Min(0 + 204, 255)),
                                   Color.FromArgb(255, 2, Math.Min(2 + 204, 255)),
                                   Color.FromArgb(255, Math.Min(0 + 153, 255), Math.Min(8 + 204, 255)),
                                   Color.FromArgb(255, 5, Math.Min(5 + 204, 255))
        };

        // 상수 정의
        private const int xScreen = 1200;                            // 화면 너비
        private const int yScreen = 800;                             // 화면 높이  
        private const double PI = 3.1426535159;                      // 원주율 
        private const double e = 2.71828;                            // 자연수 e     
        private const double averag_distance = 0.162;                // 호의 각도가 0.01 증가할 때 원시 매개변수 방정식의 각 점 사이의 평균 거리
        private const int quantity = 506;                            // 완전한 하트 모양에 필요한 점의 수
        private const int circles = 210;                             // 하트 모양을 구성하는 하트의 개수 (각 하트는 다른 계수를 곱함)  
        private const int frames = 20;                               // 하트가 확장하는 프레임 수     

        // 데이터 배열 초기화
        private MyPoint[] origin_points = new MyPoint[quantity];     // 원시 하트 데이터를 저장하는 배열  
        private MyPoint[] points = new MyPoint[circles * quantity];  // 모든 하트 데이터를 저장하는 배열
        private Bitmap[] images = new Bitmap[frames];                // 이미지 배열

        // 애니메이션 상태 변수 초기화
        private int currentFrame = 0;
        private bool extend = true;
        private Random random = new Random(); // Random 객체를 한 번만 생성

        public Form1()
        {
            InitializeComponent();

            this.ClientSize = new Size(xScreen, yScreen);
            this.DoubleBuffered = true; // 깜박임 방지
            CreateData(); // 데이터 생성
            Timer timer = new Timer { Interval = 20 };
            timer.Tick += (s, e) => { UpdateFrame(); this.Invalidate(); };
            timer.Start(); // 타이머 시작
        }

        // 화면 X좌표 변환 함수
        private double ScreenX(double x)
        {
            return x + xScreen / 2;
        }

        // 화면 Y좌표 변환 함수
        private double ScreenY(double y)
        {
            return -y + yScreen / 2;
        }

        // x1에서 x2 사이의 난수를 생성하는 함수
        private int CreateRandom(int x1, int x2)
        {
            return random.Next(x1, x2 + 1);
        }

        // 하트가 확장할 때의 전체 데이터를 생성하고 20장의 이미지로 그리기
        private void CreateData()
        {
            int index = 0;

            // 인접 좌표 정보를 저장하여 거리 계산에 사용
            double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            for (double radian = 0.1; radian <= 2 * PI; radian += 0.005)
            {
                // 하트 모양의 좌표 계산
                x2 = 16 * Math.Pow(Math.Sin(radian), 3);
                y2 = 13 * Math.Cos(radian) - 5 * Math.Cos(2 * radian) - 2 * Math.Cos(3 * radian) - Math.Cos(4 * radian);

                // 두 점 사이의 거리 계산
                double distance = Math.Sqrt(Math.Pow(x2 - x1, 2) + Math.Pow(y2 - y1, 2));

                // 두 점 사이의 거리가 평균 거리보다 클 때만 점을 저장
                if (distance > averag_distance)
                {
                    // x1과 y1에 현재 데이터를 유지
                    x1 = x2; y1 = y2;
                    origin_points[index].X = x2;
                    origin_points[index++].Y = y2;
                }
            }

            index = 0;
            for (double size = 0.1; size <= 20; size += 0.1)
            {
                // 시그모이드 함수를 사용하여 현재 계수의 성공 확률 계산
                double success_p = 1 / (1 + Math.Pow(e, 8 - size / 2));

                // 모든 원시 데이터 순회
                for (int i = 0; i < quantity; ++i)
                {
                    // 확률에 따라 필터링
                    if (success_p > CreateRandom(0, 100) / 100.0)
                    {
                        // 색상 배열에서 무작위로 색상 선택
                        points[index].Color = colors[CreateRandom(0, 6)];

                        // 원시 데이터에 계수를 곱하여 points에 저장
                        points[index].X = size * origin_points[i].X + CreateRandom(-4, 4);
                        points[index++].Y = size * origin_points[i].Y + CreateRandom(-4, 4);
                    }
                }
            }

            // index 현재 값은 points에 저장된 구조체의 수
            int points_size = index;

            for (int frame = 0; frame < frames; ++frame)
            {
                // 각 이미지의 너비 xScreen, 높이 yScreen으로 초기화
                images[frame] = new Bitmap(xScreen, yScreen);

                // frame번째 이미지를 현재 작업 이미지로 설정
                using (Graphics g = Graphics.FromImage(images[frame]))
                {
                    g.Clear(Color.Black);

                    // 하트 모양의 점들을 프레임마다 이동
                    for (index = 0; index < points_size; ++index)
                    {
                        double x = points[index].X, y = points[index].Y; // 현재 값을 x와 y에 할당
                        double distance = Math.Sqrt(Math.Pow(x, 2) + Math.Pow(y, 2)); // 현재 점과 원점 사이의 거리 계산
                        double distance_increase = -0.0009 * distance * distance + 0.35714 * distance + 5; // 현재 거리를 방정식에 대입하여 해당 점의 증가 거리 계산

                        // 증가 거리에 따라 x축 방향의 증가 거리 계산
                        double x_increase = distance_increase * x / distance / frames;

                        // 증가 거리에 따라 y축 방향의 증가 거리 계산
                        double y_increase = distance_increase * y / distance / frames;

                        // 새로운 데이터로 기존 데이터를 덮어씀
                        points[index].X += x_increase;
                        points[index].Y += y_increase;

                        // 현재 점의 색상을 추출하여 그리기 색상 설정
                        using (SolidBrush brush = new SolidBrush(points[index].Color))
                        {
                            // 수학 좌표를 화면 좌표로 변환하여 원을 그림
                            g.FillEllipse(brush, (float)ScreenX(points[index].X), (float)ScreenY(points[index].Y), 1, 1);
                        }
                    }

                    // 외부 깜빡이는 점 생성
                    for (double size = 17; size < 23; size += 0.3)
                    {
                        for (index = 0; index < quantity; ++index)
                        {
                            // 계수가 20 이상일 때 40% 확률, 20 미만일 때 5% 확률로 필터링
                            if ((CreateRandom(0, 100) / 100.0 > 0.6 && size >= 20) || (size < 20 && CreateRandom(0, 100) / 100.0 > 0.95))
                            {
                                double x, y;
                                if (size >= 20)
                                {
                                    // frame의 제곱의 양수 및 음수 값을 상한 및 하한으로 하여 무작위 수 생성
                                    x = origin_points[index].X * size + CreateRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                                    y = origin_points[index].Y * size + CreateRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                                }
                                else
                                {
                                    // 계수가 20 미만일 때의 처리는 하트 점과 동일
                                    x = origin_points[index].X * size + CreateRandom(-5, 5);
                                    y = origin_points[index].Y * size + CreateRandom(-5, 5);
                                }

                                // 무작위 색상을 선택하여 현재 그리기 색상으로 설정
                                using (SolidBrush brush = new SolidBrush(colors[CreateRandom(0, 6)]))
                                {
                                    // 수학 좌표를 화면 좌표로 변환하여 원을 그림
                                    g.FillEllipse(brush, (float)ScreenX(x), (float)ScreenY(y), 1, 1);
                                    // 이러한 점은 이전 프레임의 좌표 데이터가 필요하지 않으므로 저장하지 않음
                                }
                            }
                        }
                    }
                }
            }
        }

        // 프레임 업데이트 함수
        private void UpdateFrame()
        {
            // 확장 및 축소 효과를 위해 프레임 인덱스를 조정
            if (extend)
            {
                // 확장 시 프레임 증가
                if (currentFrame == frames - 1)
                {
                    extend = false;
                }
                else
                {
                    currentFrame++;
                }
            }
            else
            {
                // 축소 시 프레임 감소
                if (currentFrame == 0)
                {
                    extend = true;
                }
                else
                {
                    currentFrame--;
                }
            }
        }

        // 그리기 함수
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            e.Graphics.DrawImage(images[currentFrame], 0, 0); // 현재의 이미지를 표시
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.Text = "Hear pumping"; // Form의 Caption 수정
        }
    }
}
