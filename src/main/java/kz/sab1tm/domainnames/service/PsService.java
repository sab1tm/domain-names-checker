package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.config.AppEnv;
import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.dto.ps.*;
import kz.sab1tm.domainnames.model.enumeration.DomainSource;
import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Slf4j
public class PsService {

    private final AppEnv appEnv;
    private final DomainService domainService;
    private final RestTemplate restTemplate;

    private final String URL_TOMORROW = "https://www.ps.kz/domains/lists/freez?period=tomorrow";
    private final String URL_TODAY = "https://www.ps.kz/domains/lists/freez";
    private final String URL_CHECK = "https://api.ps.kz/kzdomain/domain-check?username=%s&password=%s&input_format=http&output_format=json&dname=%s";

    @Scheduled(cron = "0 1 0 * * ?") // Запуск в 00:01 каждую ночь
    public void run() {
        log.info("=== request domains available tomorrow ===");
        try {
            Document doc = Jsoup.connect(URL_TOMORROW).get();
            Date nextDay = Date.valueOf(LocalDate.now().plusDays(1));

            for (Element div : doc.select("div")) {
                if (div.hasClass("domains-freeze__item")) {
                    domainProcessing(div, nextDay);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("=== end ===");
    }

    @Scheduled(fixedDelay = 300000) // Запуск каждые 5 минут
    public void todayReleasesProcessing() {
        log.info("=== monitoring domains released today ===");
        List<Domain> list = domainService.getTodayReleases();
        notReleasedProcessing(list);
        log.info("=== end ===");
    }

    @Scheduled(fixedDelay = 600000) // Запуск каждые 10 минут
    public void oldReleasesProcessing() {
        log.info("=== monitoring previously unreleased domains ===");
        List<Domain> list = domainService.getOldTodayReleases();
        notReleasedProcessing(list);
        log.info("=== end ===");
    }

    private void domainProcessing(Element div, Date nextDay) {
        Element spanElement = div.selectFirst("span");
        if (Objects.nonNull(spanElement)) {
            String domainName = spanElement.text();
            // delete old
            domainService.deleteByName(domainName);
            log.info("added domain {}", domainName);
            domainService.create(
                    Domain.builder()
                            .name(domainName)
                            .releaseDate(nextDay)
                            .checkDateTime(LocalDateTime.now())
                            .status(DomainStatus.HOLDED)
                            .source(DomainSource.PS_KZ)
                            .errorCode(null)
                            .errorText(null)
                            .build()
            );
        }
    }

    private void notReleasedProcessing(List<Domain> list) {
        for (Domain domain : list) {
            String requestUrl = String.format(URL_CHECK, appEnv.getPsApiLogin(), appEnv.getPsApiPassword(), domain.getName());
            try {
                PsResponseDto responseDto = restTemplate.getForObject(requestUrl, PsResponseDto.class);
                if (responseDto.result() == PsResultEnum.success) {
                    if (Objects.nonNull(responseDto.answer())) {
                        List<PsDomainDto> domains = responseDto.answer().domains();
                        if (!domains.isEmpty()) {
                            PsDomainDto domainDto = domains.getFirst();
                            if (domain.getName().equals(domainDto.dname())) {
                                if (domainDto.result() == PsDomainResultEnum.Available) {
                                    domain.setStatus(DomainStatus.AVAILABLE);
                                } else if (domainDto.result() == PsDomainResultEnum.error) {
                                    if (domainDto.errorCode() == PsErrorCodeEnum.DOMAIN_ALREADY_EXISTS) {
                                        domain.setStatus(DomainStatus.TAKEN);
                                    } else {
                                        domain.setErrorCode(domainDto.errorCode());
                                        domain.setErrorText(domainDto.errorText());
                                    }
                                }
                                domain.setCheckDateTime(LocalDateTime.now());
                                domainService.update(domain);
                                log.info("request for {}, status: {}", domain.getName(), domain.getStatus());
                            }
                        }
                    }
                } else {
                    log.error("request for {}, result is error", domain.getName());
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                log.error("request for {}, throwing exception", domain.getName());
                log.error(e.getMessage());
            }
        }
    }
}
