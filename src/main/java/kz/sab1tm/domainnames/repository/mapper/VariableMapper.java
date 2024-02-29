package kz.sab1tm.domainnames.repository.mapper;

import kz.sab1tm.domainnames.model.Variable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class VariableMapper implements RowMapper<Variable> {

    @Override
    public Variable mapRow(ResultSet rs, int rowNum) throws SQLException {
        Variable variable = new Variable();
        variable.setKey(rs.getString("key"));
        variable.setValue(rs.getString("value"));
        return variable;
    }
}