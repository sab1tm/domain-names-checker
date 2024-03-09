package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.config.AppEnv;
import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.enumeration.DomainSourceEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <td class="field_domain"><a href="/goto/1/4uhv9q/489/" target="_blank" rel="nofollow" title="GamesTeem.com">GamesTeem.com</a></td> <!-- this div will be processed -->
 */

@Service
@AllArgsConstructor
@Slf4j
public class EdNetService {

    private final DomainService domainService;
    private final RestTemplate restTemplate;
    private final AppEnv appEnv;

    private final String URL_AVAILABLE = "https://www.expireddomains.net";

//    @Scheduled(fixedDelay = 60000) // Запуск каждую минуту
    public void run() {
        if (appEnv.isMaintaining())
            return;
        log.info("=== request available domains to EDNet ===");

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(URL_AVAILABLE)
                .pathSegment("tld", "com")
                .build();
        try {
            Document doc = Jsoup.connect(uri.toUriString()).get();
            Date today = Date.valueOf(LocalDate.now());

            Elements elements = doc.select("td.field_domain a");
            for (Element element : elements) {
                String domainName = element.text();
                if (!domainName.contains("...") && domainName.length() <= 20)
                    domainProcessing(domainName, today);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("=== end ===");
    }

    private void domainProcessing(String domainName, Date today) {
        if (!domainName.isBlank()) {
            // delete old
            domainService.deleteByName(domainName.toLowerCase());
            log.info("added domain {}", domainName);
            domainService.create(
                    Domain.builder()
                            .name(domainName.toLowerCase())
                            .releaseDate(today)
                            .checkDateTime(LocalDateTime.now())
                            .status(DomainStatusEnum.AVAILABLE)
                            .source(DomainSourceEnum.ED_NET)
                            .errorCode(null)
                            .errorText(null)
                            .build()
            );
        }
    }
}
