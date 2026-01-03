package com.rental.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Сущность, представляющая пункт проката инвентаря.
 * <p>
 * Пункт проката содержит информацию о месте, где можно взять в аренду спортивный инвентарь.
 * Каждый пункт проката имеет уникальное расположение (адрес).
 * </p>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>
 * {@code
 * RentalPoint point = new RentalPoint();
 * point.setPointName("Абсолют Спорт");
 * point.setLocation(г.Пермь, ул.Маршрутная, 9");
 * point.setOpeningHours("Пн-Пт: 10:00-22:00, Сб-Вс: 9:00-23:00");
 * }
 * </pre>
 *
 * @see AvailableEquipment
 * @see EquipmentType
 */
@Entity
@Table(name = "rental_points")
public class RentalPoint {
    /**
     * Уникальный идентификатор пункта проката.
     * <p>
     * Генерируется автоматически базой данных при создании новой записи.
     * Не может быть null.
     * </p>
     */
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название пункта проката.
     * <p>
     * Обязательное поле. Должно содержать от 1 до 200 символов.
     * Не может быть null.
     * <p>
     * Пример: "Абсолют Спорт", "Горный прокат".
     * </p>
     *
     * <p>Валидация выполняется автоматически через аннотации:
     * <ul>
     *     <li>{@link NotBlank} - строка не должна быть null, пустой или состоять только из пробелов</li>
     *     <li>{@link Size} - проверяет длину строки от 1 до 200 символов</li>
     * </ul>
     * </p>
     *
     * <p><b>Сообщения об ошибках валидации:</b>
     * <ul>
     *     <li>"Название пункта проката не может быть пустым."</li>
     *     <li>"Название пункта проката должно быть от 1 до 200 символов."</li>
     * </ul>
     * </p>
     */
    @NotBlank (message = "Название пункта проката не может быть пустым.")
    @Size(min = 1, max = 200, message = "Название пункта проката должно быть от 1 до 200 символов.")
    @Column (name = "point_name", nullable = false)
    private String pointName;

    /**
     * Расположение пункта проката.
     * <p>
     * Обязательное поле. Должно содержать от 1 до 300 символов.
     * Должно быть уникальным - не может быть двух прокатов с одинаковым адресом.
     * Не может быть null.
     * <p>
     * Пример: "г.Пермь, ул.Маршрутная, 9", "г.Москва, ул.Спортивная, 15".
     * </p>
     *
     * <p>Валидация выполняется автоматически через аннотации:
     * <ul>
     *     <li>{@link NotBlank} - строка не должна быть null, пустой или состоять только из пробелов</li>
     *     <li>{@link Size} - проверяет длину строки от 1 до 300 символов</li>
     *     <li>{@link Column#unique} - гарантирует уникальность значения в базе данных</li>
     * </ul>
     * </p>
     *
     * <p><b>Сообщения об ошибках валидации:</b>
     * <ul>
     *     <li>"Расположение пункта проката не может быть пустым."</li>
     *     <li>"Расположение пункта проката должно быть от 1 до 300 символов."</li>
     * </ul>
     * </p>
     */
    @NotBlank (message = "Расположение пункта проката не может быть пустым.")
    @Size(min = 1, max = 300, message = "Расположение пункта проката должно быть от 1 до 300 символов.")
    @Column (name = "location", nullable = false, unique = true)
    private String location;

    /**
     * Часы работы пункта проката.
     * <p>
     * Обязательное поле. Должно содержать от 1 до 500 символов.
     * Может содержать информацию о рабочих днях, выходных, перерывах.
     * Не может быть null.
     * Хранится как тип <code>TEXT</code> в базе данных.
     * <p>
     * Пример: "Пн-Пт: 10:00-22:00, Сб-Вс: 9:00-23:00", "Ежедневно: 8:00-20:00".
     * </p>
     *
     * <p>Валидация выполняется автоматически через аннотации:
     * <ul>
     *     <li>{@link NotBlank} - строка не должна быть null, пустой или состоять только из пробелов</li>
     *     <li>{@link Size} - проверяет длину строки от 1 до 500 символов</li>
     * </ul>
     * </p>
     *
     * <p><b>Сообщения об ошибках валидации:</b>
     * <ul>
     *     <li>"Часы работы пункта проката не могут быть пустыми."</li>
     *     <li>"Часы работы должны быть от 1 до 500 символов."</li>
     * </ul>
     * </p>
     */
    @NotBlank (message = "Часы работы пункта проката не могут быть пустыми.")
    @Size(min = 1, max = 500, message = "Часы работы должны быть от 1 до 500 символов.")
    @Column (name = "opening_hours", nullable = false, columnDefinition = "TEXT")
    private String openingHours;

    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется JPA для создания экземпляров сущности.
     * </p>
     */
    public RentalPoint() {
    }

    /**
     * Конструктор с параметрами.
     * <p>
     * Создает новый пункт проката с указанными значениями.
     * </p>
     *
     * @param pointName название пункта проката (не null, не пустое, 1-200 символов)
     * @param location расположение пункта проката (не null, не пустое, 1-300 символов, уникальное)
     * @param openingHours часы работы (не null, не пустое, 1-500 символов)
     */
    public RentalPoint(String pointName, String location, String openingHours) {
        this.pointName = pointName;
        this.location = location;
        this.openingHours = openingHours;
    }

    /**
     * Возвращает уникальный идентификатор пункта проката.
     *
     * @return идентификатор пункта проката
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор пункта проката.
     * <p>
     * Обычно устанавливается автоматически базой данных.
     * </p>
     *
     * @param id новый идентификатор пункта проката
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название пункта проката.
     *
     * @return название пункта проката
     */
    public String getPointName() {
        return pointName;
    }

    /**
     * Устанавливает название пункта проката.
     *
     * @param pointName новое название пункта проката (не null, не пустое, 1-200 символов)
     * @throws IllegalArgumentException если pointName не соответствует ограничениям
     */
    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    /**
     * Возвращает расположение пункта проката.
     *
     * @return расположение пункта проката
     */
    public String getLocation() {
        return location;
    }

    /**
     * Устанавливает расположение пункта проката.
     *
     * @param location новое расположение пункта проката (не null, не пустое, 1-300 символов, уникальное)
     * @throws IllegalArgumentException если location не соответствует ограничениям
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Возвращает часы работы пункта проката.
     *
     * @return часы работы пункта проката
     */
    public String getOpeningHours() {
        return openingHours;
    }

    /**
     * Устанавливает часы работы пункта проката.
     *
     * @param openingHours новые часы работы пункта проката (не null, не пустое, 1-500 символов)
     * @throws IllegalArgumentException если openingHours не соответствует ограничениям
     */
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * Возвращает строковое представление пункта проката.
     * <p>
     * <b>Формат:</b> RentalPoint{id=[id], pointName='[pointName]',
     * location='[location]', openingHours='[openingHours]'}
     * </p>
     *
     * @return строковое представление пункта проката
     */
    @Override
    public String toString() {
        return "RentalPoint{" +
                "id=" + id +
                ", pointName='" + pointName + '\'' +
                ", location='" + location + '\'' +
                ", openingHours='" + openingHours + '\'' +
                '}';
    }
}
