# MVVM Sample
- A Simple MVVM(ModelView-ViewModel) Architecture implement Sample
- ViewModel + LiveData + DataBinding

# References
- MVVM Sample(https://kangmin1012.tistory.com/39)

# Note
- ActivityMainBinding class는 라이브러리에서 제공하거나, 사용자가 작성하는 클래스는 아님.
- activity_main.xml내의 <layout>태그로 선언이 되었다면 자동으로 만들어지는 클래스.
- 즉 activity_sub.xml 파일 내에 <layout>태그가 있다면 ActivitySubBinding 클래스가 제공될것이다.