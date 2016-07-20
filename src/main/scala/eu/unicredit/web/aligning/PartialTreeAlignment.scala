package eu.unicredit.web.aligning

import eu.unicredit.web.Models.{DomNode, WebList}
import eu.unicredit.web.aligning.SimpleTreeMatching.TreeAlignment

import scala.collection.immutable.{IndexedSeq, Queue}

/**
  * Created by fabiana on 7/8/16.
  */
class PartialTreeAlignment {

  var alignmentMap = Map.empty[DomNode, List[DataRecord]]
  /**
    *
    * @param sizeTree size of the tree at root domNode
    * @param index index of datarecord in a weblist
    * @param domNode
    */
  case class DataRecord(sizeTree: Int, index: Int, domNode:DomNode)

  private def getSizeTree(tree: DomNode): Int = {
    def getSize0(nodes: List[DomNode], acc:Int): Int = nodes match {
      case List() => acc
      case h::tail => getSize0(h.children.toList ++ tail, acc+1)
    }

    getSize0(List(tree), 0)
  }

  /**Given a sequence of DomNodes return a collection of DataRecords ordered wrt the size of the DomNode's tree.
    * The i-th DomNode is converted in a DataRecord having index=i, sizeTree=#nodes of the tree rooted in the ith DomNode
    *
    * @param datarecords
    * @return
    */
  private def sortDataRecords(datarecords: Seq[DomNode]): IndexedSeq[DataRecord]  = {
    val indexedDataRecord: IndexedSeq[(Int, DomNode)] = datarecords.indices.zip(datarecords)
    indexedDataRecord
      .map(dr => DataRecord(getSizeTree(dr._2), dr._1, dr._2))
      .sortWith(_.sizeTree > _.sizeTree)
  }

  private def initializeAlignmentMap(seed: DataRecord): Unit = {
    def initializeAlignmentMap0(nodesToAnalyze: List[DomNode], acc: List[(DomNode, List[DataRecord])]): List[(DomNode, List[DataRecord])] = {
      nodesToAnalyze match {
        case List() => acc
        case head :: tail =>
          val dataRecord = DataRecord(seed.sizeTree, seed.index, head)
          initializeAlignmentMap0(tail, acc :+ (head, List(dataRecord)))
      }
    }

    alignmentMap = initializeAlignmentMap0(List(seed.domNode),  List()).toMap

  }


