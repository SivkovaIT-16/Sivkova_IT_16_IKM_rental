package com.rental.app.service;

import com.rental.app.model.entity.EquipmentType;
import com.rental.app.repository.EquipmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipmentTypeService {
    private final EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    public EquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    // Получение всех типов инвентаря
    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    // Получение типов инвентаря по Id
    public Optional<EquipmentType> getEquipmentTypeById(Long id) {
        return equipmentTypeRepository.findById(id);
    }

    // Сохранение нового типа инвентаря
    public EquipmentType saveEquipmentType(EquipmentType equipmentType) {
        // Проверка уникальности типа
        if (equipmentTypeRepository.existsByTypeName(equipmentType.getTypeName())) {
            throw new IllegalArgumentException(
                    "Тип инвентаря с названием \"" + equipmentType.getTypeName() + "\" уже существует.");
        }
        return equipmentTypeRepository.save(equipmentType);
    }

    // Обновление уже существующего типа инвентаря
    public EquipmentType updateEquipmentType(Long id, EquipmentType equipmentTypeDetails) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Тип инвентаря с Id: " + id + " не найден."));

        // Проверка уникальности типа
        if (!equipmentType.getTypeName().equals(equipmentTypeDetails.getTypeName()) &&
                equipmentTypeRepository.existsByTypeName(equipmentTypeDetails.getTypeName())) {
            throw new IllegalArgumentException(
                    "Тип инвентаря с названием \"" + equipmentTypeDetails.getTypeName() + "\" уже существует.");
        }

        equipmentType.setTypeName(equipmentTypeDetails.getTypeName());
        equipmentType.setCategory(equipmentTypeDetails.getCategory());
        equipmentType.setDescription(equipmentTypeDetails.getDescription());

        return equipmentTypeRepository.save(equipmentType);
    }

    // Удаление типа инвентаря
    public void deleteEquipmentType(Long id) {
        equipmentTypeRepository.deleteById(id);
    }

    // Поиск типа инвентаря по названию
    public List<EquipmentType> searchByTypeName(String typeName) {
        return equipmentTypeRepository.findByTypeNameContainingIgnoreCase(typeName);
    }

    // Поиск типа инвентаря по категории
    public List<EquipmentType> searchByCategory(String category) {
        return equipmentTypeRepository.findByCategoryContainingIgnoreCase(category);
    }

    // Получение общего количества типов инвентаря
    public long getEquipmentTypeCount() {
        return equipmentTypeRepository.count();
    }
}
