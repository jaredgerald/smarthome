package de.sidion.ausbildung.smarthome.database.repository;

import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IDeviceTypeRepository extends JpaRepository<DeviceType, Integer> {
    void deleteByName(String name);
    Optional<DeviceType> findByName(String name);
}
