package kz.sab1tm.domainnames.model;

import kz.sab1tm.domainnames.model.dto.ps.PsErrorCodeEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainSourceEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    private String name;
    private DomainSourceEnum source;
    private Date releaseDate;
    private LocalDateTime checkDateTime;
    private DomainStatusEnum status;
    private PsErrorCodeEnum errorCode;
    private String errorText;

    public String getFormattedReleaseDate() {
        if (releaseDate != null) {
            LocalDate date = releaseDate.toLocalDate();
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } else {
            return "";
        }
    }

    public String getFormattedCheckDateTime() {
        if (checkDateTime != null) {
            return checkDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        } else {
            return "";
        }
    }
}
