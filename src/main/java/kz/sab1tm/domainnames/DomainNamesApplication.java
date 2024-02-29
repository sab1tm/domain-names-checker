package kz.sab1tm.domainnames;

import kz.sab1tm.domainnames.service.DbService;
import kz.sab1tm.domainnames.service.PsService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
public class DomainNamesApplication implements CommandLineRunner {

    private final DbService dbService;

    public static void main(String[] args) {
        SpringApplication.run(DomainNamesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dbService.maintaining();
    }
}
