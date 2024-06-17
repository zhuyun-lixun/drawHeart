import pygame
import random
import math

# 색상 배열 초기화
colors = [(255, min(32 + 204, 255), 83),
          (252, min(222 + 204, 255), 250),
          (255, min(0 + 153, 255), min(0 + 204, 255)),
          (255, min(0 + 153, 255), min(0 + 204, 255)),
          (255, 2, min(2 + 204, 255)),
          (255, min(0 + 153, 255), min(8 + 204, 255)),
          (255, 5, min(5 + 204, 255))]

# 상수 정의
xScreen = 1200
yScreen = 800
PI = 3.1426535159
e = 2.71828
average_distance = 0.162
quantity = 506
circles = 210
frames = 20

# 데이터 배열 초기화
origin_points = []
points = []
images = []

# pygame 초기화
pygame.init()
screen = pygame.display.set_mode((xScreen, yScreen))
clock = pygame.time.Clock()

def screen_x(x):
    return x + xScreen / 2

def screen_y(y):
    return -y + yScreen / 2

def create_random(x1, x2):
    return random.randint(x1, x2)

# 데이터 생성 함수
def create_data():
    x1 = 0
    y1 = 0

    for radian in [x * 0.005 for x in range(1, int(2 * PI / 0.005))]:
        # 하트 모양의 좌표 계산
        x2 = 16 * math.sin(radian)**3
        y2 = 13 * math.cos(radian) - 5 * math.cos(2 * radian) - 2 * math.cos(3 * radian) - math.cos(4 * radian)
        distance = math.sqrt((x2 - x1)**2 + (y2 - y1)**2)

        if distance > average_distance:
            x1 = x2
            y1 = y2
            origin_points.append((x2, y2))

    for size in [x * 0.1 for x in range(1, int(20 / 0.1))]:
        success_p = 1 / (1 + math.exp(8 - size / 2))

        for i in range(quantity):
            if success_p > create_random(0, 100) / 100.0:
                color = random.choice(colors)
                x = size * origin_points[i][0] + create_random(-4, 4)
                y = size * origin_points[i][1] + create_random(-4, 4)
                points.append([x, y, color])

    points_size = len(points)

    for frame in range(frames):
        image = pygame.Surface((xScreen, yScreen))
        image.fill((0, 0, 0))

        for index in range(points_size):
            x, y, color = points[index]
            distance = math.sqrt(x**2 + y**2)
            distance_increase = -0.0009 * distance**2 + 0.35714 * distance + 5

            x_increase = distance_increase * x / distance / frames
            y_increase = distance_increase * y / distance / frames

            points[index][0] += x_increase
            points[index][1] += y_increase

            pygame.draw.ellipse(image, color, (screen_x(points[index][0]), screen_y(points[index][1]), 1, 1))

        for size in [x * 0.3 + 17 for x in range(20)]:
            for index in range(quantity):
                if ((create_random(0, 100) / 100.0 > 0.6 and size >= 20) or (size < 20 and create_random(0, 100) / 100.0 > 0.95)):
                    if size >= 20:
                        x = origin_points[index][0] * size + create_random(-frame**2 // 5 - 15, frame**2 // 5 + 15)
                        y = origin_points[index][1] * size + create_random(-frame**2 // 5 - 15, frame**2 // 5 + 15)
                    else:
                        x = origin_points[index][0] * size + create_random(-5, 5)
                        y = origin_points[index][1] * size + create_random(-5, 5)

                    color = random.choice(colors)
                    pygame.draw.ellipse(image, color, (screen_x(x), screen_y(y), 1, 1))

        images.append(image)

create_data()

currentFrame = 0
extend = True

# 메인 루프
running = True
while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    if extend:
        if currentFrame == frames - 1:
            extend = False
        else:
            currentFrame += 1
    else:
        if currentFrame == 0:
            extend = True
        else:
            currentFrame -= 1

    screen.blit(images[currentFrame], (0, 0))
    pygame.display.flip()
    clock.tick(50)

pygame.quit()
