package de.sidion.ausbildung.smarthome.dto;

import de.sidion.ausbildung.smarthome.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 30)
    @Pattern(regexp = "^[\\S]+$")
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[\\S]+$")
    private String location;

    private DeviceType deviceType;
}
