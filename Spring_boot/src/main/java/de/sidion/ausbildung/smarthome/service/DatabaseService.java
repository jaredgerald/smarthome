package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.database.entity.DeviceDataId;
import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceDataRepository;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceRepository;
import de.sidion.ausbildung.smarthome.database.repository.IDeviceTypeRepository;
import de.sidion.ausbildung.smarthome.database.repository.IModeRepository;
import de.sidion.ausbildung.smarthome.dto.DeviceDTO;
import de.sidion.ausbildung.smarthome.dto.DeviceTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final IDeviceDataRepository deviceDataRepository;
    private final IDeviceRepository deviceRepository;
    private final IDeviceTypeRepository deviceTypeRepository;
    private final IModeRepository modeRepository;

    public Device findDevice(int id) {
        return deviceRepository.findById(id)
                .orElseThrow(NoResultException::new);
    }

    public List<Device> findDevicesByType(DeviceType deviceType) {
        return deviceRepository.findDevicesByDeviceType(deviceType);
    }

    public DeviceType findDeviceTypeByName(String name) {
        return deviceTypeRepository.findByName(name).orElseThrow(NoResultException::new);
    }

    public Optional<DeviceType> findDeviceType(String name) {
        return deviceTypeRepository.findByName(name);
    }

    public boolean existsModeByNameAndType(String name, DeviceType deviceType) {
        return modeRepository.existsByNameAndDeviceType(name, deviceType);
    }

    public boolean existsDeviceById(int id) {
        return deviceRepository.existsById(id);
    }

    public List<Device> findAllDeviceDTOs() {
        return deviceRepository.findAll();
    }

    public List<DeviceType> findAllDeviceTypes() {
        return deviceTypeRepository.findAll();
    }

    public Optional<DeviceData> findLastDataOfDeviceByIdAndDataType(int id, String dataType) {
        return deviceDataRepository.findLastDeviceDataByIdAndType(id, dataType);
    }

    public Page<DeviceData> findDataOfDeviceByIdAndDataType(int id, String dataType, int page, int size) {
        Pageable sortedByName = PageRequest.of(page, size, Sort.Direction.DESC);
        return deviceDataRepository.findDeviceDataByIdAndType(id, dataType, sortedByName);
    }

    @Transactional
    public void saveDeviceData(int id, String type, String data) {
        DeviceDataId deviceDataId = new DeviceDataId();
        deviceDataId.setDeviceId(id);
        deviceDataId.setDate(LocalDateTime.now());

        DeviceData deviceData = new DeviceData();
        deviceData.setId(deviceDataId);
        deviceData.setDataType(type);
        deviceData.setData(data);
        deviceDataRepository.save(deviceData);
    }

    @Transactional
    public Device saveDevice(DeviceDTO deviceDTO) {
        Device device = new Device();
        device.setName(deviceDTO.getName());
        device.setLocation(deviceDTO.getLocation());
        DeviceType deviceType = findDeviceTypeByName(deviceDTO.getDeviceType());
        device.setDeviceType(deviceType);

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
        DeviceType deviceType = findDeviceTypeByName(deviceDTO.getDeviceType());
        device.setDeviceType(deviceType);

        return device;
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
