package de.sidion.ausbildung.smarthome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LEDModeDTO {
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[\\S]+$")
    private String mode;

    @Min(1)
    private int speed;

    @Min(0)
    @Max(100)
    private int brightness;

    @Valid
    private Color color;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Color {
        @Min(0)
        @Max(255)
        private int red;

        @Min(0)
        @Max(255)
        private int blue;

        @Min(0)
        @Max(255)
        private int green;
    }
}
