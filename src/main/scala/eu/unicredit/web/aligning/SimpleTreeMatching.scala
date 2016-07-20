package eu.unicredit.web.aligning

import eu.unicredit.web.Models.DomNode


/**
  * Created by fabiana on 7/8/16.
  * Simple Tree Matching is a technique implemented in the paper "Structured Data Extraction from the Web Based on Partial Tree Alignment"
  */
object SimpleTreeMatching {

  object TrackBack extends Enumeration {
    type TrackBackType = Value
    val UP, LEFT, DIAGONAL = Value
  }

  case class TreeAlignment(score: Double, firstNode: Option[DomNode], secondNode: Option[DomNode], subTreeAlignment: Seq[TreeAlignment])

  /**
    *  Using trackBackMatrix to extract aligned children
    * @param alignmentMatrix
    * @param trackbackMatrix
    * @return
    */
  private def trackBack( alignmentMatrix: Array[Array[TreeAlignment]], trackbackMatrix: Array[Array[TrackBack.TrackBackType]]): List[TreeAlignment] =
  {
    def trackBack0(row:Int, column: Int,  acc:List[TreeAlignment]) : List[TreeAlignment] = {
      if(row == -1 || column == -1)
        acc
      else {
        trackbackMatrix(row)(column) match {
          case TrackBack.DIAGONAL =>
            val newAcc = alignmentMatrix(row)(column) +: acc
              trackBack0(row-1, column-1, newAcc)
          case TrackBack.UP =>
            trackBack0(row-1, column, acc)
          case TrackBack.LEFT =>
            trackBack0(row, column-1, acc)
        }
      }
    }

    var startRow = trackbackMatrix.length - 1
    val startColumn = startRow match {
      case -1 => -1
      case _ => trackbackMatrix(0).length - 1
    }
    trackBack0(startRow, startColumn, List())
  }

  /**
    * Given two DomeNodes returns the aligned tree
    * @param domNodeA
    * @param domNodeB
    * @return
    */
  def align(domNodeA: DomNode, domNodeB: DomNode): TreeAlignment = {
    case class TrackTuple(trackBack: TrackBack.TrackBackType , score: Double)

    def max(xs: List[TrackTuple]): Option[TrackTuple] = xs match {
      case Nil => None
      case List(x: TrackTuple) => Some(x)
      case x :: y :: rest => max( (if (x.score > y.score) x else y) :: rest )
    }

    domNodeA.tagName.equals(domNodeB.tagName) match {
      case false => TreeAlignment(0D, None, None, Seq.empty)
      case true =>
        val num_rows = domNodeA.children.size + 1
        val num_columns =  domNodeB.children.size + 1
        //matchMatrix has 1 column and row more than other matrices
        val matchMatrix = Array.ofDim[Double](num_rows, num_columns)
        val treeAlignmentMatrix = Array.ofDim[TreeAlignment](num_rows -1, num_columns -1)
        val trackBackMatrix = Array.ofDim[TrackBack.Value](num_rows -1 , num_columns -1)

        //Initialize 0th row and 0th column
        matchMatrix.indices.foreach(row => matchMatrix(row)(0) = 0D)
        matchMatrix(0).indices.foreach(column => matchMatrix(0)(column) = 0D)

        val pairs = for{
          row <- 1 until num_rows
          column <- 1 until num_columns
        } yield (row, column)

        pairs.foreach{
          case (row, column) =>
            treeAlignmentMatrix(row-1)(column -1) = align(domNodeA.children(row-1), domNodeB.children(column-1))
            val diagonalTuple = TrackTuple(TrackBack.DIAGONAL, matchMatrix(row-1)(column-1) + treeAlignmentMatrix(row-1)(column-1).score)
            val upTuple = TrackTuple(TrackBack.UP, matchMatrix(row-1)(column))
            val leftTuple= TrackTuple(TrackBack.LEFT,matchMatrix(row)(column-1))
            val bestMatch = max(List(diagonalTuple, upTuple, leftTuple)).get

            matchMatrix(row)(column) = bestMatch.score
            trackBackMatrix(row -1)(column -1) = bestMatch.trackBack
        }
        val subTreeAligned = trackBack(treeAlignmentMatrix, trackBackMatrix)
        val score = 1.00 + matchMatrix(matchMatrix.length - 1)(matchMatrix(0).length - 1)
        TreeAlignment(score, Some(domNodeA), Some(domNodeB), subTreeAligned)
    }
  }


}
