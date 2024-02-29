package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.config.AppEnv;
import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.dto.ps.PsDomainDto;
import kz.sab1tm.domainnames.model.dto.ps.PsDomainResultEnum;
import kz.sab1tm.domainnames.model.dto.ps.PsResponseDto;
import kz.sab1tm.domainnames.model.dto.ps.PsResultEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainSource;
import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
public class PsService {

    private final AppEnv appEnv;
    private final DomainService domainService;
    private final RestTemplate restTemplate;

    private final String URL_TOMORROW = "https://www.ps.kz/domains/lists/freez?period=tomorrow";
    private final String URL_CHECK = "https://api.ps.kz/kzdomain/domain-check?username=%s&password=%s&input_format=http&output_format=json&dname=%s";

    @Scheduled(cron = "0 1 0 * * ?") // Запуск в 00:01 каждую ночь
    public void run() {
        System.out.println("=== Опрос списка доменов из PS.kz, освобождающихся завтра ===");
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
        System.out.println("=== Завершен ===");
    }

    @Scheduled(fixedDelay = 300000) // Запуск каждые 5 минут
    public void statusPoll() {
        System.out.println("=== Опрос статусов доменов, которые должны освободится сегодня ===");
        List<Domain> list = domainService.getTodayReleases();
        for (Domain domain : list) {
            System.out.print("опрос статуса для: " + domain.getName());
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
                                    System.out.println(", доступен");
                                } else if (domainDto.result() == PsDomainResultEnum.error) {
                                    domain.setStatus(DomainStatus.NOT_AVAILABLE);
                                    System.out.println(", не доступен");
                                }
                                domainService.update(domain);
                            }
                        }
                    }
                } else {
                    System.out.println(", ошибка");
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                System.out.println(", ошибка");
                System.out.println(e.getMessage());
            }
        }
        System.out.println("=== Завершен ===");
    }

    private void domainProcessing(Element div, Date nextDay) {
        Element spanElement = div.selectFirst("span");
        if (Objects.nonNull(spanElement)) {
            String domainName = spanElement.text();
            // delete old
            domainService.deleteByName(domainName);
            System.out.println("добавляется в БД: " + domainName);
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


}
