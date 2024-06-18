//#include <SDL2/SDL.h>
#include <SDL.h>
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <vector>
#include <random>

// 상수 정의
const int xScreen = 1200;
const int yScreen = 800;
const double PI = 3.1426535159;
const double e = 2.71828;
const double averag_distance = 0.162;
const int quantity = 506;
const int circles = 210;
const int frames = 20;

struct MyPoint {
    double X, Y;
    SDL_Color Color;
};

// 색상 배열 초기화
SDL_Color colors[] = {
    {255, std::min(32 + 204, 255), 83, 255},
    {252, std::min(222 + 204, 255), 250, 255},
    {255, std::min(0 + 153, 255), std::min(0 + 204, 255), 255},
    {255, std::min(0 + 153, 255), std::min(0 + 204, 255), 255},
    {255, 2, std::min(2 + 204, 255), 255},
    {255, std::min(0 + 153, 255), std::min(8 + 204, 255), 255},
    {255, 5, std::min(5 + 204, 255), 255}
};

std::vector<MyPoint> origin_points(quantity);
std::vector<MyPoint> points(circles* quantity);
std::vector<SDL_Texture*> images(frames);

int currentFrame = 0;
bool extend = true;
std::mt19937 randomGenerator(time(nullptr));

// 화면 X좌표 변환 함수
double ScreenX(double x) {
    return x + xScreen / 2;
}

// 화면 Y좌표 변환 함수
double ScreenY(double y) {
    return -y + yScreen / 2;
}

// x1에서 x2 사이의 난수를 생성하는 함수
int CreateRandom(int x1, int x2) {
    std::uniform_int_distribution<int> distribution(x1, x2);
    return distribution(randomGenerator);
}

// 하트가 확장할 때의 전체 데이터를 생성하고 20장의 이미지로 그리기
void CreateData(SDL_Renderer* renderer) {
    int index = 0;
    double x1 = 0, y1 = 0, x2 = 0, y2 = 0;

    for (double radian = 0.1; radian <= 2 * PI; radian += 0.005) {
        x2 = 16 * std::pow(std::sin(radian), 3);
        y2 = 13 * std::cos(radian) - 5 * std::cos(2 * radian) - 2 * std::cos(3 * radian) - std::cos(4 * radian);
        double distance = std::sqrt(std::pow(x2 - x1, 2) + std::pow(y2 - y1, 2));

        if (distance > averag_distance) {
            x1 = x2; y1 = y2;
            origin_points[index].X = x2;
            origin_points[index++].Y = y2;
        }
    }

    index = 0;
    for (double size = 0.1; size <= 20; size += 0.1) {
        double success_p = 1 / (1 + std::pow(e, 8 - size / 2));

        for (int i = 0; i < quantity; ++i) {
            if (success_p > CreateRandom(0, 100) / 100.0) {
                points[index].Color = colors[CreateRandom(0, 6)];
                points[index].X = size * origin_points[i].X + CreateRandom(-4, 4);
                points[index++].Y = size * origin_points[i].Y + CreateRandom(-4, 4);
            }
        }
    }

    int points_size = index;

    for (int frame = 0; frame < frames; ++frame) {
        SDL_Surface* surface = SDL_CreateRGBSurface(0, xScreen, yScreen, 32, 0, 0, 0, 0);
        SDL_FillRect(surface, NULL, SDL_MapRGB(surface->format, 0, 0, 0));

        for (index = 0; index < points_size; ++index) {
            double x = points[index].X, y = points[index].Y;
            double distance = std::sqrt(std::pow(x, 2) + std::pow(y, 2));
            double distance_increase = -0.0009 * distance * distance + 0.35714 * distance + 5;
            double x_increase = distance_increase * x / distance / frames;
            double y_increase = distance_increase * y / distance / frames;

            points[index].X += x_increase;
            points[index].Y += y_increase;

            SDL_Rect rect = { static_cast<int>(ScreenX(points[index].X)), static_cast<int>(ScreenY(points[index].Y)), 1, 1 };
            SDL_FillRect(surface, &rect, SDL_MapRGB(surface->format, points[index].Color.r, points[index].Color.g, points[index].Color.b));
        }

        for (double size = 17; size < 23; size += 0.3) {
            for (index = 0; index < quantity; ++index) {
                if ((CreateRandom(0, 100) / 100.0 > 0.6 && size >= 20) || (size < 20 && CreateRandom(0, 100) / 100.0 > 0.95)) {
                    double x, y;
                    if (size >= 20) {
                        x = origin_points[index].X * size + CreateRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                        y = origin_points[index].Y * size + CreateRandom(-frame * frame / 5 - 15, frame * frame / 5 + 15);
                    }
                    else {
                        x = origin_points[index].X * size + CreateRandom(-5, 5);
                        y = origin_points[index].Y * size + CreateRandom(-5, 5);
                    }

                    SDL_Rect rect = { static_cast<int>(ScreenX(x)), static_cast<int>(ScreenY(y)), 1, 1 };
                    SDL_Color color = colors[CreateRandom(0, 6)];
                    SDL_FillRect(surface, &rect, SDL_MapRGB(surface->format, color.r, color.g, color.b));
                }
            }
        }

        images[frame] = SDL_CreateTextureFromSurface(renderer, surface);
        SDL_FreeSurface(surface);
    }
}

// 프레임 업데이트 함수
void UpdateFrame() {
    if (extend) {
        if (currentFrame == frames - 1) {
            extend = false;
        }
        else {
            currentFrame++;
        }
    }
    else {
        if (currentFrame == 0) {
            extend = true;
        }
        else {
            currentFrame--;
        }
    }
}

int main(int argc, char* argv[]) {
    SDL_Init(SDL_INIT_VIDEO);
    SDL_Window* window = SDL_CreateWindow("Heart Pumping", SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED, xScreen, yScreen, SDL_WINDOW_SHOWN);
    SDL_Renderer* renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED);

    CreateData(renderer);

    bool running = true;
    SDL_Event event;
    Uint32 start_time;

    while (running) {
        start_time = SDL_GetTicks();

        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT) {
                running = false;
            }
        }

        SDL_RenderClear(renderer);
        SDL_RenderCopy(renderer, images[currentFrame], NULL, NULL);
        SDL_RenderPresent(renderer);

        UpdateFrame();

        Uint32 frame_time = SDL_GetTicks() - start_time;
        if (frame_time < 20) {
            SDL_Delay(20 - frame_time);
        }
    }

    for (auto texture : images) {
        SDL_DestroyTexture(texture);
    }

    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();

    return 0;
}
