package de.sidion.ausbildung.smarthome.database.repository;

import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import de.sidion.ausbildung.smarthome.database.entity.Mode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IModeRepository extends JpaRepository<Mode, Integer> {
    boolean existsByNameAndDeviceType(String name, DeviceType deviceType);
}
