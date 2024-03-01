package kz.sab1tm.domainnames.repository.mapper;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.dto.ps.PsErrorCodeEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainSourceEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainStatusEnum;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class DomainMapper implements RowMapper<Domain> {

    @Override
    public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("name");
        Date releaseDate = rs.getDate("release_date");
        LocalDateTime checkDateTime = rs.getTimestamp("check_date_time").toLocalDateTime();
        DomainStatusEnum status = DomainStatusEnum.valueOf(rs.getString("status"));
        DomainSourceEnum source = DomainSourceEnum.valueOf(rs.getString("source"));
        PsErrorCodeEnum errorCode = PsErrorCodeEnum.valueOf(rs.getString("error_code"));
        String errorText = rs.getString("error_text");
        return Domain.builder()
                .name(name)
                .releaseDate(releaseDate)
                .checkDateTime(checkDateTime)
                .status(status)
                .source(source)
                .errorCode(errorCode)
                .errorText(errorText)
                .build();
    }
}