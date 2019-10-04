package samples.common

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import scala.util.matching.Regex

object DateTimeExtractor {
  val dateTimeExtractors = List(new ZonedDateTimeExtractor, new LocalDateTimeExtractor)

  def extractDate(line: String): Long = {
    dateTimeExtractors
      .view
      .map(_.maybeParse(line))
      .collectFirst {
        case Some(d) => d
      }
      .getOrElse(-1L)
  }

  sealed trait DateTimeExtractor {
    def regex: Regex
    def parse(dateStr: String): Long
    def maybeParse(dateStr: String): Option[Long] = {
      val matched: Option[String] = regex.findFirstIn(dateStr)
      matched.map(parse)
    }
  }

  /**
    * ZonedDateTime
    * Ex)
    * 2016-01-19T15:21:32.59+02:00
    * https://regex101.com/r/LYluKk/4
    */
  final class ZonedDateTimeExtractor extends DateTimeExtractor {
    val regex: Regex = """((?:(\d{4}-\d{2}-\d{2})[T| ](\d{2}:\d{2}:\d{2}(?:\.\d+)?))(Z|[\+-]\d{2}:\d{2})+)""".r
    def parse(dateStr: String): Long = ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant.toEpochMilli
  }

  /**
    * LocalDateTime
    * Ex)
    * 2019-09-20 21:18:24,774
    * https://regex101.com/r/LYluKk/3
    */
  final class LocalDateTimeExtractor extends DateTimeExtractor {
    private val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss,SSS")
    val regex: Regex = """((?:(\d{4}-\d{2}-\d{2})[T| ](\d{2}:\d{2}:\d{2}(?:\.\d+)?)),(\d{3}?))""".r
    def parse(dateStr: String): Long = LocalDateTime.parse(dateStr, pattern).toInstant(ZoneOffset.UTC).toEpochMilli
  }
}
