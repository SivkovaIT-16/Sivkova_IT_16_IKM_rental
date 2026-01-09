package com.rental.app.service;

import com.rental.app.entity.AvailableEquipment;
import com.rental.app.entity.EquipmentType;
import com.rental.app.entity.RentalPoint;
import com.rental.app.repository.AvailableEquipmentRepository;
import com.rental.app.repository.EquipmentTypeRepository;
import com.rental.app.repository.RentalPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с доступным инвентарем ({@link AvailableEquipment}).
 * <p>
 * Содержит бизнес-логику для управления наличием инвентаря в пунктах проката.
 * Обеспечивает связь между пунктами проката ({@link RentalPoint}) и типами инвентаря
 * ({@link EquipmentType}), управляет количеством доступных единиц и стоимостью аренды.
 * Каждый метод выполняется в отдельной транзакции базы данных.
 * </p>
 * <p>
 * <b>Транзакции:</b> Spring автоматически управляет транзакциями -
 * открывает перед вызовом метода и закрывает после его завершения.
 * При возникновении исключения все изменения в базе данных откатываются.
 * </p>
 * <p>
 * <b>Уникальность записей:</b> Комбинация "пункт проката + тип инвентаря" должна быть уникальной -
 * нельзя добавить один и тот же тип инвентаря в один пункт проката дважды.
 * </p>
 *
 * @see AvailableEquipment
 * @see RentalPoint
 * @see EquipmentType
 * @see AvailableEquipmentRepository
 * @see Service
 * @see Transactional
 */
