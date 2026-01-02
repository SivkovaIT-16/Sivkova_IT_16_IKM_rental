package com.rental.app.service;

import com.rental.app.model.entity.AvailableEquipment;
import com.rental.app.model.entity.EquipmentType;
import com.rental.app.model.entity.RentalPoint;
import com.rental.app.repository.AvailableEquipmentRepository;
import com.rental.app.repository.EquipmentTypeRepository;
import com.rental.app.repository.RentalPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvailableEquipmentService {
    private final AvailableEquipmentRepository availableEquipmentRepository;
    private final RentalPointRepository rentalPointRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    public AvailableEquipmentService(
            AvailableEquipmentRepository availableEquipmentRepository,
            RentalPointRepository rentalPointRepository,
            EquipmentTypeRepository equipmentTypeRepository) {
        this.availableEquipmentRepository = availableEquipmentRepository;
        this.rentalPointRepository = rentalPointRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    // Получить все записи (для списка)
    public List<AvailableEquipment> getAllAvailableEquipments() {
        return availableEquipmentRepository.findAll();
    }

    // Получить по ID (для просмотра/редактирования)
    public Optional<AvailableEquipment> getAvailableEquipmentById(Long id) {
        return availableEquipmentRepository.findById(id);
    }

    // Добавить новую запись
    public AvailableEquipment saveAvailableEquipment(AvailableEquipment availableEquipment) {
        validateRentalPointAndEquipmentType(availableEquipment);

        Long pointId = availableEquipment.getRentalPoint().getId();
        Long typeId = availableEquipment.getEquipmentType().getId();

        if (availableEquipmentRepository.existsByRentalPointIdAndEquipmentTypeId(pointId, typeId)) {
            throw new IllegalArgumentException(
                    "Этот тип инвентаря уже добавлен в данный пункт проката. " +
                            "Пожалуйста, отредактируйте существующую запись."
            );
        }

        return availableEquipmentRepository.save(availableEquipment);
    }

    // Обновить существующую запись
    public AvailableEquipment updateAvailableEquipment(Long id, AvailableEquipment availableEquipmentDetails) {
        AvailableEquipment existing = availableEquipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Запись инвентаря с ID: " + id + " не найдена."));

        validateRentalPointAndEquipmentType(availableEquipmentDetails);

        Long newPointId = availableEquipmentDetails.getRentalPoint().getId();
        Long newTypeId = availableEquipmentDetails.getEquipmentType().getId();
        Long oldPointId = existing.getRentalPoint().getId();
        Long oldTypeId = existing.getEquipmentType().getId();

        if (!oldPointId.equals(newPointId) || !oldTypeId.equals(newTypeId)) {
            if (availableEquipmentRepository.existsByRentalPointIdAndEquipmentTypeId(newPointId, newTypeId)) {
                throw new IllegalArgumentException("Этот тип инвентаря уже добавлен в данный пункт проката.");
            }
        }

        existing.setRentalPoint(availableEquipmentDetails.getRentalPoint());
        existing.setEquipmentType(availableEquipmentDetails.getEquipmentType());
        existing.setTotalCount(availableEquipmentDetails.getTotalCount());
        existing.setAvailableCount(availableEquipmentDetails.getAvailableCount());
        existing.setCost(availableEquipmentDetails.getCost());

        return availableEquipmentRepository.save(existing);
    }

    // Удалить запись
    public void deleteAvailableEquipment(Long id) {
        if (!availableEquipmentRepository.existsById(id)) {
            throw new RuntimeException("Запись инвентаря с ID: " + id + " не найдена.");
        }
        availableEquipmentRepository.deleteById(id);
    }

    // Найти весь инвентарь в пункте проката
    public List<AvailableEquipment> getEquipmentByRentalPoint(Long rentalPointId) {
        return availableEquipmentRepository.findByRentalPointId(rentalPointId);
    }

    // Найти весь инвентарь определенного типа
    public List<AvailableEquipment> getEquipmentByType(Long equipmentTypeId) {
        return availableEquipmentRepository.findByEquipmentTypeId(equipmentTypeId);
    }

    // Найти запись по пункту и типу инвентаря
    public Optional<AvailableEquipment> getEquipmentByPointAndType(Long rentalPointId, Long equipmentTypeId) {
        return availableEquipmentRepository.findByRentalPointIdAndEquipmentTypeId(rentalPointId, equipmentTypeId);
    }

    // Поиск доступного инвентаря по минимальному количеству
    public List<AvailableEquipment> getEquipmentWithMinAvailable(Integer minCount) {
        return availableEquipmentRepository.findByAvailableCountGreaterThan(minCount);
    }

    // Арендовать инвентарь
    public void rentEquipment(Long equipmentId, int quantity) {
        AvailableEquipment equipment = availableEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Запись инвентаря с ID: " + equipmentId + " не найдена."));

        equipment.rentEquipment(quantity);
        availableEquipmentRepository.save(equipment);
    }

    //Вернуть инвентарь
    public void returnEquipment(Long equipmentId, int quantity) {
        AvailableEquipment equipment = availableEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Запись инвентаря с ID: " + equipmentId + " не найдена."));

        equipment.returnEquipment(quantity);
        availableEquipmentRepository.save(equipment);
    }

    // Получить все пункты проката с доступным инвентарём
    public List<RentalPoint> getAllRentalPoints() {
        return rentalPointRepository.findAll();
    }

    // Получить все типы доступного инвентаря
    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    // Проверить доступность инвентаря по ID
    public boolean isEquipmentAvailable(Long equipmentId) {
        Optional<AvailableEquipment> equipment = availableEquipmentRepository.findById(equipmentId);
        return equipment.isPresent() && equipment.get().isAvailable();
    }

    // Получить количество типов доступного инвентаря (availableCount > 0)
    public long getAvailableEquipmentsCountWithStock() {
        return availableEquipmentRepository.findByAvailableCountGreaterThan(0).size();
    }

    // Получить общее количество записей
    public long getAvailableEquipmentsCount() {
        return availableEquipmentRepository.count();
    }

    // Проверка существования пункта и типа
    private void validateRentalPointAndEquipmentType(AvailableEquipment availableEquipment) {
        RentalPoint rentalPoint = availableEquipment.getRentalPoint();
        if (rentalPoint == null || rentalPoint.getId() == null) {
            throw new IllegalArgumentException("Пункт проката должен быть указан.");
        }

        if (!rentalPointRepository.existsById(rentalPoint.getId())) {
            throw new IllegalArgumentException("Указанный пункт проката не существует.");
        }

        EquipmentType equipmentType = availableEquipment.getEquipmentType();
        if (equipmentType == null || equipmentType.getId() == null) {
            throw new IllegalArgumentException("Тип инвентаря должен быть указан.");
        }

        if (!equipmentTypeRepository.existsById(equipmentType.getId())) {
            throw new IllegalArgumentException("Указанный тип инвентаря не существует.");
        }
    }
}
