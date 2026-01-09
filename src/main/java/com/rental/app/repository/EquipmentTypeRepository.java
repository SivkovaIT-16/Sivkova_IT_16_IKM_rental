package com.rental.app.repository;

import com.rental.app.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий для работы с типами инвентаря ({@link EquipmentType}).
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также специализированные методы для поиска пунктов проката.
 * Spring автоматически создаёт реализацию этого репозитория.
 * </p>
 *
 * @see EquipmentType
 * @see JpaRepository
 */
@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long>{
    /**
     * Находит типы инвентаря по части названия.
     * <p>
     * Ищет типы инвентаря, в названии которых содержится указанный текст.
     * Поиск выполняется без учёта регистра (игнорируются большие и маленькие буквы).
     * </p>
     *
     * @param typeName подстрока для поиска в названии типа инвентаря
     * @return список найденных типов инвентаря или пустой список, если ничего не найдено
     */
    List<EquipmentType> findByTypeNameContainingIgnoreCase(String typeName);

    /**
     * Находит типы инвентаря по категории.
     * <p>
     * Ищет все типы инвентаря, которые принадлежат к указанной категории.
     * Поиск выполняется без учёта регистра.
     * </p>
     *
     * @param category подстрока для поиска в категории типа инвентаря
     * @return список типов инвентаря указанной категории или пустой список
     */
    List<EquipmentType> findByCategoryContainingIgnoreCase(String category);

    /**
     * Проверяет, существует ли уже тип инвентаря с указанным названием.
     * <p>
     * Проверка выполняется с учётом точного совпадения названия без учёта регистра (функция {@code LOWER()}).
     * Использует аннотацию {@link Query} с JPQL-запросом.
     * Используется для обеспечения уникальности названий типов инвентаря в системе.
     * </p>
     *
     * @param typeName название типа инвентаря для проверки (например: "Велосипед")
     * @return {@code true} если тип инвентаря с таким названием уже существует,
     * {@code false} если название свободно
     * @see Query
     * @see EquipmentType#getTypeName()
     * @see EquipmentType#setTypeName(String)
     */
    @Query("SELECT COUNT(e) > 0 FROM EquipmentType e WHERE LOWER(e.typeName) = LOWER(:typeName)")
    boolean existsByTypeName(String typeName);
}
