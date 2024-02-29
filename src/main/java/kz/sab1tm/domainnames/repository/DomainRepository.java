package kz.sab1tm.domainnames.repository;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.repository.mapper.DomainMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@AllArgsConstructor
public class DomainRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DomainMapper mapper;

    public void create(Domain entity) {
        String sql = "INSERT INTO domains (name, release_date, check_date) VALUES (?, ?, ?)";
        Date __releaseDate = entity.getReleaseDate() != null ? Date.valueOf(entity.getReleaseDate()) : null;
        Date __checkDate = entity.getCheckDate() != null ? Date.valueOf(entity.getCheckDate()) : null;
        jdbcTemplate.update(sql, entity.getName(), __releaseDate, __checkDate);
    }

    public void update(String name, LocalDate releaseDate, LocalDate checkDate) {
        String sql = "UPDATE domains SET release_date = ?, check_date = ? WHERE name = ?";
        Date __releaseDate = releaseDate != null ? Date.valueOf(releaseDate) : null;
        Date __checkDate = checkDate != null ? Date.valueOf(checkDate) : null;
        jdbcTemplate.update(sql, __releaseDate, __checkDate, name);
    }

    public void deleteByName(String name) {
        String sql = "DELETE FROM domains WHERE name = ?";
        jdbcTemplate.update(sql, name);
    }

    public Domain getByName(String param) {
        String sql = "SELECT * FROM domains WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, mapper, param);
    }

    public List<Domain> getAll() {
        String sql = "SELECT * FROM domains";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Domain.class));
    }
}
