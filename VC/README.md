[01] 준비물
1. Visual Studio Community 2019 버전 ( 에서 VC++ 윈도우즈 데스크탑 관련 체크해서 설치)
다운로드 파일 : https://visualstudio.microsoft.com/thank-you-downloading-visual-studio/?sku=Community&rel=16   

2. SDL2 라이브러리 설치 (최신 버전으로 도 가능할듯)
출처 : https://wikidocs.net/194523
SDL 프로그램 컴파일 및 링크

여기서는 SDL 라이브러리 자체를 빌드하는 방법은 생략하고 미리 컴파일된 바이너리를 활용하겠다.2

    https://github.com/libsdl-org/SDL/releases/tag/release-2.26.4 에서 SDL2-devel-2.26.4-VC.zip 파일을 다운받는다.
    새 Win32 콘솔 응용 프로그램 프로젝트를 만든다.
    “Include Directories” 필드에 SDL2 개발 라이브러리의 “include” 폴더 경로를 추가한다.
    “Library Directories” 필드에 SDL2 개발 라이브러리의 “lib” 폴더 경로를 추가한다.
    프로젝트 > 속성 > 구성 속성 > 링커 > 입력으로 이동하여 SDL2 라이브러리를 추가한다. 추가 종속성 필드에서 SDL2.lib, SDL2main.lib 라이브러리를 추가한다.
    SDL 프로그램을 작성한다. 테스트 코드로 03-01절 내용을 활용하자.
    빌드 > 솔루션 빌드를 선택해서 프로그램을 컴파일하고 링크한다.
    F5키를 눌러서 프로그램을 실행하고 정상적으로 창이 생성되는지 확인한다.

여기서는 SDL 라이브러리만을 프로젝트에 링크하는 방법만 설명했다. 다른 SDL 확장 라이브러리도 위와 같은 절차를 따르면 프로젝트에 포함시킬 수 있다. 

3. 주의사항
SDL2 라이브러리를 사용했으면 컴파일된 실행파일과 같은 폴더에 SDL2.dll 파일이 있어야 정상적으로 실행됨.

[02] 컴파일 결과물
