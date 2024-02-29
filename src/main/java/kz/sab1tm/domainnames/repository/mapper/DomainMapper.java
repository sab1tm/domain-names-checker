package kz.sab1tm.domainnames.repository.mapper;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.enumeration.DomainSource;
import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class DomainMapper implements RowMapper<Domain> {

    @Override
    public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("name");
        Date releaseDate = rs.getDate("release_date");
        Date checkDate = rs.getDate("check_date");
        DomainStatus status = DomainStatus.valueOf(rs.getString("status"));
        DomainSource source = DomainSource.valueOf(rs.getString("source"));
        return Domain.builder()
                .name(name)
                .releaseDate(releaseDate)
                .checkDate(checkDate)
                .status(status)
                .source(source)
                .build();
    }
}