package de.sidion.ausbildung.smarthome.dto;

import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

@Value
public class DeviceTypeDTO {

    public DeviceTypeDTO(String deviceType) {
        deviceType = deviceType.replace(" ", "_");
        deviceType = deviceType.toUpperCase(Locale.ROOT);

        this.type = new DeviceType();
        this.type.setName(deviceType);
    }

    @NotNull
    @NotBlank
    @Size(min = 4, max = 30)
    DeviceType type;
}
