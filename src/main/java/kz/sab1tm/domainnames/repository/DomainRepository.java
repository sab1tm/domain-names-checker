package kz.sab1tm.domainnames.repository;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.enumeration.DomainStatusEnum;
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
        String sql = "INSERT INTO domains (name, release_date, check_date_time, status, source, error_code, error_text) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql, entity.getName(), entity.getReleaseDate(),
                entity.getCheckDateTime(), entity.getStatus().toString(),
                entity.getSource().toString(), entity.getErrorCode() != null ? entity.getErrorCode().toString() : null,
                entity.getErrorText()
        );
    }

    public void update(Domain entity) {
        String sql = "UPDATE domains SET release_date = ?, check_date_time = ?, status = ?, source = ?, error_code = ?, error_text = ? WHERE name = ?";
        jdbcTemplate.update(
                sql, entity.getReleaseDate(), entity.getCheckDateTime(),
                entity.getStatus().toString(), entity.getSource().toString(),
                entity.getErrorCode() != null ? entity.getErrorCode().toString() : null,
                entity.getErrorText(), entity.getName()
        );
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
        String sql = "SELECT * FROM domains ORDER BY length(name)";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getLimitTodayReleases(int limit) {
        String sql = "SELECT * FROM domains WHERE status = ? AND release_date = ? ORDER BY check_date_time LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{DomainStatusEnum.HOLDED.toString(), Date.valueOf(LocalDate.now()), limit},
                new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getByStatus(DomainStatusEnum status) {
        String sql = "SELECT * FROM domains WHERE status = ? ORDER BY length(name)";
        return jdbcTemplate.query(sql, new Object[]{status.toString()},
                new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getLimitOldTodayReleases(int limit) {
        String sql = "SELECT * FROM domains WHERE status = ? AND release_date < ? ORDER BY check_date_time LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{DomainStatusEnum.HOLDED.toString(), Date.valueOf(LocalDate.now()), limit},
                new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getTodayReleases() {
        String sql = "SELECT * FROM domains WHERE (status = ? OR status = ?) AND release_date = ? ORDER BY length(name)";
        return jdbcTemplate.query(sql, new Object[]{DomainStatusEnum.HOLDED.toString(), DomainStatusEnum.AVAILABLE.toString(), Date.valueOf(LocalDate.now())},
                new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getTomorrowReleases() {
        String sql = "SELECT * FROM domains WHERE status = ? AND release_date = ? ORDER BY length(name)";
        return jdbcTemplate.query(sql, new Object[]{DomainStatusEnum.HOLDED.toString(), Date.valueOf(LocalDate.now().plusDays(1))},
                new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getAvailable() {
        String sql = "SELECT * FROM domains WHERE status = ? ORDER BY length(name)";
        return jdbcTemplate.query(sql, new Object[]{DomainStatusEnum.AVAILABLE.toString()},
                new BeanPropertyRowMapper<>(Domain.class));
    }

    public List<Domain> getTaken() {
        String sql = "SELECT * FROM domains WHERE status = ? ORDER BY length(name)";
        return jdbcTemplate.query(sql, new Object[]{DomainStatusEnum.TAKEN.toString()},
                new BeanPropertyRowMapper<>(Domain.class));
    }
}
