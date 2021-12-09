package de.sidion.ausbildung.smarthome.database.repository;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDeviceRepository extends JpaRepository<Device, Integer> {
    List<Device> findDevicesByDeviceType(DeviceType deviceType);
}
