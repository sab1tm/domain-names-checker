package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.model.Variable;
import kz.sab1tm.domainnames.repository.UpdateRepository;
import kz.sab1tm.domainnames.repository.VariableRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DbService {

    private final VariableRepository variableRepository;
    private final UpdateRepository updateRepository;

    private int getDbVersion() {
        Variable version = variableRepository.getByKey("version");
        return Integer.parseInt(version.getValue());
    }

    public void maintaining() {
        try {
            // check the existence of a database file
            getDbVersion();
        } catch (Exception e) {
            // initial creation of a database file
            updateRepository.init();
        }
    }
}
