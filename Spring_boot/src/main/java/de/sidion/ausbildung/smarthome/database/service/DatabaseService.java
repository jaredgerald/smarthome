package de.sidion.ausbildung.smarthome.database.service;

import de.sidion.ausbildung.smarthome.database.entity.*;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceDataRepository;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceRepository;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceTypeRepository;
import de.sidion.ausbildung.smarthome.database.repository.IModeRepository;
import de.sidion.ausbildung.smarthome.dto.DeviceDTO;
import de.sidion.ausbildung.smarthome.dto.DeviceTypeDTO;
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
    private final IDeviceTypeRepository deviceTypeRepository;
    private final IModeRepository modeRepository;

    public Device findDeviceById(int id) {
        return deviceRepository.findById(id).orElseThrow(NoResultException::new);
    }

    public DeviceType findDeviceTypeByName(String name) {
        return deviceTypeRepository.findByName(name).orElseThrow(NoResultException::new);
    }

    public boolean existsModeByNameAndType(String name, DeviceType deviceType) {
        return modeRepository.existsByNameAndDeviceType(name, deviceType);
    }

    public boolean existsDeviceById(int id) {
        return deviceRepository.existsById(id);
    }

    public List<Device> findAllDevices() {
        return deviceRepository.findAll();
    }

    public List<DeviceType> findAllDeviceTypes() {
        return deviceTypeRepository.findAll();
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
        device.setDeviceType(deviceDTO.getDeviceType().getType());
        return deviceRepository.save(device);
    }

    @Transactional
    public DeviceType saveDeviceType(DeviceTypeDTO deviceTypeDTO) {
        return deviceTypeRepository.save(deviceTypeDTO.getType());
    }

    @Transactional
    public Device updateDevice(int id, DeviceDTO deviceDTO) {
        Device device = deviceRepository.findById(id).orElseThrow(NoResultException::new);
        device.setName(deviceDTO.getName());
        device.setLocation(deviceDTO.getLocation());
        device.setDeviceType(deviceDTO.getDeviceType().getType());
        return device;
    }

    @Transactional
    public void updateDeviceTimestamp(int id, LocalDateTime date) {
        Device device = deviceRepository.findById(id).orElseThrow(NoResultException::new);
        device.setLastTimestamp(date);
    }

    @Transactional
    public void updateDeviceStatus(int id, String status) {
        Device device = deviceRepository.findById(id).orElseThrow(NoResultException::new);
        device.setState(status);
    }

    @Transactional
    public void deleteDevice(int id) {
        deviceRepository.deleteById(id);
    }

    @Transactional
    public void deleteDeviceType(String name) {
        deviceTypeRepository.deleteByName(name);
    }
}
