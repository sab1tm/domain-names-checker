package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.model.Variable;
import kz.sab1tm.domainnames.repository.UpdateRepository;
import kz.sab1tm.domainnames.repository.VariableRepository;
import org.springframework.stereotype.Service;

@Service
public class DbService {

    private final VariableRepository variableRepository;
    private final UpdateRepository updateRepository;

    public DbService(VariableRepository variableRepository, UpdateRepository updateRepository) {
        this.variableRepository = variableRepository;
        this.updateRepository = updateRepository;
    }

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
