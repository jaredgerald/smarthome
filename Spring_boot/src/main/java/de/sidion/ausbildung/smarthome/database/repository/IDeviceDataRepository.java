package de.sidion.ausbildung.smarthome.database.repository;

import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.database.entity.DeviceDataId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IDeviceDataRepository extends JpaRepository<DeviceData, DeviceDataId> {
    @Query("SELECT d from DeviceData d WHERE d.id.deviceId = ?1 AND d.dataType = ?2 ORDER BY d.id.date DESC")
    Page<DeviceData> findDeviceDataByIdAndType(int id, String dataType, Pageable pageable);

    @Query(value = "SELECT * from device_data d WHERE d.device_id = ?1 AND d.data_type = ?2 ORDER BY d.date DESC limit 1", nativeQuery = true)
    Optional<DeviceData> findLastDeviceDataByIdAndType(int id, String dataType);
}
