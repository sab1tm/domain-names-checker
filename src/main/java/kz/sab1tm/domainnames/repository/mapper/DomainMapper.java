package kz.sab1tm.domainnames.repository.mapper;

import kz.sab1tm.domainnames.model.Domain;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Service
public class DomainMapper implements RowMapper<Domain> {

    @Override
    public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
        Domain entity = new Domain();
        entity.setName(rs.getString("name"));
        Date releaseDate = rs.getDate("release_date");
        if (Objects.nonNull(releaseDate))
            entity.setReleaseDate(releaseDate.toLocalDate());
        Date checkDate = rs.getDate("check_date");
        if (Objects.nonNull(checkDate))
            entity.setCheckDate(checkDate.toLocalDate());
        return entity;
    }
}