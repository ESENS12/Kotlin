# TOMTOM SDK EXAMPLE
 - A simple android basic map using tomtom sdk

# References
 - https://developer.tomtom.com/maps-android-sdk/documentation/product-information/introduction

# Notes
Key valid check url
https://api.tomtom.com/map/1/tile/basic/main/0/0/0.png?key=<YOUR_SDK_KEY>

빌드 오류

1. Annotation 관련

implementation "com.android.support:support-annotations:28.0.0"
App 레벨 build.gradle에 추가.

2. 다중 manifest 파일 병합 처리 필요
   AndroidManifest 에 tools:node="replace" 추가

3. 가장 어이없는 버전 문제 . 2.4.807 로 써놓고 안된다고 계속 오류 찾았는데 사실 2.4807 형태로 해야함
   implementation "com.tomtom.online:sdk-maps:2.4807"
   릴리즈 노트에 2.4.807 형태로 표기되어있음……
   https://developer.tomtom.com/maps-android-sdk/documentation/product-information/release-notes

4. AndroidX 관련 오류
   gradle.properties 에
   android.enableJetifier=true 추가.

5. 현재 map tile 못받아오는 문제 있음. android 21 저장소 문제인지 권한문제인지 확인 후 수정해야함 
