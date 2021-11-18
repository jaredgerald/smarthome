package de.sidion.ausbildung.smarthome.database.repository;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDeviceRepository extends JpaRepository<Device, Integer> {
    Device findDeviceByName(String name);
}
