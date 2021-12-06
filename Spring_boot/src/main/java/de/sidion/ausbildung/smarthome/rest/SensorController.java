package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import de.sidion.ausbildung.smarthome.dto.DeviceDataDTO;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorController {
    private final DatabaseService databaseService;
    private final ResponseService responseService;

    @GetMapping("/{id}")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SENSOR')")
    public ResponseEntity<DeviceDataDTO> getCurrentDataOfDevice(@PathVariable("id") int id) {
        final DeviceData data = databaseService.findLastDataOfDevice(id);
        return responseService.createSendResponse(HttpStatus.OK, new DeviceDataDTO(data));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SENSOR')")
    public ResponseEntity<List<DeviceDataDTO>> getDataHistoryOfDevice (@PathVariable("id") int id) {
        final List<DeviceData> dataList = databaseService.findAllDataOfDevice(id);
        return responseService.createSendResponse(HttpStatus.OK,
                dataList.stream().map(DeviceDataDTO::new).collect(Collectors.toList()));
    }
}
