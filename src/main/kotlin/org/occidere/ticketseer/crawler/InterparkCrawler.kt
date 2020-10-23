package org.occidere.ticketseer.crawler

import org.jsoup.Jsoup
import org.occidere.ticketseer.enums.SiteType
import org.occidere.ticketseer.vo.MusicalTicket
import java.util.stream.Collectors

/**
 * @author occidere
 * @Blog: https://occidere.blog.me
 * @Github: https://github.com/occidere
 * @since 2020-10-23
 */
object InterparkCrawler {
    private const val ROOT_URL = "http://ticket.interpark.com"
    private const val MUSICAL_URL = "$ROOT_URL/TPGoodsList.asp?Ca=Mus"

    fun getMusicalTickets(): List<MusicalTicket> = getDocument(MUSICAL_URL)
            .select("div.stit > table > tbody > tr").stream()
            .map {
                val basicInfo = it.select("td.RKthumb > a")[0]
                val pageUrl = "$ROOT_URL${basicInfo.attr("href")}" // http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GroupCode=20007209
                val groupCode = pageUrl.split("GroupCode=")[1] // 20007209

                val imgTitleInfo = basicInfo.getElementsByTag("img")[0]
                val title = imgTitleInfo.attr("alt").trim()
                val imageUrl = imgTitleInfo.attr("src").trim()

                val placeDateInfo = it.select("td.Rkdate")
                val place = placeDateInfo.first().getElementsByTag("a")[0].text().trim()

                val dates = placeDateInfo[1].text().split("~")
                val startDate = dates[0].replace(Regex("[^0-9]"), "")
                val endDate = dates[1].replace(Regex("[^0-9]"), "")

                MusicalTicket(
                        id = groupCode,
                        title = title,
                        place = place,
                        pageUrl = pageUrl,
                        startDate = startDate,
                        endDate = endDate,
                        siteType = SiteType.INTERPARK,
                        imgUrl = imageUrl
                )
            }.collect(Collectors.toList())

    private fun getDocument(url: String) = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .followRedirects(true)
            .get()
}