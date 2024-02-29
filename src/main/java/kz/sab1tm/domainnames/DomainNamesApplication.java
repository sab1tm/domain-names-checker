package kz.sab1tm.domainnames;

import kz.sab1tm.domainnames.service.DbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DomainNamesApplication implements CommandLineRunner {

    private final DbService dbService;

    public DomainNamesApplication(DbService dbService) {
        this.dbService = dbService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DomainNamesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dbService.maintaining();
    }
}
