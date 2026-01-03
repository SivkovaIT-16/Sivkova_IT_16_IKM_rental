package com.rental.app.repository;

import com.rental.app.model.entity.RentalPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link RentalPoint} (пункт проката).
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также специализированные методы для поиска пунктов проката.
 * Spring автоматически создаёт реализацию этого репозитория.
 * </p>
 * <p>
 * <b>Особенности:</b>
 * <ul>
 *   <li>Spring автоматически создаёт SQL-запросы по названиям методов</li>
 *   <li>Поиск выполняется без учёта регистра (contains)</li>
 *   <li>Spring оптимизирует запросы для лучшей скорости</li>
 *   <li>Встроены пагинация (разбивка на страницы) и сортировка через наследование от {@link JpaRepository}</li>
 * </ul>
 * </p>
 *
 * @see RentalPoint
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.repository.CrudRepository
 */
@Repository
public interface RentalPointRepository extends JpaRepository<RentalPoint, Long> {
    /**
     * Находит все пункты проката, название которых содержит указанную подстроку.
     * <p>
     * Поиск выполняется без учета регистра.
     * </p>
     *
     * @param pointName подстрока для поиска в названии пункта проката
     * @return список пунктов проката, удовлетворяющих условию поиска
     * (пустой список, если ничего не найдено)
     * @throws IllegalArgumentException если pointName равен null
     */
    List<RentalPoint> findByPointNameContainingIgnoreCase(String pointName);

    /**
     * Находит все пункты проката, расположение (адрес) которых содержит указанную подстроку.
     * <p>
     * Поиск выполняется без учета регистра.
     * </p>
     *
     * @param location подстрока для поиска в адресе пункта проката
     * @return список пунктов проката, удовлетворяющих условию поиска
     * (пустой список, если ничего не найдено)
     * @throws IllegalArgumentException если location равен null
     */
    List<RentalPoint> findByLocationContainingIgnoreCase(String location);

    /**
     * Находит все пункты проката, часы работы которых содержат указанную подстроку.
     * <p>
     * Поиск выполняется с учетом регистра, так как часы работы
     * обычно содержат специфические сокращения и символы.
     * </p>
     *
     * @param openingHours подстрока для поиска в часах работы пункта проката
     * @return список пунктов проката, удовлетворяющих условию поиска
     * (пустой список, если ничего не найдено)
     * @throws IllegalArgumentException если openingHours равен null
     */
    List<RentalPoint> findByOpeningHoursContaining(String openingHours);

    /**
     * Проверяет, существует ли пункт проката с указанным расположением (адресом).
     * <p>
     * Проверка выполняется с учетом регистра и точного совпадения,
     * так как адрес должен быть уникальным в системе.
     * Используется для предотвращения создания дублирующих пунктов проката.
     * </p>
     *
     * @param location расположение (адрес) пункта проката для проверки
     * @return {@code true} если пункт проката с таким адресом существует,
     * {@code false} в противном случае
     * @throws IllegalArgumentException если location равен null
     * @see RentalPoint#getLocation()
     * @see RentalPoint#setLocation(String)
     */
    boolean existsByLocation(String location);

}

