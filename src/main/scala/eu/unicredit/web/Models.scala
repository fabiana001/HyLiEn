package eu.unicredit.web

import org.jsoup.Jsoup

import scala.annotation.tailrec
<<<<<<< HEAD
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Failure, Success, Try}
=======
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.util.{ Failure, Success, Try }
>>>>>>> upstream/master

/**
 * Created by fabiofumarola on 24/05/16.
 */
object Models {

  case class BrowserSize(width: Int, height: Int)

  case class Location(x: Int, y: Int) {
    def +(that: Location) = {
      Location(
        x = if (this.x < that.x) this.x else that.x,
        y = if (this.y < that.y) this.y else that.y)
    }

  }

  val noLocation = Location(-1, -1)

  case class Size(width: Int, height: Int) {
    def +(that: Size): Size = {
      Size(
        width = this.width + that.width,
        height = this.height + that.height)
    }
  }

  val noSize = Size(-1, -1)

  val noCssSelector = ""

  case class DomNode(
    id: Int,
    parentId: Int,
    tagName: String,
    cssClass: String,
    cssProperties: Map[String, String],
    cssSelector: String,
    location: Location,
    size: Size,
    text: String,
    children: mutable.Buffer[DomNode] = mutable.Buffer.empty[DomNode],
    html: String) {
    lazy val bfs = DomNode.bfs(this)
<<<<<<< HEAD

    def getUrls(html: String): Seq[String] = {
      val tryhtml = Try {
        Jsoup.parse(html)
          .select("a[href]")
          .asScala
          .map(link => link.attr("href"))
          .toList
          .filter(s => s.size > 0)
      }
      tryhtml match {
        case Success(lists) => lists
        case Failure(ex) => List()
      }
    }

    lazy val urls = getUrls(html)
=======
    lazy val urls = DomNode.getUrls(html)
    lazy val bfsCssClasses = DomNode.bfsCssClasses(this)
    lazy val visualFeatures = DomNode.visualFeatures(this)
>>>>>>> upstream/master
  }

  object DomNode {

    def visualFeatures(n: DomNode): Map[String,String] = {
      val features = Seq("font-size", "color", "background-color",
        "font-family", "font-weight", "fill")

      features
        .map(v => v -> n.cssProperties.get(v))
        .filter(_._2.nonEmpty)
        .map(kv => kv._1 -> kv._2.get)
        .toMap

    }

    def bfs(n: DomNode): Seq[String] = {
      @tailrec
      def bfs0(nodes: Seq[DomNode], acc: Seq[String]): Seq[String] =
        if (nodes.isEmpty) acc
        else {
          val (head, tail) = (nodes.head, nodes.tail)
          bfs0(tail ++ head.children, acc ++ head.children.map(_.tagName))
        }

      bfs0(n.children, Seq(n.tagName) ++ n.children.map(_.tagName))
    }

    def getUrls(html: String, baseUri: String) = Try {
      Jsoup.parse(html, baseUri)
        .select("a[href]")
        .map(n => n.text() -> n.attr("abs:href")).toMap
    }

    def getUrls(html: String): Seq[String] = Try {
      Jsoup.parse(html)
        .select("a[href]")
        .map(_.attr("href"))
        .filter(_.length > 0)
    }.getOrElse(List.empty)

    def bfsCssClasses(n: DomNode): Seq[String] = {

      @tailrec
      def styles0(nodes: Seq[DomNode], acc: Seq[String]): Seq[String] =
        if (nodes.isEmpty) acc
        else {
          val (head, tail) = (nodes.head, nodes.tail)
          styles0(tail ++ head.children, acc ++ head.children.map(_.cssClass))
        }

      styles0(n.children, Seq(n.cssClass) ++ n.children.map(_.cssClass))
    }
  }

  object Orientation extends Enumeration {
    type Orientation = Value
    val horizontal, vertical, tiled = Value
  }

  import Orientation._

  case class WebList(
    pageUrl: String,
    parent: DomNode,
    orientation: Orientation,
    location: Location,
    size: Size,
<<<<<<< HEAD
    elements: Seq[DomNode]){
    lazy val urls = elements.flatMap(n => n.urls)
=======
    elements: Seq[DomNode],
    from: Seq[WebList] = Seq.empty) {
    lazy val urls = elements.flatMap(_.urls)
    lazy val bfs = elements.flatMap(_.bfs)
>>>>>>> upstream/master
  }

}

