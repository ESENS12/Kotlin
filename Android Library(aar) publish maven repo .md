Android Library(aar) maven 배포하기

 - private gitlab repository 에서 gradle(maven-publish) plugin을 사용해서 aar 배포 
	-> 즉 소스코드 공개 혹은 별도 저장소 생성 없이 작업하던 프로젝트의 build.gradle 수정만으로 라이브러리 배포 가능

 - 애초에 jitpack이 간단해서 jitpack을 검토하였으나 openSource project만 무료로 가능함..
  -> 꼼수로 lib만 포함하는 project를 github에 오픈소스로 공개후 배포해 보았더니 배포는 성공했으나 다운받으려고 보니 jitpack에서 내려줄때 aar을 포함하는 library는 불가능하다고 오류를 뱉어냄
 
 - 하여 gitlab 저장소를 maven 저장소처럼 사용한다면, private 저장소이므로 소스코드 공개 및 별도의 배포용 프로젝트 작업환경 구축 없이 간단하게 가능 
  (물론 github도 가능하겠지만 회사에서 작업용으로 쓰는게 gitlab이라.. 또다시 github용 private repo를 만들수는 없는 노릇이므로)
 
 - 상당히 간단한데 생각보다 많이 헤맸다. 설명이 이랬다 저랬다 하도 거창한것들이 많아서 ..

 # 작업 프로세스
  1. gitlab에서 해당 프로젝트(혹은 그룹) 진입 후 좌측 패널 Settings(본인이 Maintainer 권한 이상이어야 함) -> Repository -> Deploy Tokens 에 들어가서 토큰 발행
   -> name은 추후 관리 할때 어느 용도였는지 판단하기 위함이며 unique해야 하고 Expiration date, Username 등은 optional 하므로 아무렇게나 해도 상관은 없음
   -> 생성 되면 password 항목에 토큰이 발행되는데 해당 페이지를 벗어나면 다시는 확인할 방법이 없으므로 반드시 따로 적어두자
   -> Scopes는 read_package_registry, write_pacakge_registry를 주면 말그대로 읽고 쓰기가 가능하며 배포자는 반드시 write 권한이 있어야함

  2. 1번에서 발행한 토큰을 잘 저장해두고, 배포해야하는 module(library) 프로젝트로 돌아가 app/build.gradle 을 수정해주자
   -> 간단하게만 하자면, apply plugin: 'maven-publish' 추가 후 아무곳에나 
```
   afterEvaluate {
    publishing {
        repositories {
            maven {
                url "https://${깃랩주소}/api/v4/projects/${프로젝트 번호}/packages/maven"
                // e.g) https://gitlab.sjlee.kr/api/v4/projects/263/pacakges/maven
                name "GitLab" // 의미 없음. 아무값이나
                credentials(HttpHeaderCredentials) {
                    name = 'Deploy-Token' // 토큰 이름 따위를 넣는게 아니라 그냥 Deploy-Token 그대로 넣어줘야함
                    value = '아까 gitlab에서 생성했던 토큰값'
                }
                authentication {
                    header(HttpHeaderAuthentication)
                }
            }
        }
        publications {
            deploy(MavenPublication) {
                groupId '배포.할.패키지명' // e.g ) kr.esens.sample
                artifactId '아티팩트 id' // e.g) libexample
                version '0.0.1' // e.g) 버전 , 이 셋을 조합하면 결과물은 'kr.esens.sample:libexample:0.0.1.aar' 이렇게 된다.
                artifact("$buildDir/outputs/aar/app-release.aar")	// 빌드된 aar이 생성되는 위치와 aar 파일명

                pom.withXml {
                    def dependenciesNode = asNode().getAt('dependencies')[0] ?: asNode().appendNode('dependencies')
                    // 여기서 만약 implementation을 찾을 수 없다고 오류가 발생하면 build.gradle 위치를 잘못 찾아왔을 확률이 높음. 
                    // 실제 해당 라이브러리의 모듈에서 dependencies를 넣어주는 gradle에서 작업
                    // 또한 배포시에는 포함되지 않아도 되는 dependency는 api, library 등으로 사용하면
                    // 배포시 의존성 주입에는 빠지게 된다 (물론 아래와 같은 방법을 반복해서 추가로 넣어줘도 됨)
                    configurations.implementation.getDependencies().each{
                        if (it.name != 'unspecified') {
                            def dependencyNode = dependenciesNode.appendNode('dependency')
                            dependencyNode.appendNode('groupId', it.group)
                            dependencyNode.appendNode('artifactId', it.name)
                            dependencyNode.appendNode('version', it.version)
                        }
                    }
                }
            }
        }
    }
}
```

  해당 코드 추가 후 gradle publish 하면 배포 완료됨 .

 3. 해당 라이브러리를 사용 할 프로젝트의 build.gradle 수정
  -> 프로젝트 레벨의 build.gradle에 maven repo 정보를 추가해줘야함 . 
  ```
  allprojects{
  	repositories {
            maven {
                url "https://${깃랩주소}/api/v4/projects/${프로젝트 번호}/packages/maven"
                // e.g) https://gitlab.sjlee.kr/api/v4/projects/263/pacakges/maven
                name "GitLab" // 의미 없음. 아무값이나
                credentials(HttpHeaderCredentials) {
                    name = 'Deploy-Token' // 토큰 이름 따위를 넣는게 아니라 그냥 Deploy-Token 그대로 넣어줘야함
                    value = '아까 생성했던 토큰값'
                }
                authentication {
                    header(HttpHeaderAuthentication)
                }
            }
        }
	}

  ```
  위 처럼 넣어주고 (만약 이때 토큰값이 read,write 둘다 가능한 권한이면 문제가 될 수 있음, 읽기만 가능한 토큰을 발행 후 사용하는 곳에서는 해당 토큰만 사용하도록 하는게 중요) 빌드하면 끝!
