package de.sidion.ausbildung.smarthome.database.service;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.database.entity.DeviceDataId;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceDataRepository;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceRepository;
import de.sidion.ausbildung.smarthome.dto.DeviceDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DatabaseService {
    private final IDeviceDataRepository deviceDataRepository;
    private final IDeviceRepository deviceRepository;

    public Device findDeviceById(int id) {
        return deviceRepository.findById(id).orElseThrow(NoResultException::new);
    }

    public boolean existsDeviceById(int id) {
        return deviceRepository.existsById(id);
    }

    public List<Device> findAllDevices() {
        return deviceRepository.findAll();
    }

    public DeviceData findLastDataOfDevice(int id) {
        return deviceDataRepository.findDeviceDataById(id).stream().findFirst()
                .orElseThrow(NoResultException::new);
    }

    public List<DeviceData> findAllDataOfDevice(int id) {
        return deviceDataRepository.findDeviceDataById(id);
    }

    @Transactional
    public void saveDeviceData(int id, String data) {
        DeviceDataId deviceDataId = new DeviceDataId();
        deviceDataId.setDeviceId(id);
        deviceDataId.setDate(LocalDateTime.now());

        DeviceData deviceData = new DeviceData();
        deviceData.setId(deviceDataId);
        deviceData.setData(data);
        deviceDataRepository.save(deviceData);
    }

    @Transactional
    public Device saveDevice(DeviceDTO deviceDTO) {
        Device device = new Device();
        device.setName(deviceDTO.getName());
        device.setLocation(deviceDTO.getLocation());
        device.setType(deviceDTO.getDeviceType());
        return deviceRepository.save(device);
    }

    @Transactional
    public Device updateDevice(int id, DeviceDTO deviceDTO) {
        Device device = deviceRepository.findById(id).orElseThrow(NoResultException::new);
        device.setName(deviceDTO.getName());
        device.setLocation(deviceDTO.getLocation());
        device.setType(deviceDTO.getDeviceType());
        return device;
    }

    @Transactional
    public void updateDeviceState(int id, boolean isActive) {
        Device device = deviceRepository.findById(id).orElseThrow(NoResultException::new);
        device.setActive(isActive);
    }

    @Transactional
    public void updateDeviceStatus(int id, String status) {
        Device device = deviceRepository.findById(id).orElseThrow(NoResultException::new);
        device.setStatus(status);
    }

    @Transactional
    public void deleteDevice(int id) {
        deviceRepository.deleteById(id);
    }
}
