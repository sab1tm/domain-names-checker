package kz.sab1tm.domainnames;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.enumeration.DomainSource;
import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import kz.sab1tm.domainnames.service.DbService;
import kz.sab1tm.domainnames.service.DomainService;
import kz.sab1tm.domainnames.service.PsKzService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
public class DomainNamesApplication implements CommandLineRunner {

    private final DbService dbService;

    private final PsKzService psKzService;
    private final DomainService domainService;

    public static void main(String[] args) {
        SpringApplication.run(DomainNamesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dbService.maintaining();

        // TODO test
//        psKzService.run();

//        domainService.create(Domain.builder()
//                .name("joycom.kz")
//                .source(DomainSource.PS_KZ)
//                .status(DomainStatus.NOT_RELEASED)
//                .releaseDate(Date.valueOf(LocalDate.now()))
//                .checkDate(null)
//                .build());


    }
}
