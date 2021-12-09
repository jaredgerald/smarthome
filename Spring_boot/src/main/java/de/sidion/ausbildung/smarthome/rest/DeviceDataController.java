package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.service.DatabaseService;
import de.sidion.ausbildung.smarthome.dto.DeviceDataDTO;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device/data")
public class DeviceDataController {
    private final DatabaseService databaseService;
    private final ResponseService responseService;

    @GetMapping("/{id}/{selector}")
    public ResponseEntity<DeviceDataDTO> getLastDataOfDevice(@PathVariable("id") int id,
                                                             @PathVariable("selector") String selector) {
        final Optional<DeviceData> data = databaseService.findLastDataOfDeviceByIdAndDataType(id, selector);
        DeviceDataDTO deviceDataDTO = data.map(DeviceDataDTO::new).orElse(null);
        return responseService.createSendResponse(HttpStatus.OK, deviceDataDTO);
    }

    @GetMapping("/{id}/history/{selector}")
    public ResponseEntity<List<DeviceDataDTO>> getDataHistoryOfDevice(@PathVariable("id") int id,
                                                                      @PathVariable("selector") String selector,
                                                                      @RequestParam("page") int page,
                                                                      @RequestParam("size") int size) {
        Page<DeviceData> dataList = databaseService.findDataOfDeviceByIdAndDataType(id, selector, page, size);

        return responseService.createSendResponse(HttpStatus.OK,
                dataList.stream().map(DeviceDataDTO::new)
                        .collect(Collectors.toList()));
    }
}
