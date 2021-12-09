package de.sidion.ausbildung.smarthome.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeDTO {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 30)
    private String name;
}
