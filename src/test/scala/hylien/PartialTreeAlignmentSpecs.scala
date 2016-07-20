package hylien

import eu.unicredit.web.Models.{DomNode, Location, Size, WebList}
import eu.unicredit.web.aligning.PartialTreeAlignment
import eu.unicredit.web.hylien.Distances
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.collection.mutable

/**
  * Created by fabiana on 7/20/16.
  */
class PartialTreeAlignmentSpecs extends Specification {
  class Context extends Scope {
    val node_tagA = DomNode(id =1,
      parentId=0,
      tagName = "a",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "node A",
      html = "")
    val node_tagB = DomNode(id = 2,
      parentId=0,
      tagName = "b",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "node B",
      html = "")
    val node_tagC = DomNode(id = 3,
      parentId=0,
      tagName = "c",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "node C",
      html = "")
    val node_tagD = DomNode(id = 2,
      parentId=0,
      tagName = "d",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "node D",
      html = "")
    val node_tagE = DomNode(id = 2,
      parentId=0,
      tagName = "e",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "node E",
      html = "")
    val node_tagF = DomNode(id = 2,
      parentId=0,
      tagName = "f",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "node F",
      html = "")

    val seed = DomNode(id = 2,
      parentId=0,
      tagName = "a",
      cssClass ="",
      cssProperties = Map(),
      cssSelector = "",
      location = Location(100, 100),
      size = Size(100,100),
      text= "seed",
      html = "",
      children = mutable.Buffer(node_tagA, node_tagB, node_tagC, node_tagC)
    )

  }

  "A PartialTreAlignment" should {
//    "add a branch in head of the seed datarecord" in new Context{
//
//      val clonedSeed = seed.copy()
//      val datarecord2 = DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "seed",
//        html = "",
//        children = mutable.Buffer(node_tagD, node_tagA, node_tagB)
//      )
//
//      val webList = new WebList("www.prova.it", null, null, null,null, Seq(clonedSeed, datarecord2))
//      val pta = new PartialTreeAlignment
//      val schema = pta.run(webList)
//      schema.children.size === 5
//
//    }
//    "add a branch in the end of the seed datarecord" in new Context{
//      val clonedSeed = seed.copy()
//      val datarecord2 = DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "seed",
//        html = "",
//        children = mutable.Buffer(node_tagC, node_tagA, node_tagB)
//      )
//
//      val webList = new WebList("www.prova.it", null, null, null,null, Seq(clonedSeed, datarecord2))
//      val pta = new PartialTreeAlignment
//      val schema = pta.run(webList)
//      schema.children.size === 5
//    }
//    "add a branch in the middle of the seed datarecord" in new Context{
//      val clonedSeed = seed.copy()
//      val datarecord2 = DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "seed",
//        html = "",
//        children = mutable.Buffer(node_tagB, node_tagD, node_tagC)
//      )
//
//      val webList = new WebList("www.prova.it", null, null, null,null, Seq(clonedSeed, datarecord2))
//      val pta = new PartialTreeAlignment
//      val schema = pta.run(webList)
//      schema.children.size === 5
//    }
//    "add a branch in the middle of the seed datarecord al deeper level" in new Context{
//      val T1 =  DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "T1",
//        html = "",
//        children = mutable.Buffer(node_tagA, node_tagC)
//      )
//      val T2 =  DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "T2",
//        html = "",
//        children = mutable.Buffer(node_tagA, node_tagA.copy(id=3), node_tagC)
//      )
//      val newSeed = DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "seed",
//        html = "",
//        children = mutable.Buffer(T1, node_tagB, node_tagC, node_tagC)
//      )
//      val datarecord = DomNode(id = 2,
//        parentId=0,
//        tagName = "a",
//        cssClass ="",
//        cssProperties = Map(),
//        cssSelector = "",
//        location = Location(100, 100),
//        size = Size(100,100),
//        text= "seed",
//        html = "",
//        children = mutable.Buffer(T2, node_tagB)
//      )
//      val webList = new WebList("www.prova.it", null, null, null,null, Seq(newSeed, datarecord))
//      val pta = new PartialTreeAlignment
//      val schema = pta.run(webList)
//      Distances.treeEditDistance(schema, datarecord) === 6
//    }
    "add some branch after the first iteration" in new Context {
      val TA =  DomNode(id = 2,
        parentId=0,
        tagName = "a",
        cssClass ="",
        cssProperties = Map(),
        cssSelector = "",
        location = Location(100, 100),
        size = Size(100,100),
        text= "TA",
        html = "",
        children = mutable.Buffer(node_tagA, node_tagB)
      )
      val TB =  DomNode(id = 2,
        parentId=0,
        tagName = "b",
        cssClass ="",
        cssProperties = Map(),
        cssSelector = "",
        location = Location(100, 100),
        size = Size(100,100),
        text= "TB",
        html = "",
        children = mutable.Buffer(node_tagA, node_tagB)
      )
      val TC =  DomNode(id = 2,
        parentId=0,
        tagName = "c",
        cssClass ="",
        cssProperties = Map(),
        cssSelector = "",
        location = Location(100, 100),
        size = Size(100,100),
        text= "TC",
        html = "",
        children = mutable.Buffer(node_tagA, node_tagB)
      )
      val newSeed2 = DomNode(id = 2,
        parentId=0,
        tagName = "a",
        cssClass ="",
        cssProperties = Map(),
        cssSelector = "",
        location = Location(100, 100),
        size = Size(100,100),
        text= "seed",
        html = "",
        children = mutable.Buffer(TB, TC, node_tagD)
      )
      val datarecord1 = DomNode(id = 2,
        parentId=0,
        tagName = "a",
        cssClass ="",
        cssProperties = Map(),
        cssSelector = "",
        location = Location(100, 100),
        size = Size(100,100),
        text= "datarecord1",
        html = "",
        children = mutable.Buffer(TA, node_tagB, node_tagE, node_tagF, node_tagD)
      )
      val datarecord2 = DomNode(id = 2,
        parentId=0,
        tagName = "a",
        cssClass ="",
        cssProperties = Map(),
        cssSelector = "",
        location = Location(100, 100),
        size = Size(100,100),
        text= "datarecord2",
        html = "",
        children = mutable.Buffer( node_tagB, node_tagC, node_tagE, node_tagD)
      )
      val webList = new WebList("www.prova.it", null, null, null,null, Seq(newSeed2, datarecord1, datarecord2))
      val pta = new PartialTreeAlignment
      val schema = pta.run(webList)
      Distances.treeEditDistance(schema, schema) === 13
    }
  }
}
