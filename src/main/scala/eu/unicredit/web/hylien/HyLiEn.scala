package eu.unicredit.web.hylien

import com.typesafe.scalalogging.Logger
import eu.unicredit.web.Models.{ BrowserSize, DomNode, WebList }
import eu.unicredit.web.VisualTagTreeBuilder
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

/**
 * Created by fabiofumarola on 25/05/16.
 */
class VisualHyLiEn(headless: Boolean = true, quickRender: Boolean = true,
  logReqs: Boolean = false, browserSize: BrowserSize = BrowserSize(1920, 1080)) {
  private val logger = Logger(LoggerFactory.getLogger("HyLiEn"))

  private val webExtractor = new VisualTagTreeBuilder(
    headless = headless,
    quickRender = quickRender,
    logReqs = logReqs,
    browserSize = browserSize)

  def extract(url: String, tagSimFactor: Float = 0.4F, maxRecordTags: Int = 30): Seq[WebList] = {
    val root = webExtractor.parse(url)
    logger.debug(s"parsed $url, start extracting lists")

    @tailrec
    def extract0(notAligned: List[DomNode], acc: List[WebList]): List[WebList] =
      if (notAligned.isEmpty) acc
      else notAligned match {
        case head :: tail =>
          val (lists, notAligned) = VisualListFinder.find(url, head, tagSimFactor, maxRecordTags)
          extract0(tail ++ notAligned, acc ++ lists)

        case Nil => acc
      }

    val lists = extract0(List(root), List.empty)

    //compose functions applying from the last to the first
    val filters = WebListFilters.tiledListsFinder(tagSimFactor) _ compose
      WebListFilters.filterDuplicates compose
      WebListFilters.filterEmptyText

    filters(lists)

  }

  def close() = webExtractor.close()

}
