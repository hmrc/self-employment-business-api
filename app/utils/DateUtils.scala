package utils

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

  private val longDateTimeFormatGmt: DateTimeFormatter = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    .withZone(ZoneId.of("GMT"))

  def getTaxYear(taxYearOpt: Option[String], currentDate: LocalDate): String = taxYearOpt match {
    case Some(taxYear) => taxYear
    case None =>
      val fiscalYearStartDate = LocalDate.parse(s"${currentDate.getYear.toString}-04-05")

      if (currentDate.isAfter(fiscalYearStartDate)) {
        s"${currentDate.getYear}-${currentDate.getYear.+(1).toString.drop(2)}"
      } else {
        s"${currentDate.getYear.-(1)}-${currentDate.getYear.toString.drop(2)}"
      }
  }

  def longDateTimestampGmt(dateTime: LocalDateTime): String = longDateTimeFormatGmt.format(dateTime)
}
