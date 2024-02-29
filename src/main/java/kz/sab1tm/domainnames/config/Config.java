package kz.sab1tm.domainnames.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class Config {

    private final AppEnv appEnv;

    public Config(AppEnv appEnv) {
        this.appEnv = appEnv;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        JdbcTemplate duckdb =new JdbcTemplate();
        duckdb.setDataSource(dataSource);
        return duckdb;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.duckdb.DuckDBDriver");
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(3);
        config.setJdbcUrl("jdbc:duckdb:" + appEnv.getDbFileName());
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }
}
