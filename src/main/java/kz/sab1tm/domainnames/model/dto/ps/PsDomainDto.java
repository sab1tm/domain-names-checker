package kz.sab1tm.domainnames.model.dto.ps;

import com.fasterxml.jackson.annotation.JsonAlias;

public record PsDomainDto(
        String dname,
        PsDomainResultEnum result,
        @JsonAlias("error_code") PsErrorCodeEnum errorCode,
        @JsonAlias("error_text") String errorText
) {
}