  /**
    *
    * @param alignment alignment between the seed and a tree
    * @param seed which will be update with tree's nodes
    * @return
    */
  private def updatedAlignmentTree(alignment: TreeAlignment, seed: DomNode): Boolean = {


    def aggregationFunction(acc:List[List[(DomNode, Int)]], el:(DomNode, Int)): List[List[(DomNode, Int)]] = {

      val lastList: List[(DomNode, Int)] = acc.last
      if (lastList.isEmpty)
        List(List(el))
      else {
        val lastEl = lastList.last
        if (lastEl._2 == el._2 - 1) {
          val headAcc = acc.dropRight(1)
          val aggregateList = lastList :+ el
          headAcc :+ aggregateList
        }
        else
          acc :+ List(el)
      }
    }

    //contains all children of tree for which exists a match
    val matchedTreeChildren = alignment.subTreeAlignment.flatMap(x => x.secondNode)
    val tree = alignment.secondNode.get
    val treeChildren = tree.children
    val indexedTreeChildren = tree.children.zipWithIndex
    val unmatchedChildren = indexedTreeChildren.filterNot(x => matchedTreeChildren.contains(x._1))
    var isInserted = false
    val clonedSeedChildren = seed.children.map(c => c.copy())

    //is a collection of consecutive unmatched DomNodes
    matchedTreeChildren match {
      case List() => false
      //TODO se seed non ha figli allora prende i figli di tree
      case _ =>
        val groupedUnmatchedChildren = unmatchedChildren.foldLeft(List(List.empty[(DomNode,Int)])){(acc, el) => aggregationFunction(acc, el)}

        groupedUnmatchedChildren.foreach{
          case Nil =>
          case unmachedNodes =>
            //check left matching
            if(unmachedNodes.head._2 == 0) {
              val firstMatchedIndex = unmachedNodes.last._2 + 1
              val firstMatchedNode = treeChildren(firstMatchedIndex)
              //firstMatchedNode deve essere allineato con il primo nodo del seed
              if(alignment.subTreeAlignment.head.secondNode.get == firstMatchedNode && alignment.subTreeAlignment.head.firstNode.get == seed.children.head){
                seed.children.++=:(unmachedNodes.map(_._1))
                isInserted = true
              }
            }
            //check right matching
            else if(unmachedNodes.last._1 == treeChildren.last){
              val lastMatchedIndex = unmachedNodes.head._2 - 1
              val lastMatchedNode = treeChildren(lastMatchedIndex)
              if(alignment.subTreeAlignment.last.secondNode.get == lastMatchedNode && alignment.subTreeAlignment.last.firstNode.get == seed.children.last ){
                seed.children.++=(unmachedNodes.map(_._1))
                isInserted = true
              }
            }
            //check between matching
            else {
              val leftMatchedIndex = unmachedNodes.head._2 - 1
              val rightMatchedIndex = unmachedNodes.last._2 + 1
              val rightMatchedNode = treeChildren(rightMatchedIndex)
              val leftMatchedNode = treeChildren(leftMatchedIndex)

              //identify the indices of leftMatchedNode and rightMatchedNode in alignment.subTreeAlignment
              val indexLeft = matchedTreeChildren.indexOf(leftMatchedNode)
              val indexRight = matchedTreeChildren.indexOf(rightMatchedNode)

              //use the previous indices to find the related nodes in the seed tree
              val sNodeLeft = alignment.subTreeAlignment(indexLeft).firstNode.get
              val sNodeRight = alignment.subTreeAlignment(indexRight).firstNode.get

              //find the indices of previous seed's DomNodes and check if they are consecutive
              val indexSeedLeft = seed.children.indexOf(sNodeLeft)
              val indexSeedRight = seed.children.indexOf(sNodeRight)

              if(indexSeedLeft == indexSeedRight -1){
                seed.children.insertAll(indexSeedLeft+1, unmachedNodes.map(_._1))
                isInserted = true
              }
            }

        }

        //try to match other discendents of tree
        val inserted = alignment.subTreeAlignment.map{node =>
          clonedSeedChildren.find{n => node.firstNode.get == n}
          match {
            case None =>
              println(s"Some error in n = ${node.firstNode.get.text}")
              false
            case Some(childSeed)  =>
              updatedAlignmentTree(node, childSeed)}
        }


        isInserted || inserted.exists(x => x)
    }



  }


  /**
    *
    * @param webList
    * @return
    */
  def run(webList: WebList): DomNode = {
    /**
      *
      * @param queue       containing domNode to align with the seed and max number of iterations (i.e. 2)
      * @param updatedSeed tree to update
      * @return
      */
    def align(queue: Queue[(DataRecord, Int)], updatedSeed: DomNode): DomNode = {
      queue match {
        case Queue() => updatedSeed
        case head +: tail =>
          val dataRecord = head._1
          val alignedTree = SimpleTreeMatching.align(updatedSeed, dataRecord.domNode)
          val unalignedNodes = dataRecord.sizeTree - alignedTree.score
          if (unalignedNodes != 0) {
            val isUpdated = updatedAlignmentTree(alignedTree, updatedSeed)
            println(s"Added unaligned nodes: $isUpdated")
          }
          head._2 match {
            case depth if depth < 2  =>
              val newQueue =  unalignedNodes match {
                case 0 => tail
                case _ => tail.enqueue((head._1, head._2 + 1))
              }
              align(newQueue, updatedSeed)
            case _ => align(tail, updatedSeed)


          }
      }
    }

    val sortedDataRecords = sortDataRecords(webList.elements)
    val seed: DataRecord = sortedDataRecords.head
    val clonedSeed = seed.copy().domNode
    val queue: Queue[(DataRecord, Int)] = scala.collection.immutable.Queue(sortedDataRecords.tail.map(x => (x, 1)): _*)

    align(queue, clonedSeed)
    clonedSeed
  }
}