@Service
@Transactional
public class AvailableEquipmentService {
    private final AvailableEquipmentRepository availableEquipmentRepository;
    private final RentalPointRepository rentalPointRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    /**
     * Создает сервис с указанными репозиториями.
     * <p>
     * Spring автоматически находит и передает все необходимые репозитории.
     * </p>
     *
     * @param availableEquipmentRepository репозиторий для работы с доступным инвентарем
     * @param rentalPointRepository репозиторий для работы с пунктами проката
     * @param equipmentTypeRepository репозиторий для работы с типами инвентаря
     */
    @Autowired
    public AvailableEquipmentService(
            AvailableEquipmentRepository availableEquipmentRepository,
            RentalPointRepository rentalPointRepository,
            EquipmentTypeRepository equipmentTypeRepository) {
        this.availableEquipmentRepository = availableEquipmentRepository;
        this.rentalPointRepository = rentalPointRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    /**
     * Получает все записи о доступном инвентаре из системы.
     *
     * @return список всех записей о доступном инвентаре (может быть пустым)
     */
    public List<AvailableEquipment> getAllAvailableEquipments() {
        return availableEquipmentRepository.findAll();
    }

    /**
     * Находит запись о доступном инвентаре по её идентификатору.
     * <p>
     * Использует {@link Optional} для корректной обработки случаев,
     * когда запись не найдена.
     * </p>
     *
     * @param id идентификатор записи о доступном инвентаре
     * @return {@link Optional}, содержащий запись о доступном инвентаре, если найдена,
     * или пустой {@link Optional}, если не найдена
     */
    public Optional<AvailableEquipment> getAvailableEquipmentById(Long id) {
        return availableEquipmentRepository.findById(id);
    }

    /**
     * Сохраняет новую запись о доступном инвентаре в системе.
     * <p>
     * Перед сохранением выполняет следующие проверки:
     * <ol>
     *   <li>Существование указанного пункта проката</li>
     *   <li>Существование указанного типа инвентаря</li>
     *   <li>Уникальность комбинации "пункт проката + тип инвентаря"</li>
     * </ol>
     * </p>
     *
     * @param availableEquipment запись о доступном инвентаре для сохранения
     * @return сохраненная запись о доступном инвентаре
     * @throws IllegalArgumentException если пункт проката или тип инвентаря не существуют,
     * или если комбинация уже существует в системе
     * @see #validateRentalPointAndEquipmentType(AvailableEquipment)
     * @see AvailableEquipmentRepository#existsByRentalPointIdAndEquipmentTypeId(Long, Long)
     */
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

    /**
     * Обновляет существующую запись о доступном инвентаре.
     * <p>
     * Находит запись по идентификатору, проверяет существование пункта проката и типа инвентаря,
     * проверяет уникальность новой комбинации (если пункт или тип были изменены),
     * обновляет поля и сохраняет изменения.
     * </p>
     *
     * @param id идентификатор обновляемой записи
     * @param availableEquipmentDetails объект с новыми значениями полей
     * @return обновленная запись о доступном инвентаре
     * @throws RuntimeException если запись с указанным id не найдена
     * @throws IllegalArgumentException если пункт проката или тип инвентаря не существуют,
     * или если новая комбинация уже существует в системе
     */
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

    /**
     * Удаляет запись о доступном инвентаре по идентификатору.
     * <p>
     * Проверяет существование записи перед удалением.
     * </p>
     *
     * @param id идентификатор удаляемой записи
     * @throws RuntimeException если запись с указанным id не найдена
     * @see AvailableEquipmentRepository#deleteById(Object)
     */
    public void deleteAvailableEquipment(Long id) {
        if (!availableEquipmentRepository.existsById(id)) {
            throw new RuntimeException("Запись инвентаря с ID: " + id + " не найдена.");
        }
        availableEquipmentRepository.deleteById(id);
    }

    /**
     * Находит весь инвентарь в указанном пункте проката.
     *
     * @param rentalPointId идентификатор пункта проката
     * @return список записей о доступном инвентаре в указанном пункте (может быть пустым)
     * @see AvailableEquipmentRepository#findByRentalPointId(Long)
     */
    public List<AvailableEquipment> getEquipmentByRentalPoint(Long rentalPointId) {
        return availableEquipmentRepository.findByRentalPointId(rentalPointId);
    }

    /**
     * Находит весь инвентарь указанного типа во всех пунктах проката.
     *
     * @param equipmentTypeId идентификатор типа инвентаря
     * @return список записей о наличии указанного типа инвентаря (может быть пустым)
     * @see AvailableEquipmentRepository#findByEquipmentTypeId(Long)
     */
    public List<AvailableEquipment> getEquipmentByType(Long equipmentTypeId) {
        return availableEquipmentRepository.findByEquipmentTypeId(equipmentTypeId);
    }

    /**
     * Находит конкретную запись о наличии инвентаря по пункту проката и типу инвентаря.
     *
     * @param rentalPointId идентификатор пункта проката
     * @param equipmentTypeId идентификатор типа инвентаря
     * @return {@link Optional}, содержащий запись о наличии, если найдена,
     * или пустой {@link Optional}, если не найдена
     * @see AvailableEquipmentRepository#findByRentalPointIdAndEquipmentTypeId(Long, Long)
     */
    public Optional<AvailableEquipment> getEquipmentByPointAndType(Long rentalPointId, Long equipmentTypeId) {
        return availableEquipmentRepository.findByRentalPointIdAndEquipmentTypeId(rentalPointId, equipmentTypeId);
    }

    /**
     * Находит инвентарь с количеством доступных единиц больше указанного значения.
     * <p>
     * Используется для поиска позиций, которые доступны для аренды.
     * </p>
     *
     * @param minCount минимальное количество доступных единиц (не включая это значение)
     * @return список позиций инвентаря с доступным количеством больше указанного (может быть пустым)
     * @see AvailableEquipmentRepository#findByAvailableCountGreaterThan(Integer)
     */
    public List<AvailableEquipment> getEquipmentWithMinAvailable(Integer minCount) {
        return availableEquipmentRepository.findByAvailableCountGreaterThan(minCount);
    }

    /**
     * Арендует указанное количество инвентаря.
     * <p>
     * Уменьшает количество доступных единиц после проверки наличия достаточного количества.
     * Использует бизнес-логику сущности {@link AvailableEquipment#rentEquipment(int)}.
     * </p>
     *
     * @param equipmentId идентификатор записи о доступном инвентаре
     * @param quantity количество арендуемого инвентаря
     * @throws RuntimeException если запись с указанным id не найдена
     * @throws IllegalArgumentException если количество <= 0
     * @throws IllegalStateException если доступного инвентаря недостаточно
     * @see AvailableEquipment#rentEquipment(int)
     */
    public void rentEquipment(Long equipmentId, int quantity) {
        AvailableEquipment equipment = availableEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Запись инвентаря с ID: " + equipmentId + " не найдена."));

        equipment.rentEquipment(quantity);
        availableEquipmentRepository.save(equipment);
    }

    /**
     * Возвращает указанное количество инвентаря.
     * <p>
     * Увеличивает количество доступных единиц после проверки, что общее количество не будет превышено.
     * Использует бизнес-логику сущности {@link AvailableEquipment#returnEquipment(int)}.
     * </p>
     *
     * @param equipmentId идентификатор записи о доступном инвентаре
     * @param quantity количество возвращаемого инвентаря
     * @throws RuntimeException если запись с указанным id не найдена
     * @throws IllegalArgumentException если количество <= 0
     * @throws IllegalStateException если возврат превысит общее количество инвентаря
     * @see AvailableEquipment#returnEquipment(int)
     */
    public void returnEquipment(Long equipmentId, int quantity) {
        AvailableEquipment equipment = availableEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Запись инвентаря с ID: " + equipmentId + " не найдена."));

        equipment.returnEquipment(quantity);
        availableEquipmentRepository.save(equipment);
    }

    /**
     * Получает все пункты проката из системы.
     * <p>
     * Используется для заполнения выпадающих списков в пользовательском интерфейсе.
     * </p>
     *
     * @return список всех пунктов проката (может быть пустым)
     * @see RentalPointRepository#findAll()
     */
    public List<RentalPoint> getAllRentalPoints() {
        return rentalPointRepository.findAll();
    }

    /**
     * Получает все типы инвентаря из системы.
     * <p>
     * Используется для заполнения выпадающих списков в пользовательском интерфейсе.
     * </p>
     *
     * @return список всех типов инвентаря (может быть пустым)
     * @see EquipmentTypeRepository#findAll()
     */
    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    /**
     * Проверяет доступность инвентаря по его идентификатору.
     * <p>
     * Инвентарь считается доступным, если количество доступных единиц больше 0.
     * </p>
     *
     * @param equipmentId идентификатор записи о доступном инвентаре
     * @return {@code true} если инвентарь доступен для аренды,
     * {@code false} если инвентарь не найден или недоступен
     * @see AvailableEquipment#isAvailable()
     */
    public boolean isEquipmentAvailable(Long equipmentId) {
        Optional<AvailableEquipment> equipment = availableEquipmentRepository.findById(equipmentId);
        return equipment.isPresent() && equipment.get().isAvailable();
    }

    /**
     * Получает количество записей о доступном инвентаре с положительным остатком.
     * <p>
     * Подсчитывает только те записи, где {@code availableCount > 0}.
     * </p>
     *
     * @return количество записей с доступным инвентарем
     * @see AvailableEquipmentRepository#findByAvailableCountGreaterThan(Integer)
     */
    public long getAvailableEquipmentsCountWithStock() {
        return availableEquipmentRepository.findByAvailableCountGreaterThan(0).size();
    }

    /**
     * Получает общее количество записей о доступном инвентаре в системе.
     *
     * @return общее количество записей
     * @see AvailableEquipmentRepository#count()
     */
    public long getAvailableEquipmentsCount() {
        return availableEquipmentRepository.count();
    }

    /**
     * Проверяет существование указанных пункта проката и типа инвентаря.
     * <p>
     * Вспомогательный приватный метод, используемый в методах сохранения и обновления.
     * </p>
     *
     * @param availableEquipment запись для проверки
     * @throws IllegalArgumentException если пункт проката не указан или не существует,
     * или если тип инвентаря не указан или не существует
     */
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
