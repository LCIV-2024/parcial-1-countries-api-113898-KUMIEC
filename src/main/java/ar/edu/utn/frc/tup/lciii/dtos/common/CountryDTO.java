package ar.edu.utn.frc.tup.lciii.dtos.common;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryDTO {
    private String code;
    private String name;
}
