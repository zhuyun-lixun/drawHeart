[01] 소스코드 설명

C언어 원본소스코드 출처 : https://www.bilibili.com/read/cv20048480/

C언어 소스코드를 컨버팅한 CSharp 소스코드를 컨버팅 했음.

chatGPT 의 많은 도움으로 컨버팅 했음.


[02] 개발툴 : embarcadero dev c++

https://sourceforge.net/projects/embarcadero-devcpp/

[03] 컴파일 결과물













======아래 사항은 일단 보류===================

[04]. 라이브러리 : SDL2 

https://github.com/libsdl-org/SDL/releases/tag/release-2.0.9

에서 SDL2-devel-2.0.9-mingw.tar.gz 파일 다운로드

[05]. SDL2 환경 세팅

1. 압축풀어서 SDL2-2.0.9 폴더를 C드라이브에 복사
   
2. DevC++ 실행해서 C++ 빈 프로젝트 생성 ( drawHeart 프로젝트명)

3. 도구->컴파일러 설정-> 디렉토리 -> C++ includes 탭

(Tools->Compiler Options->Directories->C++ includes 탭)

아래 폴더 추가

C:\SDL2-2.0.9\i686-w64-mingw32\include\SDL2

4. 라이브러리 탭(Libraries tab)에도 아래 폴더 추가

C:\SDL2-2.0.9\i686-w64-mingw32\lib

5. 프로젝트 옵션 설정

6. 프로젝트->프로젝트 옵션->매개변수들->LInker 에 아래 내용 추가

(Project->Project options->Parameters Tab->Linker)

-lmingw32 -lSDL2main -lSDL2

7. 파일/디렉토리 탭(Directories Tab)에서 Include Directories 에 아래폴더 추가

C:\SDL2-2.0.9\i686-w64-mingw32\include\SDL2

8. 파일/디렉토리 탭(Directories Tab)에서 Library Directories에 아래 폴더 추가

C:\SDL2-2.0.9\i686-w64-mingw32\lib

9. 실행을 위한 DLL 파일 복사

C:\SDL2-2.0.9\i686-w64-mingw32\bin\SDL2.dll 파일을 복사해서, 프로젝트 실행폴더에 복붙 하면 준비 끝.

10. 코드 실행해보기

아래 참고 사이트 에 소스코드 있음.

SDL2 사용법 참고 : https://blog.naver.com/tepet/221406851288

* Dev-cpp 에서 SDL2 사용하는방법은 아직 잘 안되는 상황임.

