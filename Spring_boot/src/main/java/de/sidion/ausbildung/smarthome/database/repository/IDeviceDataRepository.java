package de.sidion.ausbildung.smarthome.database.repository;

import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.database.entity.DeviceDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IDeviceDataRepository extends JpaRepository<DeviceData, DeviceDataId> {
    @Query("SELECT d from DeviceData d WHERE d.id.deviceId = ?1 ORDER BY d.id.date DESC")
    List<DeviceData> findDeviceDataById(int id);
}
