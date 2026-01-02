package com.rental.app.repository;

import com.rental.app.model.entity.AvailableEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableEquipmentRepository extends JpaRepository<AvailableEquipment, Long> {
    // Найти весь инвентарь в пункте проката
    List<AvailableEquipment> findByRentalPointId(Long rentalPointId);

    // Найти весь инвентарь определенного типа
    List<AvailableEquipment> findByEquipmentTypeId(Long equipmentTypeId);

    // Найти запись по пункту и типу инвентаря
    Optional<AvailableEquipment> findByRentalPointIdAndEquipmentTypeId(Long rentalPointId, Long equipmentTypeId);

    // Поиск доступного инвентаря по минимальному количеству
    List<AvailableEquipment> findByAvailableCountGreaterThan(Integer count);

    // Проверка существования записи для пункта и типа
    boolean existsByRentalPointIdAndEquipmentTypeId(Long rentalPointId, Long equipmentTypeId);
}
