package com.rental.app.service;

import com.rental.app.entity.EquipmentType;
import com.rental.app.repository.EquipmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с типами инвентаря ({@link EquipmentType}).
 * <p>
 * Содержит бизнес-логику для управления типами инвентаря, включая создание,
 * обновление, удаление и поиск. Гарантирует уникальность названий типов инвентаря.
 * Каждый метод выполняется в отдельной транзакции базы данных.
 * </p>
 * <p>
 * <b>Транзакции:</b> Spring автоматически управляет транзакциями -
 * открывает перед вызовом метода и закрывает после его завершения.
 * При возникновении исключения все изменения в базе данных откатываются.
 * </p>
 *
 * @see EquipmentType
 * @see EquipmentTypeRepository
 * @see Service
 * @see Transactional
 */
@Service
@Transactional
public class EquipmentTypeService {
    private final EquipmentTypeRepository equipmentTypeRepository;

    /**
     * Создает сервис с указанным репозиторием типов инвентаря.
     *
     * @param equipmentTypeRepository репозиторий для доступа к данным типов инвентаря
     */
    @Autowired
    public EquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    /**
     * Получает все типы инвентаря из системы.
     *
     * @return список всех типов инвентаря (может быть пустым)
     */
    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    /**
     * Находит тип инвентаря по его идентификатору.
     * <p>
     * Использует {@link Optional} для корректной обработки случаев,
     * когда тип инвентаря не найден.
     * </p>
     *
     * @param id идентификатор типа инвентаря
     * @return {@link Optional}, содержащий тип инвентаря, если найден,
     * или пустой {@link Optional}, если не найден
     */
    public Optional<EquipmentType> getEquipmentTypeById(Long id) {
        return equipmentTypeRepository.findById(id);
    }

    /**
     * Сохраняет новый тип инвентаря в системе.
     * <p>
     * Перед сохранением проверяет уникальность названия типа инвентаря.
     * Проверка выполняется без учета регистра символов.
     * </p>
     *
     * @param equipmentType тип инвентаря для сохранения
     * @return сохраненный тип инвентаря
     * @throws IllegalArgumentException если тип инвентаря с таким названием уже существует в системе
     * @see EquipmentTypeRepository#existsByTypeName(String)
     */
    public EquipmentType saveEquipmentType(EquipmentType equipmentType) {
        if (equipmentTypeRepository.existsByTypeName(equipmentType.getTypeName())) {
            throw new IllegalArgumentException(
                    "Тип инвентаря с названием \"" + equipmentType.getTypeName() + "\" уже существует.");
        }
        return equipmentTypeRepository.save(equipmentType);
    }

    /**
     * Обновляет существующий тип инвентаря.
     * <p>
     * Находит тип инвентаря по идентификатору, проверяет уникальность нового названия
     * (если название было изменено), обновляет поля и сохраняет изменения.
     * Проверка уникальности выполняется без учета регистра символов.
     * </p>
     *
     * @param id идентификатор обновляемого типа инвентаря
     * @param equipmentTypeDetails объект с новыми значениями полей
     * @return обновленный тип инвентаря
     * @throws RuntimeException если тип инвентаря с указанным id не найден
     * @throws IllegalArgumentException если новое название уже используется другим типом инвентаря
     */
    public EquipmentType updateEquipmentType(Long id, EquipmentType equipmentTypeDetails) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Тип инвентаря с Id: " + id + " не найден."));

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

    /**
     * Удаляет тип инвентаря по идентификатору.
     * <p>
     * Проверяет существование типа инвентаря перед удалением.
     * </p>
     *
     * @param id идентификатор удаляемого типа инвентаря
     * @throws RuntimeException если тип инвентаря с указанным id не найден
     * @see EquipmentTypeRepository#deleteById(Object)
     */
    public void deleteEquipmentType(Long id) {
        if (!equipmentTypeRepository.existsById(id)) {
            throw new RuntimeException("Тип инвентаря с ID: " + id + " не найден.");
        }
        equipmentTypeRepository.deleteById(id);
    }

    /**
     * Ищет типы инвентаря по части названия.
     * <p>
     * Поиск выполняется без учета регистра символов.
     * </p>
     *
     * @param typeName подстрока для поиска в названии типа инвентаря
     * @return список найденных типов инвентаря (может быть пустым)
     * @see EquipmentTypeRepository#findByTypeNameContainingIgnoreCase(String)
     */
    public List<EquipmentType> searchByTypeName(String typeName) {
        return equipmentTypeRepository.findByTypeNameContainingIgnoreCase(typeName);
    }

    /**
     * Ищет типы инвентаря по части категории.
     * <p>
     * Поиск выполняется без учета регистра символов.
     * </p>
     *
     * @param category подстрока для поиска в категории типа инвентаря
     * @return список найденных типов инвентаря (может быть пустым)
     * @see EquipmentTypeRepository#findByCategoryContainingIgnoreCase(String)
     */
    public List<EquipmentType> searchByCategory(String category) {
        return equipmentTypeRepository.findByCategoryContainingIgnoreCase(category);
    }

    /**
     * Возвращает общее количество типов инвентаря в системе.
     *
     * @return количество типов инвентаря
     * @see EquipmentTypeRepository#count()
     */
    public long getEquipmentTypeCount() {
        return equipmentTypeRepository.count();
    }
}
