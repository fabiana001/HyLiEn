package eu.unicredit.web.hylien

import eu.unicredit.web.Models._
import scala.language.postfixOps

import scala.util.Random

private[this] object VisualListFinder {
  /**
   *
   * @param domNode
   * @param minsim
   * @param maxRecordTags
   * @return return a tuple with lists and non aligned nodes
   */
  def find(domNode: DomNode, minsim: Float, maxRecordTags: Int): (Seq[WebList], Seq[DomNode]) = {

    val verticalAligned: Map[Int, Seq[DomNode]] =
      VisualListFinder.verticallyAligned(domNode, maxRecordTags)

    val horizontalAligned: Map[Int, Seq[DomNode]] =
      VisualListFinder.horizontallyAligned(domNode, maxRecordTags)

    var notAligned = VisualListFinder.notAligned(domNode, verticalAligned, horizontalAligned)

    val verticalList = verticalAligned.map {
      case (pos, list) =>
        val (similar, nonSimilar) = VisualListFinder.structuralFilter(list, minsim)
        notAligned = notAligned ++ nonSimilar
        pos -> WebList(domNode, Orientation.vertical, domNode.location, domNode.size ,similar)
    }.filter(_._2.elements.size > 1).values

    val horizontalList = horizontalAligned.map {
      case (pos, list) =>
        val (similar, nonSimilar) = VisualListFinder.structuralFilter(list, minsim)
        notAligned = notAligned ++ nonSimilar
        pos -> WebList(domNode, Orientation.horizontal, domNode.location, domNode.size, similar)
    }.filter(_._2.elements.size > 1).values

    (verticalList ++ horizontalList toSeq, notAligned.toSeq)
  }

  /**
   *
   * @param domNode
   * @param maxRecordTags
   * @param mapper
   * @return meta function to get all the aligned elements, fold all the aligned element in a Map[Int, Seq[DomNode] ]
   */
  private def aligned(domNode: DomNode, maxRecordTags: Int, mapper: DomNode => (Int, DomNode)): Map[Int, Seq[DomNode]] =
    domNode.children
      .filter(_.bfs.size <= maxRecordTags)
      .map(mapper)
      .foldLeft(Map.empty[Int, Seq[DomNode]]) { (map, posNode) =>
        val (pos, node) = posNode
        map + (pos -> (map.getOrElse(pos, Seq.empty) :+ node))
      }.filter(_._2.size > 1)

  private def verticallyAligned(domNode: DomNode, maxRecordTags: Int) =
    aligned(domNode, maxRecordTags, n => n.location.x -> n)

  private def horizontallyAligned(domNode: DomNode, maxRecordTags: Int) =
    aligned(domNode, maxRecordTags, n => n.location.y -> n)

  private def notAligned(domNode: DomNode, vertical: Map[Int, Seq[DomNode]], horizontal: Map[Int, Seq[DomNode]]) = {
    val aligned = (vertical.values ++ horizontal.values).flatten.toSet
    domNode.children.toSet.diff(aligned)
  }

  /**
   *
   * @param seq
   * @param minsim
   * @return a seq of structurally similar DomNode and a seq of non structurally similar DomNodes
   */
  private def structuralFilter(seq: Seq[DomNode], minsim: Float): (Seq[DomNode], Seq[DomNode]) = {
    var nonSimilar = List.empty[DomNode]

    val similar = Random.shuffle(seq) match {
      //take the head and for the tail filter all the elements similar to the head
      case head :: tail =>
        head :: tail.filter { n =>
          val dist = Distances.normalizedEditDistance(head.bfs, n.bfs)
          if (dist > minsim) nonSimilar = n :: nonSimilar
          dist <= minsim
        }
    }

    if (similar.size > 1) (similar, nonSimilar)
    else (Seq.empty, similar ++ nonSimilar)

  }

}

object TiledListFinder

object ListMerger

