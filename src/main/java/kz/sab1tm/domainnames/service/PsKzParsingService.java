package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.model.Domain;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PsKzParsingService {

    private final DomainService domainService;

    private final String PS_KZ_TOMORROW_LIST = "https://www.ps.kz/domains/lists/freez?period=tomorrow";

    @Scheduled(cron = "0 1 0 * * ?") // Запуск в 00:01 каждую ночь
    public void run() {
        try {
            Document doc = Jsoup.connect(PS_KZ_TOMORROW_LIST).get();
            LocalDate nextDay = LocalDate.now().plusDays(1);

            for (Element div : doc.select("div")) {
                if (div.hasClass("domains-freeze__item"))
                    domainProcessing(div, nextDay);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void domainProcessing(Element div, LocalDate nextDay) {
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
                            .build()
            );
        }
    }
}
