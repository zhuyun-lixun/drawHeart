import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class HeartAnimation extends JPanel {

    // 화면 너비와 높이
    private static final int SCREEN_WIDTH = 1200;
    private static final int SCREEN_HEIGHT = 800;

    // 원주율 정의
    private static final double PI = 3.1426535159;//Math.PI;

    // 원시 매개변수 방정식의 각 점 사이의 평균 거리
    private static final double AVERAGE_DISTANCE = 0.162;

    // 완전한 하트 모양에 필요한 점의 수
    private static final int HEART_POINTS = 506;

    // 하트 모양을 구성하는 하트의 개수
    private static final int HEART_CIRCLES = 210;

    // 하트가 확장하는 프레임 수
    private static final int ANIMATION_FRAMES = 20;

    // 하트의 점 구조체
    private static class MyPoint {
        public double x, y;
        public Color color;

        public MyPoint(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    // 색상 배열 초기화
    private final Color[] colors = {
            new Color(255, Math.min(32 + 204, 255), 83),
            new Color(252, Math.min(222 + 204, 255), 250),
            new Color(255, Math.min(0 + 153, 255), Math.min(0 + 204, 255)),
            new Color(255, Math.min(0 + 153, 255), Math.min(0 + 204, 255)),
            new Color(255, 2, Math.min(2 + 204, 255)),
            new Color(255, Math.min(0 + 153, 255), Math.min(8 + 204, 255)),
            new Color(255, 5, Math.min(5 + 204, 255))
    };

    // 데이터 배열 초기화
    private final MyPoint[] originPoints = new MyPoint[HEART_POINTS]; // 원시 하트 데이터를 저장하는 배열
    private final MyPoint[] points = new MyPoint[HEART_CIRCLES * HEART_POINTS]; // 모든 하트 데이터를 저장하는 배열
    private final Image[] images = new Image[ANIMATION_FRAMES]; // 이미지 배열

    // 애니메이션 상태 변수 초기화
    private int currentFrame = 0;
    private boolean extend = true;
    private final Random random = new Random(); // 랜덤 객체 생성

    public HeartAnimation() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        createData(); // 데이터 생성
        Timer timer = new Timer(20, e -> {
            updateFrame();
            repaint();
        });
        timer.start(); // 타이머 시작
    }

    // 화면 X 좌표 변환 함수
    private double screenX(double x) {
        return x + SCREEN_WIDTH / 2;
    }

    // 화면 Y 좌표 변환 함수
    private double screenY(double y) {
        return -y + SCREEN_HEIGHT / 2;
    }

    // x1에서 x2 사이의 난수 생성 함수
    private int createRandom(int x1, int x2) {
        return random.nextInt(x2 - x1 + 1) + x1;
    }

    // 하트가 확장할 때의 전체 데이터를 생성하고 20장의 이미지로 그리기
    private void createData() {
        int index = 0;

        // 인접 좌표 정보를 저장하여 거리 계산에 사용
        double x1 = 0, y1 = 0, x2, y2;
        for (double radian = 0.1; radian <= 2 * PI; radian += 0.005) {
            // 하트 모양의 좌표 계산
            x2 = 16 * Math.pow(Math.sin(radian), 3);
            y2 = 13 * Math.cos(radian) - 5 * Math.cos(2 * radian) - 2 * Math.cos(3 * radian) - Math.cos(4 * radian);

            // 두 점 사이의 거리 계산
            double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

            // 두 점 사이의 거리가 평균 거리보다 클 때만 점을 저장
            if (distance > AVERAGE_DISTANCE) {
                // x1과 y1에 현재 데이터를 유지
                x1 = x2;
                y1 = y2;
                originPoints[index] = new MyPoint(x2, y2, Color.BLACK); // 원시 데이터 저장
                index++;
            }
        }

        index = 0;
        for (double size = 0.1; size <= 20; size += 0.1) {
            // 시그모이드 함수를 사용하여 현재 계수의 성공 확률 계산
            double successP = 1 / (1 + Math.pow(Math.E, 8 - size / 2));

            // 모든 원시 데이터 순회
            for (int i = 0; i < HEART_POINTS; ++i) {
                // 확률에 따라 필터링
                if (successP > createRandom(0, 100) / 100.0) {
                    // 색상 배열에서 무작위로 색상 선택
                    Color randomColor = colors[createRandom(0, 6)];

                    // 원시 데이터에 계수를 곱하여 points에 저장
                    points[index] = new MyPoint(size * originPoints[i].x + createRandom(-4, 4),
                                                size * originPoints[i].y + createRandom(-4, 4),
                                                randomColor);
                    index++;
                }
            }
        }

        // index 현재 값은 points에 저장된 구조체의 수
        int pointsSize = index;

        for (int frame = 0; frame < ANIMATION_FRAMES; ++frame) {
            // 각 이미지의 너비 SCREEN_WIDTH, 높이 SCREEN_HEIGHT으로 초기화
            images[frame] = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);

            // frame번째 이미지를 현재 작업 이미지로 설정
            Graphics2D g2d = (Graphics2D) images[frame].getGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            // 하트 모양의 점들을 프레임마다 이동
            for (index = 0; index < pointsSize; ++index) {
                double x = points[index].x;
                double y = points[index].y;

                double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // 현재 점과 원점 사이의 거리 계산
                double distanceIncrease = -0.0009 * distance * distance + 0.35714 * distance + 5; // 현재 거리를 방정식에 대입하여 해당 점의 증가 거리 계산

                // 증가 거리에 따라 x축 방향의 증가 거리 계산
                double xIncrease = distanceIncrease * x / distance / ANIMATION_FRAMES;

                // 증가 거리에 따라 y축 방향의 증가 거리 계산
                double yIncrease = distanceIncrease * y / distance / ANIMATION_FRAMES;

                // 새로운 데이터로 기존 데이터를 덮어씀
                points[index].x += xIncrease;
                points[index].y += yIncrease;




                // 현재 점의 색상 설정
                g2d.setColor(points[index].color);

                // 수학 좌표를 화면 좌표로 변환하여 원을 그림
                g2d.fill(new Ellipse2D.Double(screenX(x), screenY(y), 1, 1));
            }

            // 외부 깜빡이는 점 생성
            for (double size = 17; size < 23; size += 0.3) {
                for (index = 0; index < HEART_POINTS; ++index) {
                    // 계수가 20 이상일 때 40% 확률, 20 미만일 때 
                    // 계수가 20 이상일 때 40% 확률, 20 미만일 때 5% 확률로 필터링
                    if ((createRandom(0, 100) / 100.0 > 0.6 && size >= 20) || (size < 20 && createRandom(0, 100) / 100.0 > 0.95)) {
                        double x, y;
                        if (size >= 20) {
                            // frame의 제곱의 양수 및 음수 값을 상한 및 하한으로 하여 무작위 수 생성
                            x = originPoints[index].x * size + createRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                            y = originPoints[index].y * size + createRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                        } else {
                            // 계수가 20 미만일 때의 처리는 하트 점과 동일
                            x = originPoints[index].x * size + createRandom(-5, 5);
                            y = originPoints[index].y * size + createRandom(-5, 5);
                        }

                        // 무작위 색상을 선택하여 현재 그리기 색상으로 설정
                        g2d.setColor(colors[createRandom(0, 6)]);

                        // 수학 좌표를 화면 좌표로 변환하여 원을 그림
                        g2d.fill(new Ellipse2D.Double(screenX(x), screenY(y), 1, 1));
                    }
                }
            }

            g2d.dispose();
        }
    }

    // 프레임 업데이트 함수
    private void updateFrame() {
        // 확장 및 축소 효과를 위해 프레임 인덱스를 조정
        if (extend) {
            // 확장 시 프레임 증가
            if (currentFrame == ANIMATION_FRAMES - 1) {
                extend = false;
            } else {
                currentFrame++;
            }
        } else {
            // 축소 시 프레임 감소
            if (currentFrame == 0) {
                extend = true;
            } else {
                currentFrame--;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 현재 프레임의 이미지를 화면에 그림
        g2d.drawImage(images[currentFrame], 0, 0, null);

        g2d.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Heart Animation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new HeartAnimation());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
