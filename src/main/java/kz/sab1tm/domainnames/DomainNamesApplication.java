package kz.sab1tm.domainnames;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.service.DbService;
import kz.sab1tm.domainnames.service.PsKzParsingService;
import kz.sab1tm.domainnames.service.DomainService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
public class DomainNamesApplication implements CommandLineRunner {

    private final DbService dbService;

    private final PsKzParsingService psKzParsingService;
    private final DomainService domainService;

    public static void main(String[] args) {
        SpringApplication.run(DomainNamesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dbService.maintaining();

        // TODO test
        psKzParsingService.run();

        List<Domain> list = domainService.getAll();

        list.forEach(
                System.out::println
        );
    }
}
