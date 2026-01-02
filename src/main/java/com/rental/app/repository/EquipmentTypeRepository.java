package com.rental.app.repository;

import com.rental.app.model.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long>{
    // Поиск типа инвентаря по названию
    List<EquipmentType> findByTypeNameContainingIgnoreCase(String typeName);

    // Поиск типа инвентаря по категории
    List<EquipmentType> findByCategoryContainingIgnoreCase(String category);

    // Проверка существования типа инвентаря по названию
    boolean existsByTypeName(String typeName);
}
