package kz.sab1tm.domainnames.model;

import kz.sab1tm.domainnames.model.dto.ps.PsErrorCodeEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainSource;
import kz.sab1tm.domainnames.model.enumeration.DomainStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    private String name;
    private DomainSource source;
    private Date releaseDate;
    private Date checkDate;
    private DomainStatus status;
    private PsErrorCodeEnum errorCode;
    private String errorText;
}
