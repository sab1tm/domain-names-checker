package kz.sab1tm.domainnames.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class InitRepository {

    private final JdbcTemplate jdbcTemplate;

    public void init() {
        String sql = """
                CREATE TABLE variables (key VARCHAR, value VARCHAR);
                INSERT INTO variables VALUES ('version', '1');
                
                CREATE TABLE domains (
                    name VARCHAR, release_date DATE, check_date DATE, 
                    status VARCHAR, source VARCHAR, 
                    error_code VARCHAR, error_text VARCHAR
                );
                """;
        jdbcTemplate.execute(sql);
    }

    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }
}
