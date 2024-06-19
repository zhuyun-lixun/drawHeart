import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class HeartAnimation extends JPanel {

    // ȭ�� �ʺ�� ����
    private static final int SCREEN_WIDTH = 1200;
    private static final int SCREEN_HEIGHT = 800;

    // ������ ����
    private static final double PI = 3.1426535159;//Math.PI;

    // ���� �Ű����� �������� �� �� ������ ��� �Ÿ�
    private static final double AVERAGE_DISTANCE = 0.162;

    // ������ ��Ʈ ��翡 �ʿ��� ���� ��
    private static final int HEART_POINTS = 506;

    // ��Ʈ ����� �����ϴ� ��Ʈ�� ����
    private static final int HEART_CIRCLES = 210;

    // ��Ʈ�� Ȯ���ϴ� ������ ��
    private static final int ANIMATION_FRAMES = 20;

    // ��Ʈ�� �� ����ü
    private static class MyPoint {
        public double x, y;
        public Color color;

        public MyPoint(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    // ���� �迭 �ʱ�ȭ
    private final Color[] colors = {
            new Color(255, Math.min(32 + 204, 255), 83),
            new Color(252, Math.min(222 + 204, 255), 250),
            new Color(255, Math.min(0 + 153, 255), Math.min(0 + 204, 255)),
            new Color(255, Math.min(0 + 153, 255), Math.min(0 + 204, 255)),
            new Color(255, 2, Math.min(2 + 204, 255)),
            new Color(255, Math.min(0 + 153, 255), Math.min(8 + 204, 255)),
            new Color(255, 5, Math.min(5 + 204, 255))
    };

    // ������ �迭 �ʱ�ȭ
    private final MyPoint[] originPoints = new MyPoint[HEART_POINTS]; // ���� ��Ʈ �����͸� �����ϴ� �迭
    private final MyPoint[] points = new MyPoint[HEART_CIRCLES * HEART_POINTS]; // ��� ��Ʈ �����͸� �����ϴ� �迭
    private final Image[] images = new Image[ANIMATION_FRAMES]; // �̹��� �迭

    // �ִϸ��̼� ���� ���� �ʱ�ȭ
    private int currentFrame = 0;
    private boolean extend = true;
    private final Random random = new Random(); // ���� ��ü ����

    public HeartAnimation() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        createData(); // ������ ����
        Timer timer = new Timer(20, e -> {
            updateFrame();
            repaint();
        });
        timer.start(); // Ÿ�̸� ����
    }

    // ȭ�� X ��ǥ ��ȯ �Լ�
    private double screenX(double x) {
        return x + SCREEN_WIDTH / 2;
    }

    // ȭ�� Y ��ǥ ��ȯ �Լ�
    private double screenY(double y) {
        return -y + SCREEN_HEIGHT / 2;
    }

    // x1���� x2 ������ ���� ���� �Լ�
    private int createRandom(int x1, int x2) {
        return random.nextInt(x2 - x1 + 1) + x1;
    }

    // ��Ʈ�� Ȯ���� ���� ��ü �����͸� �����ϰ� 20���� �̹����� �׸���
    private void createData() {
        int index = 0;

        // ���� ��ǥ ������ �����Ͽ� �Ÿ� ��꿡 ���
        double x1 = 0, y1 = 0, x2, y2;
        for (double radian = 0.1; radian <= 2 * PI; radian += 0.005) {
            // ��Ʈ ����� ��ǥ ���
            x2 = 16 * Math.pow(Math.sin(radian), 3);
            y2 = 13 * Math.cos(radian) - 5 * Math.cos(2 * radian) - 2 * Math.cos(3 * radian) - Math.cos(4 * radian);

            // �� �� ������ �Ÿ� ���
            double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

            // �� �� ������ �Ÿ��� ��� �Ÿ����� Ŭ ���� ���� ����
            if (distance > AVERAGE_DISTANCE) {
                // x1�� y1�� ���� �����͸� ����
                x1 = x2;
                y1 = y2;
                originPoints[index] = new MyPoint(x2, y2, Color.BLACK); // ���� ������ ����
                index++;
            }
        }

        index = 0;
        for (double size = 0.1; size <= 20; size += 0.1) {
            // �ñ׸��̵� �Լ��� ����Ͽ� ���� ����� ���� Ȯ�� ���
            double successP = 1 / (1 + Math.pow(Math.E, 8 - size / 2));

            // ��� ���� ������ ��ȸ
            for (int i = 0; i < HEART_POINTS; ++i) {
                // Ȯ���� ���� ���͸�
                if (successP > createRandom(0, 100) / 100.0) {
                    // ���� �迭���� �������� ���� ����
                    Color randomColor = colors[createRandom(0, 6)];

                    // ���� �����Ϳ� ����� ���Ͽ� points�� ����
                    points[index] = new MyPoint(size * originPoints[i].x + createRandom(-4, 4),
                                                size * originPoints[i].y + createRandom(-4, 4),
                                                randomColor);
                    index++;
                }
            }
        }

        // index ���� ���� points�� ����� ����ü�� ��
        int pointsSize = index;

        for (int frame = 0; frame < ANIMATION_FRAMES; ++frame) {
            // �� �̹����� �ʺ� SCREEN_WIDTH, ���� SCREEN_HEIGHT���� �ʱ�ȭ
            images[frame] = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);

            // frame��° �̹����� ���� �۾� �̹����� ����
            Graphics2D g2d = (Graphics2D) images[frame].getGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            // ��Ʈ ����� ������ �����Ӹ��� �̵�
            for (index = 0; index < pointsSize; ++index) {
                double x = points[index].x;
                double y = points[index].y;

                double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // ���� ���� ���� ������ �Ÿ� ���
                double distanceIncrease = -0.0009 * distance * distance + 0.35714 * distance + 5; // ���� �Ÿ��� �����Ŀ� �����Ͽ� �ش� ���� ���� �Ÿ� ���

                // ���� �Ÿ��� ���� x�� ������ ���� �Ÿ� ���
                double xIncrease = distanceIncrease * x / distance / ANIMATION_FRAMES;

                // ���� �Ÿ��� ���� y�� ������ ���� �Ÿ� ���
                double yIncrease = distanceIncrease * y / distance / ANIMATION_FRAMES;

                // ���ο� �����ͷ� ���� �����͸� ���
                points[index].x += xIncrease;
                points[index].y += yIncrease;




                // ���� ���� ���� ����
                g2d.setColor(points[index].color);

                // ���� ��ǥ�� ȭ�� ��ǥ�� ��ȯ�Ͽ� ���� �׸�
                g2d.fill(new Ellipse2D.Double(screenX(x), screenY(y), 1, 1));
            }

            // �ܺ� �����̴� �� ����
            for (double size = 17; size < 23; size += 0.3) {
                for (index = 0; index < HEART_POINTS; ++index) {
                    // ����� 20 �̻��� �� 40% Ȯ��, 20 �̸��� �� 
                    // ����� 20 �̻��� �� 40% Ȯ��, 20 �̸��� �� 5% Ȯ���� ���͸�
                    if ((createRandom(0, 100) / 100.0 > 0.6 && size >= 20) || (size < 20 && createRandom(0, 100) / 100.0 > 0.95)) {
                        double x, y;
                        if (size >= 20) {
                            // frame�� ������ ��� �� ���� ���� ���� �� �������� �Ͽ� ������ �� ����
                            x = originPoints[index].x * size + createRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                            y = originPoints[index].y * size + createRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                        } else {
                            // ����� 20 �̸��� ���� ó���� ��Ʈ ���� ����
                            x = originPoints[index].x * size + createRandom(-5, 5);
                            y = originPoints[index].y * size + createRandom(-5, 5);
                        }

                        // ������ ������ �����Ͽ� ���� �׸��� �������� ����
                        g2d.setColor(colors[createRandom(0, 6)]);

                        // ���� ��ǥ�� ȭ�� ��ǥ�� ��ȯ�Ͽ� ���� �׸�
                        g2d.fill(new Ellipse2D.Double(screenX(x), screenY(y), 1, 1));
                    }
                }
            }

            g2d.dispose();
        }
    }

    // ������ ������Ʈ �Լ�
    private void updateFrame() {
        // Ȯ�� �� ��� ȿ���� ���� ������ �ε����� ����
        if (extend) {
            // Ȯ�� �� ������ ����
            if (currentFrame == ANIMATION_FRAMES - 1) {
                extend = false;
            } else {
                currentFrame++;
            }
        } else {
            // ��� �� ������ ����
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

        // ���� �������� �̹����� ȭ�鿡 �׸�
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
