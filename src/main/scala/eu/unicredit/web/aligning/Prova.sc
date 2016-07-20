case class Pair(value:String, index: Int)
val col1 = List(Pair("b",0), Pair("f",1), Pair("g",2), Pair("h",4), Pair("c",7))
val matched = List(Pair("b",0), Pair("c",3))

def f(acc:List[List[Pair]], el:Pair): List[List[Pair]] = {

  val lastList: List[Pair] = acc.last
  if (lastList.isEmpty)
    List(List(el))
  else {
    val lastEl = lastList.last


    if (lastEl.index == el.index - 1) {
      val headAcc: List[List[Pair]] = acc.dropRight(1)
      val aggregateList = lastList :+ el
      val res = headAcc :+ aggregateList
      println(s"${lastEl.index} ${el.index} consecutive")
      res
    }
    else {
      val res = acc :+ List(el)
      println(s"${lastEl.index} ${el.index} not consecutive")
      res
    }
  }
}


val res = col1.foldLeft(List(List.empty[Pair])){(acc, el) => f(acc, el)}
res.foreach(println(_))