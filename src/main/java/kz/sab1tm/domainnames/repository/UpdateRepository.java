package kz.sab1tm.domainnames.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UpdateRepository {

    private final JdbcTemplate jdbcTemplate;

    public UpdateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        String sql = """
                CREATE TABLE variables (key VARCHAR, value VARCHAR);
                INSERT INTO variables VALUES ('version', '1');
                """;
        jdbcTemplate.execute(sql);
    }
}
