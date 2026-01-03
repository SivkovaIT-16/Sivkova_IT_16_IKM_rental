package com.rental.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Сущность, представляющая доступный инвентарь в пункте проката.
 * <p>
 * Связывает пункт проката ({@link RentalPoint}) с типом инвентаря ({@link EquipmentType})
 * и содержит информацию о количестве, доступности и стоимости аренды.
 * Одна запись представляет собой наличие конкретного типа инвентаря в конкретном пункте проката.
 * </p>
 * <p>
 * <b>Уникальность:</b> Комбинация пункта проката и типа инвентаря должна быть уникальной
 * (не может быть двух одинаковых записей для одного пункта и одного типа).
 * </p>
 * <p>
 * <b>Бизнес-логика:</b> Предоставляет методы для аренды ({@link #rentEquipment(int)})
 * и возврата ({@link #returnEquipment(int)}) инвентаря с автоматической проверкой
 * доступности и целостности данных.
 * </p>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>
 * {@code
 * // Создание записи о доступном инвентаре
 * AvailableEquipment equipment = new AvailableEquipment();
 * equipment.setRentalPoint(rentalPoint);
 * equipment.setEquipmentType(equipmentType);
 * equipment.setTotalCount(10);
 * equipment.setAvailableCount(10);
 * equipment.setCost(100);
 *
 * // Аренда 2 единиц инвентаря
 * equipment.rentEquipment(2);
 * // Теперь availableCount = 8
 *
 * // Возврат 1 единицы
 * equipment.returnEquipment(1);
 * // Теперь availableCount = 9
 * }
 * </pre>
 *
 * @see RentalPoint
 * @see EquipmentType
 */
@Entity
@Table(name = "available_equipment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"point_id", "type_id"}))
public class AvailableEquipment {
    /**
     * Уникальный идентификатор записи о доступном инвентаре.
     * <p>
     * Генерируется автоматически базой данных при создании новой записи.
     * Не может быть null.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_id")
    private Long availableId;

    /**
     * Пункт проката, в котором доступен инвентарь.
     * <p>
     * Обязательное поле. Связь многие-к-одному с сущностью {@link RentalPoint}.
     * Не может быть null.
     * </p>
     * <p>
     * <b>Связь с базой данных:</b> Хранится как внешний ключ к таблице {@code rental_points}.
     * </p>
     * <p><b>Валидация выполняется автоматически через аннотацию:</b> {@link NotNull} - значение не должно быть null.</p>
     * <p><b>Сообщение об ошибке валидации:</b> "Точка проката должна быть указана."</p>
     */
    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false)
    @NotNull(message = "Точка проката должна быть указана.")
    private RentalPoint rentalPoint;

    /**
     * Тип инвентаря, который доступен в пункте проката.
     * <p>
     * Обязательное поле. Связь многие-к-одному с сущностью {@link EquipmentType}.
     * Не может быть null.
     * </p>
     * <p>
     * <b>Связь с базой данных:</b> Хранится как внешний ключ к таблице {@code equipment_types}.
     * </p>
     * <p><b>Валидация выполняется автоматически через аннотацию:</b> {@link NotNull} - значение не должно быть null.</p>
     * <p><b>Сообщение об ошибке валидации:</b> "Тип инвентаря должен быть указан."</p>
     */
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    @NotNull(message = "Тип инвентаря должен быть указан.")
    private EquipmentType equipmentType;

    /**
     * Общее количество инвентаря данного типа в пункте проката.
     * <p>
     * Обязательное поле. Должно быть неотрицательным числом.
     * Представляет физическое наличие инвентаря в пункте.
     * </p>
     * <p><b>Пример:</b> 10 (в пункте имеется 10 единиц данного инвентаря)</p>
     * <p>Валидация выполняется автоматически через аннотации:
     * <ul>
     *     <li>{@link NotNull} - значение не должно быть null</li>
     *     <li>{@link Min} - значение не должно быть отрицательным</li>
     * </ul>
     * </p>
     * <p><b>Сообщения об ошибках валидации:</b>
     * <ul>
     *     <li>"Общее количество инвентаря не может быть пустым."</li>
     *     <li>"Общее количество инвентаря не может быть отрицательным."</li>
     * </ul>
     * </p>
     */
    @NotNull(message = "Общее количество инвентаря не может быть пустым.")
    @Min(value = 0, message = "Общее количество инвентаря не может быть отрицательным.")
    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    /**
     * Количество инвентаря, доступного для аренды в текущий момент.
     * <p>
     * Необязательное поле. По умолчанию равно {@code totalCount}.
     * Должно быть неотрицательным числом и не может превышать {@code totalCount}.
     * Может быть null - в этом случае при сохранении будет установлено равным {@code totalCount}.
     * </p>
     * <p>
     * <b>Автоматическая инициализация:</b> При сохранении или обновлении записи,
     * если {@code availableCount} равен null, он автоматически устанавливается равным {@code totalCount}
     * через метод {@link #validate()}.
     * </p>
     * <p><b>Пример:</b> 8 (из 10 единиц доступно для аренды, 2 уже арендованы)</p>
     * <p><b>Валидация выполняется автоматически через аннотацию:</b> {@link Min} - значение не должно быть отрицательным.</p>
     * <p><b>Сообщение об ошибке валидации:</b>
     * "Доступное количество инвентаря не может быть отрицательным."
     * </p>
     */
    @Min(value = 0, message = "Доступное количество инвентаря не может быть отрицательным.")
    @Column(name = "available_count")
    private Integer availableCount;

    /**
     * Стоимость аренды одной единицы инвентаря в час.
     * <p>
     * Обязательное поле. Должно быть положительным числом.
     * Измеряется в рублях за час.
     * </p>
     * <p><b>Пример:</b> 100 (аренда стоит 100 рублей в час)</p>
     * <p>Валидация выполняется автоматически через аннотации:
     * <ul>
     *     <li>{@link NotNull} - значение не должно быть null</li>
     *     <li>{@link Min} - значение должно быть не меньше 1</li>
     * </ul>
     * <p><b>Сообщения об ошибках валидации:</b>
     * <ul>
     *     <li>"Стоимость аренды не может быть пустой."</li>
     *     <li>"Стоимость аренды должна быть больше 0."</li>
     * </ul>
     * </p>
     */
    @NotNull(message = "Стоимость аренды не может быть пустой.")
    @Min(value = 1, message = "Стоимость аренды должна быть больше 0.")
    @Column(nullable = false)
    private Integer cost;

    /**
     * Метод валидации, вызываемый автоматически перед сохранением и обновлением записи.
     * <p>
     * Выполняет следующие проверки:
     * <ol>
     *     <li>Если {@code availableCount} равен null, устанавливает его равным {@code totalCount}</li>
     *     <li>Проверяет, что {@code availableCount} не превышает {@code totalCount}</li>
     * </ol>
     * </p>
     * <p>
     * <b>Вызывается автоматически:</b> JPA вызывает этот метод перед операциями
     * {@code persist()} (сохранение) и {@code merge()} (обновление).
     * </p>
     *
     * @throws IllegalStateException если доступное количество превышает общее количество
     * @see jakarta.persistence.PrePersist
     * @see jakarta.persistence.PreUpdate
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        if (availableCount == null && totalCount != null) {
            availableCount = totalCount;
        }

        if (availableCount != null && totalCount != null && availableCount > totalCount) {
            throw new IllegalStateException(
                    "Доступное количество инвентаря (" + availableCount +
                            ") не может превышать общее (" + totalCount + ")."
            );
        }
    }

    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется JPA для создания экземпляров сущности.
     * </p>
     */

    public AvailableEquipment() {
    }

    /**
     * Конструктор с параметрами.
     * <p>
     * Создает новую запись о доступном инвентаре с указанными значениями.
     * </p>
     *
     * @param rentalPoint пункт проката (не null)
     * @param equipmentType тип инвентаря (не null)
     * @param totalCount общее количество (не null, >= 0)
     * @param availableCount доступное количество (может быть null, >= 0, <= totalCount)
     * @param cost стоимость аренды в час (не null, >= 1)
     */
    public AvailableEquipment(RentalPoint rentalPoint, EquipmentType equipmentType,
                              Integer totalCount, Integer availableCount, Integer cost) {
        this.rentalPoint = rentalPoint;
        this.equipmentType = equipmentType;
        this.totalCount = totalCount;
        this.availableCount = availableCount;
        this.cost = cost;
    }

    /**
     * Возвращает уникальный идентификатор записи о доступном инвентаре.
     *
     * @return идентификатор записи
     */
    public Long getAvailableId() {
        return availableId;
    }

    /**
     * Устанавливает уникальный идентификатор записи о доступном инвентаре.
     * <p>
     * Обычно устанавливается автоматически базой данных.
     * </p>
     *
     * @param availableId новый идентификатор записи
     */
    public void setAvailableId(Long availableId) {
        this.availableId = availableId;
    }

    /**
     * Возвращает пункт проката.
     *
     * @return пункт проката, в котором доступен инвентарь
     */
    public RentalPoint getRentalPoint() {
        return rentalPoint;
    }

    /**
     * Устанавливает пункт проката.
     *
     * @param rentalPoint новый пункт проката (не null)
     * @throws IllegalArgumentException если rentalPoint равен null
     */
    public void setRentalPoint(RentalPoint rentalPoint) {
        this.rentalPoint = rentalPoint;
    }

    /**
     * Возвращает тип инвентаря.
     *
     * @return тип инвентаря, доступного в пункте проката
     */
    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    /**
     * Устанавливает тип инвентаря.
     *
     * @param equipmentType новый тип инвентаря (не null)
     * @throws IllegalArgumentException если equipmentType равен null
     */
    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    /**
     * Возвращает общее количество инвентаря.
     *
     * @return общее количество единиц инвентаря в пункте проката
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * Устанавливает общее количество инвентаря.
     *
     * @param totalCount новое общее количество (не null, >= 0)
     * @throws IllegalArgumentException если totalCount не соответствует ограничениям
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Возвращает доступное количество инвентаря.
     *
     * @return количество единиц инвентаря, доступных для аренды
     */
    public Integer getAvailableCount() {
        return availableCount;
    }

    /**
     * Устанавливает доступное количество инвентаря.
     * <p>
     * При сохранении будет выполнена проверка,
     * что установленное значение не превышает {@code totalCount}.
     * </p>
     *
     * @param availableCount новое доступное количество (может быть null, >= 0, <= totalCount)
     * @throws IllegalArgumentException если availableCount не соответствует ограничениям
     */
    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    /**
     * Возвращает стоимость аренды в час.
     *
     * @return стоимость аренды одной единицы инвентаря за час (в рублях)
     */
    public Integer getCost() {
        return cost;
    }

    /**
     * Устанавливает стоимость аренды в час.
     *
     * @param cost новая стоимость аренды (не null, >= 1)
     * @throws IllegalArgumentException если cost не соответствует ограничениям
     */
    public void setCost(Integer cost) {
        this.cost = cost;
    }

    /**
     * Возвращает единицу измерения стоимости.
     * <p>
     * Используется для отображения в пользовательском интерфейсе.
     * </p>
     *
     * @return строка "руб./час"
     */
    public String getCostUnit() {
        return "руб./час";
    }

    /**
     * Проверяет, доступен ли инвентарь для аренды.
     * <p>
     * Инвентарь доступен, если {@code availableCount} больше 0.
     * </p>
     *
     * @return {@code true} если инвентарь доступен для аренды, {@code false} в противном случае
     */
    public boolean isAvailable() {
        return availableCount != null && availableCount > 0;
    }

    /**
     * Арендует указанное количество инвентаря.
     * <p>
     * Уменьшает {@code availableCount} на указанное количество
     * после проверки доступности.
     * </p>
     *
     * @param quantity количество арендуемого инвентаря (должно быть > 0)
     * @throws IllegalArgumentException если quantity <= 0
     * @throws IllegalStateException если доступного инвентаря недостаточно
     */
    public void rentEquipment(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество арендуемого инвентаря должно быть положительным.");
        }
        if (availableCount < quantity) {
            throw new IllegalStateException(
                    String.format("Недостаточно инвентаря для аренды. Доступно: %d, запрошено: %d.",
                            availableCount, quantity)
            );
        }
            availableCount -= quantity;
    }

    /**
     * Возвращает указанное количество инвентаря.
     * <p>
     * Увеличивает {@code availableCount} на указанное количество
     * после проверки, что общее количество не будет превышено.
     * </p>
     *
     * @param quantity количество возвращаемого инвентаря (должно быть > 0)
     * @throws IllegalArgumentException если quantity <= 0
     * @throws IllegalStateException если возврат превысит общее количество инвентаря
     */
    public void returnEquipment(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество возвращаемого инвентаря должно быть положительным.");
        }
        if (availableCount + quantity > totalCount) {
            throw new IllegalStateException(
                    String.format("Возврат превышает общее количество инвентаря. Всего: %d, будет: %d.",
                            totalCount, availableCount + quantity)
            );
        }
        availableCount += quantity;
    }

    /**
     * Возвращает строковое представление записи о доступном инвентаре.
     * <p>
     * <b>Формат:</b> AvailableEquipment{availableId=[id], rentalPoint=[pointName],
     * equipmentType=[typeName], totalCount=[total], availableCount=[available],
     * cost=[cost], costUnit='руб./час'}
     * </p>
     *
     * @return строковое представление записи
     */
    @Override
    public String toString() {
        return "AvailableEquipment{" +
                "availableId=" + availableId +
                ", rentalPoint=" + (rentalPoint != null ? rentalPoint.getPointName() : "null") +
                ", equipmentType=" + (equipmentType != null ? equipmentType.getTypeName() : "null") +
                ", totalCount=" + totalCount +
                ", availableCount=" + availableCount +
                ", cost=" + cost +
                ", costUnit='" + getCostUnit() + '\'' +
                '}';
    }
}
