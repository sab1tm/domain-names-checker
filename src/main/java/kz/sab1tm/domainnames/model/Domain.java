package kz.sab1tm.domainnames.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    private String name;
    private LocalDate releaseDate;
    private LocalDate checkDate;
}
