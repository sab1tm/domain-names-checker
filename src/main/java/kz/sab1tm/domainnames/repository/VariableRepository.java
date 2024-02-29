package kz.sab1tm.domainnames.repository;

import kz.sab1tm.domainnames.model.Variable;
import kz.sab1tm.domainnames.repository.mapper.VariableMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class VariableRepository {

    private final JdbcTemplate jdbcTemplate;
    private final VariableMapper variableMapper;

    public Variable getByKey(String key) {
        String sql = "SELECT * FROM variables WHERE key = ?";
        return jdbcTemplate.queryForObject(sql, variableMapper, key);
    }
}
