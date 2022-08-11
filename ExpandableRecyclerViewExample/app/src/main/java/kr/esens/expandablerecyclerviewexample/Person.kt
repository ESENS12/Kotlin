package kr.esens.expandablerecyclerviewexample

data class Person(
    var name: String = "",
    var description: String = "",
    var isExpanded: Boolean = false,
    var image: Int = -1
)