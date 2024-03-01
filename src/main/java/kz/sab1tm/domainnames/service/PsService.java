package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.config.AppEnv;
import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.dto.ps.*;
import kz.sab1tm.domainnames.model.enumeration.DomainSourceEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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

    private final String URL_RELEASES = "https://www.ps.kz/domains/lists/freez";
    private final String URL_CHECK = "https://api.ps.kz/kzdomain/domain-check?username=%s&password=%s&input_format=http&output_format=json";

    @Scheduled(cron = "0 1 0 * * ?") // Запуск в 00:01 каждую ночь
    public void run() {
        log.info("=== request domains available tomorrow ===");

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(URL_RELEASES)
                .queryParam("period", "tomorrow")
                .build();

        try {
            Document doc = Jsoup.connect(uri.toUriString()).get();
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

    @Scheduled(fixedDelay = 120000) // Запуск каждые 2 минут
    public void todayReleasesProcessing() {
        log.info("=== monitoring domains released today ===");
        List<Domain> list = domainService.getLimitTodayReleases(50);
        notReleasedProcessing(list);
        log.info("=== end ===");
    }

    @Scheduled(fixedDelay = 300000) // Запуск каждые 5 минут
    public void oldReleasesProcessing() {
        log.info("=== monitoring previously unreleased domains ===");
        List<Domain> list = domainService.getLimitOldTodayReleases(50);
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
                            .status(DomainStatusEnum.HOLDED)
                            .source(DomainSourceEnum.PS_KZ)
                            .errorCode(null)
                            .errorText(null)
                            .build()
            );
        }
    }

    private void notReleasedProcessing(List<Domain> list) {
        if (list.isEmpty())
            return;

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(URL_CHECK)
                .queryParam("username", appEnv.getPsApiLogin())
                .queryParam("password", appEnv.getPsApiPassword())
                .queryParam("input_format", "http")
                .queryParam("output_format", "json")
                .queryParam("dname[]", list.stream().map(Domain::getName).toArray())
                .build();

        try {
            PsResponseDto responseDto = restTemplate.getForObject(uri.toUriString(), PsResponseDto.class);
            if (responseDto.result() == PsResultEnum.success && Objects.nonNull(responseDto.answer())) {
                List<PsDomainDto> domainsResult = responseDto.answer().domains();
                if (!domainsResult.isEmpty()) {
                    for (Domain domain : list) {
                        for (PsDomainDto domainDto : domainsResult) {
                            if (domain.getName().equals(domainDto.dname())) {
                                if (domainDto.result() == PsDomainResultEnum.Available) {
                                    domain.setStatus(DomainStatusEnum.AVAILABLE);
                                } else if (domainDto.result() == PsDomainResultEnum.error) {
                                    if (domainDto.errorCode() == PsErrorCodeEnum.DOMAIN_ALREADY_EXISTS) {
                                        domain.setStatus(DomainStatusEnum.TAKEN);
                                    } else {
                                        domain.setErrorCode(domainDto.errorCode());
                                        domain.setErrorText(domainDto.errorText());
                                    }
                                }
                                domain.setCheckDateTime(LocalDateTime.now());
                                domainService.update(domain);
                                log.info("{}, status: {}", domain.getName(), domain.getStatus());
                            }
                        }
                    }
                }
            } else {
                log.error("request error");
            }
        } catch (Exception e) {
            log.error("request exception");
            log.error(e.getMessage());
        }
    }
}
