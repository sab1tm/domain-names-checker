package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.config.AppEnv;
import kz.sab1tm.domainnames.model.Variable;
import kz.sab1tm.domainnames.repository.InitRepository;
import kz.sab1tm.domainnames.repository.VariableRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DbService {

    private final VariableRepository variableRepository;
    private final InitRepository initRepository;
    private final AppEnv appEnv;

    private int getDbVersion() {
        Variable version = variableRepository.getByKey("version");
        return Integer.parseInt(version.getValue());
    }

    public void maintaining() {
        log.info("{} maintaining {}", "=".repeat(30), "=".repeat(30));
        appEnv.setMaintaining(true);
        try {
            // check the existence of a database file
            log.info("DB version: {}", getDbVersion());
        } catch (Exception e) {
            // initial creation of a database file
            initRepository.init();
            log.warn("Created new DB file");
        }

        // updating
        switch (getDbVersion()) {
            case 1:
                v2_update();
        }

        appEnv.setMaintaining(false);
        log.info("{} end {}", "=".repeat(34), "=".repeat(34));
    }

    public void execute(String sql) {
        initRepository.execute(sql);
    }

    private void v2_update() {
        log.info("updating to version {}", 2);

        try {
            execute("ALTER TABLE domains ADD IF NOT EXISTS favorite boolean DEFAULT false;");
            execute("UPDATE variables SET value = 2 WHERE key = 'version';");
            log.info("update finished successfully, current version {}", getDbVersion());
        } catch (Exception e) {
            log.error("updating exception, msg {}", e.getMessage());
        }
    }


}
