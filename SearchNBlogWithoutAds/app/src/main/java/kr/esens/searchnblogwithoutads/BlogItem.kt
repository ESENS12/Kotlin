package kr.esens.searchnblogwithoutads

class BlogItem() {
    var BlogUrl: String = "";   //블로그 url
    var BlogImages: ArrayList<String>? = null; //해당 블로그의 이미지 태그를 담고있는 리스트
    var PostTitle: String = "";    //포스팅 제목
    var bIsFakeBlog: Boolean = false; //광고 여부
}