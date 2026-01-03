package com.rental.app.repository;

import com.rental.app.model.entity.AvailableEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с доступным инвентарем ({@link AvailableEquipment}).
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также специализированные методы для работы с инвентарем в пунктах проката.
 * Spring автоматически создаёт реализацию этого репозитория.
 * </p>
 * <p>
 * <b>Основные задачи:</b>
 * <ul>
 *   <li>Управление наличием инвентаря в пунктах проката</li>
 *   <li>Поиск инвентаря по различным критериям</li>
 *   <li>Проверка наличия конкретных позиций в пунктах</li>
 *   <li>Отслеживание количества доступных единиц инвентаря</li>
 * </ul>
 * </p>
 *
 * @see AvailableEquipment
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface AvailableEquipmentRepository extends JpaRepository<AvailableEquipment, Long> {
    /**
     * Находит весь инвентарь в указанном пункте проката.
     * <p>
     * Используется для отображения полного ассортимента пункта.
     * </p>
     *
     * @param rentalPointId ID пункта проката
     * @return список доступного инвентаря в указанном пункте или пустой список, если инвентаря нет
     * @see com.rental.app.model.entity.RentalPoint
     */
    List<AvailableEquipment> findByRentalPointId(Long rentalPointId);

    /**
     * Находит весь инвентарь определенного типа во всех пунктах проката.
     *
     * @param equipmentTypeId ID типа инвентаря
     * @return список записей о наличии указанного типа инвентаря во всех пунктах проката
     * @see EquipmentTypeRepository
     */
    List<AvailableEquipment> findByEquipmentTypeId(Long equipmentTypeId);

    /**
     * Находит конкретную запись о наличии инвентаря по пункту проката и типу инвентаря.
     * <p>
     * Используется для проверки наличия определенного типа инвентаря в конкретном пункте.
     * </p>
     *
     * @param rentalPointId ID пункта проката
     * @param equipmentTypeId ID типа инвентаря
     * @return {@link Optional} с записью о наличии или пустой {@link Optional}, если запись не найдена
     */
    Optional<AvailableEquipment> findByRentalPointIdAndEquipmentTypeId(Long rentalPointId, Long equipmentTypeId);

    /**
     * Находит инвентарь с количеством доступных единиц больше указанного значения.
     * <p>
     * Используется для поиска позиций, которые есть в достаточном количестве для аренды.
     * </p>
     *
     * @param count минимальное количество доступных единиц (не включая это значение)
     * @return список позиций инвентаря с доступным количеством больше указанного
     * @see AvailableEquipment#getAvailableCount()
     */
    List<AvailableEquipment> findByAvailableCountGreaterThan(Integer count);

    /**
     * Проверяет существование записи о наличии инвентаря для указанных пункта и типа.
     * <p>
     * Используется для валидации при добавлении или обновлении записей.
     * </p>
     *
     * @param rentalPointId ID пункта проката
     * @param equipmentTypeId ID типа инвентаря
     * @return {@code true} если запись существует, {@code false} в противном случае
     */
    boolean existsByRentalPointIdAndEquipmentTypeId(Long rentalPointId, Long equipmentTypeId);
}
