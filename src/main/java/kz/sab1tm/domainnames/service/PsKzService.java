package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.enumeration.DomainSource;
import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * <div class="domains__list">   <!-- this div will be processed -->
 *           <div class="row">
 *                       <div class="domains-freeze__item">
 *               <a href="https://www.ps.kz/domains/whois/result?q=arendasaitov.kz"
 *                class="domains-freeze__link">
 *                 <span>arendasaitov.kz</span>
 *               </a>
 *               <sup>1 дн</sup>
 *             </div>
 * </div>
 */

@Service
@AllArgsConstructor
public class PsKzService {

    private final DomainService domainService;

    private final String URL = "https://www.ps.kz/domains/lists/freez?period=tomorrow";

    @Scheduled(cron = "0 1 0 * * ?") // Запуск в 00:01 каждую ночь
    public void run() {
        try {
            Document doc = Jsoup.connect(URL).get();
            Date nextDay = Date.valueOf(LocalDate.now().plusDays(1));

            for (Element div : doc.select("div")) {
                if (div.hasClass("domains-freeze__item")) {
                    domainProcessing(div, nextDay);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void domainProcessing(Element div, Date nextDay) {
        Element spanElement = div.selectFirst("span");
        if (Objects.nonNull(spanElement)) {
            String domainName = spanElement.text();
            // delete old
            domainService.deleteByName(domainName);
            domainService.create(
                    Domain.builder()
                            .name(domainName)
                            .releaseDate(nextDay)
                            .checkDate(null)
                            .status(DomainStatus.NOT_RELEASED)
                            .source(DomainSource.PS_KZ)
                            .build()
            );
        }
    }

    public void statusPoll() {
        List<Domain> list = domainService.getTodayReleases();
        for (Domain domain : list) {

        }
    }
}
