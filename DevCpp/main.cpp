#include <windows.h>
#include <cmath>
#include <vector>
#include <cstdlib>
#include <ctime>

#define PI 3.14159265358979323846
#define e 2.71828
#define averag_distance 0.162
#define quantity 506
#define circles 210
#define frames 20

struct TMyPoint {
    double X, Y;
    COLORREF Color;
};

LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam);
void CreateData();
void DrawHeart(HDC hdc, int frame);

std::vector<TMyPoint> origin_points;
std::vector<TMyPoint> points;
std::vector<HBITMAP> images;
int currentFrame = 0;
bool extend = true;
COLORREF colors[7] = {
    RGB(255, 236, 83), 
    RGB(252, 255, 250), 
    RGB(255, 153, 204), 
    RGB(255, 153, 204), 
    RGB(255, 2, 206), 
    RGB(255, 153, 212), 
    RGB(255, 5, 209)
};

int APIENTRY WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
    LPCSTR CLASS_NAME = "HeartAnimationClass"; 
    
    WNDCLASS wc = { };
    wc.lpfnWndProc   = WindowProc;
    wc.hInstance     = hInstance;
    wc.lpszClassName = CLASS_NAME;
    wc.hCursor       = LoadCursor(NULL, IDC_ARROW);
    wc.hbrBackground = (HBRUSH)GetStockObject(BLACK_BRUSH);//(HBRUSH)(COLOR_WINDOW + 1);
    
    if (!RegisterClass(&wc))
    {
        return 0;
    }
    
    HWND hwnd = CreateWindowEx(
        0,
        CLASS_NAME,
        "Heart Animation",
        WS_OVERLAPPEDWINDOW,
        CW_USEDEFAULT, CW_USEDEFAULT, 1200, 800,
        NULL,
        NULL,
        hInstance,
        NULL
    );
    
    if (hwnd == NULL)
    {
        return 0;
    }
    
    ShowWindow(hwnd, nCmdShow);
    
    CreateData();
    
    MSG msg = { };
    while (GetMessage(&msg, NULL, 0, 0))
    {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
    
    return 0;
}

void CreateData()
{
    origin_points.resize(quantity);
    points.resize(circles * quantity);
    images.resize(frames);

    double x1 = 0, y1 = 0;
    double radian = 0.1;
    int index = 0;
    
    while (radian <= 2 * PI)
    {
        double x2 = 16 * pow(sin(radian), 3);
        double y2 = 13 * cos(radian) - 5 * cos(2 * radian) - 2 * cos(3 * radian) - cos(4 * radian);
        double distance = sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2));

        if (distance > averag_distance)
        {
            x1 = x2;
            y1 = y2;
            origin_points[index].X = x2;
            origin_points[index].Y = y2;
            index++;
        }

        radian += 0.005;//0.1;
    }

    index = 0;
    double size = 0.1;
    while (size <= 20)
    {
        double success_p = 1 / (1 + pow(e, 8 - size / 2));

        for (int i = 0; i < quantity; i++)
        {
            if (success_p > (rand() % 100) / 100.0)
            {
                points[index].Color = colors[rand() % 7];
                points[index].X = size * origin_points[i].X + (rand() % 9 - 4);
                points[index].Y = size * origin_points[i].Y + (rand() % 9 - 4);
                index++;
            }
        }

        size += 0.1;
    }

    for (int frame = 0; frame < frames; frame++)
    {
        HDC hdc = GetDC(NULL);
        HDC hdcMem = CreateCompatibleDC(hdc);
        HBITMAP bmp = CreateCompatibleBitmap(hdc, 1200, 800);
        SelectObject(hdcMem, bmp);
        
        RECT rect = { 0, 0, 1200, 800 };
        //FillRect(hdcMem, &rect, (HBRUSH)(COLOR_WINDOW + 1));
        FillRect(hdcMem, &rect, (HBRUSH)GetStockObject(BLACK_BRUSH)); // backgroud color black

        for (int i = 0; i < index; i++)
        {
            double x = points[i].X;
            double y = points[i].Y;
            double distance = sqrt(pow(x, 2) + pow(y, 2));

            if (distance != 0)
            {
                double distance_increase = -0.0009 * distance * distance + 0.35714 * distance + 5;
                double x_increase = distance_increase * x / distance / frames;
                double y_increase = distance_increase * y / distance / frames;

                points[i].X += x_increase;
                points[i].Y += y_increase;
            }

            SetPixel(hdcMem, 600 + (int)points[i].X, 400 - (int)points[i].Y, points[i].Color);
        }

        double size = 17;
        while (size <= 23)
        {
            for (int i = 0; i < quantity; i++)
            {
                if (((rand() % 100) / 100.0 > 0.6 && size >= 20) || (size < 20 && (rand() % 100) / 100.0 > 0.95))
                {
                    double x, y;
                    if (size >= 20)
                    {
                        x = origin_points[i].X * size + (rand() % (frame * frame / 5 + 31) - frame * frame / 5 - 15);
                        y = origin_points[i].Y * size + (rand() % (frame * frame / 5 + 31) - frame * frame / 5 - 15);
                    }
                    else
                    {
                        x = origin_points[i].X * size + (rand() % 11 - 5);
                        y = origin_points[i].Y * size + (rand() % 11 - 5);
                    }

                    SetPixel(hdcMem, 600 + (int)x, 400 - (int)y, colors[rand() % 7]);
                }
            }

            size += 0.3;
        }

        images[frame] = bmp;
        DeleteDC(hdcMem);
        ReleaseDC(NULL, hdc);
    }
}

void DrawHeart(HDC hdc, int frame)
{
    HDC hdcMem = CreateCompatibleDC(hdc);
    HBITMAP oldBmp = (HBITMAP)SelectObject(hdcMem, images[frame]);
    BitBlt(hdc, 0, 0, 1200, 800, hdcMem, 0, 0, SRCCOPY);
    SelectObject(hdcMem, oldBmp);
    DeleteDC(hdcMem);
}

LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
    switch (uMsg)
    {
    case WM_PAINT:
    {
        PAINTSTRUCT ps;
        HDC hdc = BeginPaint(hwnd, &ps);
        DrawHeart(hdc, currentFrame);
        EndPaint(hwnd, &ps);
    }
    return 0;

    case WM_TIMER:
        if (extend)
        {
            if (currentFrame == frames - 1)
                extend = false;
            else
                currentFrame++;
        }
        else
        {
            if (currentFrame == 0)
                extend = true;
            else
                currentFrame--;
        }
        InvalidateRect(hwnd, NULL, TRUE);
        return 0;

    case WM_DESTROY:
        KillTimer(hwnd, 1);
        PostQuitMessage(0);
        return 0;

    case WM_CREATE:
        SetTimer(hwnd, 1, 50, NULL);
        srand((unsigned int)time(NULL)); // Initialize random seed
        return 0;
    }
    return DefWindowProc(hwnd, uMsg, wParam, lParam);
}
