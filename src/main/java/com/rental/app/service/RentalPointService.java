package com.rental.app.service;

import com.rental.app.entity.RentalPoint;
import com.rental.app.repository.RentalPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пунктами проката ({@link RentalPoint}).
 * <p>
 * Содержит бизнес-логику для управления пунктами проката, включая создание,
 * обновление, удаление и поиск. Гарантирует уникальность адресов пунктов проката.
 * Каждый метод выполняется в отдельной транзакции базы данных.
 * </p>
 * <p>
 * <b>Транзакции:</b> Spring автоматически управляет транзакциями -
 * открывает перед вызовом метода и закрывает после его завершения.
 * При возникновении исключения все изменения в базе данных откатываются.
 * </p>
 * @see RentalPoint
 * @see RentalPointRepository
 * @see Service
 * @see Transactional
 */
@Service
@Transactional
public class RentalPointService {
    private final RentalPointRepository rentalPointRepository;

    /**
     * Создает сервис с указанным репозиторием пунктов проката.
     *
     * @param rentalPointRepository репозиторий для доступа к данным пунктов проката
     */
    @Autowired
    public RentalPointService(RentalPointRepository rentalPointRepository) {
        this.rentalPointRepository = rentalPointRepository;
    }

    /**
     * Получает все пункты проката из системы.
     *
     * @return список всех пунктов проката (может быть пустым)
     */
    public List<RentalPoint> getAllRentalPoints() {
        return rentalPointRepository.findAll();
    }

    /**
     * Находит пункт проката по его идентификатору.
     * <p>
     * Использует {@link Optional} для корректной обработки случаев,
     * когда пункт проката не найден.
     * </p>
     *
     * @param id идентификатор пункта проката
     * @return {@link Optional}, содержащий пункт проката, если найден,
     * или пустой {@link Optional}, если не найден
     */
    public Optional<RentalPoint> getRentalPointById(Long id) {
        return rentalPointRepository.findById(id);
    }

    /**
     * Сохраняет новый пункт проката в системе.
     * <p>
     * Перед сохранением проверяет уникальность адреса пункта проката.
     * </p>
     *
     * @param rentalPoint пункт проката для сохранения
     * @return сохраненный пункт проката
     * @throws IllegalArgumentException если адрес пункта проката уже существует в системе
     * @see RentalPointRepository#existsByLocation(String)
     */
    public RentalPoint saveRentalPoint(RentalPoint rentalPoint) {
        if (rentalPointRepository.existsByLocation(rentalPoint.getLocation())) {
            throw new IllegalArgumentException(
                    "Пункт проката по адресу \"" + rentalPoint.getLocation() + "\" уже существует.");
        }
        return rentalPointRepository.save(rentalPoint);
    }

    /**
     * Обновляет существующий пункт проката.
     * <p>
     * Находит пункт проката по идентификатору, проверяет уникальность нового адреса
     * (если адрес был изменен), обновляет поля и сохраняет изменения.
     * </p>
     *
     * @param id идентификатор обновляемого пункта проката
     * @param rentalPointDetails объект с новыми значениями полей
     * @return обновленный пункт проката
     * @throws RuntimeException если пункт проката с указанным id не найден
     * @throws IllegalArgumentException если новый адрес уже используется другим пунктом проката
     */
    public RentalPoint updateRentalPoint(Long id, RentalPoint rentalPointDetails) {
        RentalPoint rentalPoint = rentalPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пункт проката с Id: " + id + " не найден."));

        if (!rentalPoint.getLocation().equals(rentalPointDetails.getLocation()) &&
                rentalPointRepository.existsByLocation(rentalPointDetails.getLocation())) {
            throw new IllegalArgumentException(
                    "Пункт проката по адресу \"" + rentalPointDetails.getLocation() + "\" уже существует.");
        }

        rentalPoint.setPointName(rentalPointDetails.getPointName());
        rentalPoint.setLocation(rentalPointDetails.getLocation());
        rentalPoint.setOpeningHours(rentalPointDetails.getOpeningHours());

        return rentalPointRepository.save(rentalPoint);
    }

    /**
     * Удаляет пункт проката по идентификатору.
     * <p>
     * Проверяет существование пункта проката перед удалением.
     * </p>
     *
     * @param id идентификатор удаляемого пункта проката
     * @throws RuntimeException если пункт проката с указанным id не найден
     * @see RentalPointRepository#deleteById(Object)
     */
    public void deleteRentalPoint(Long id) {
        if (!rentalPointRepository.existsById(id)) {
            throw new RuntimeException("Пункт проката с ID: " + id + " не найден.");
        }
        rentalPointRepository.deleteById(id);
    }

    /**
     * Ищет пункты проката по части названия.
     * <p>
     * Поиск выполняется без учета регистра символов.
     * </p>
     *
     * @param pointName подстрока для поиска в названии пункта проката
     * @return список найденных пунктов проката (может быть пустым)
     * @see RentalPointRepository#findByPointNameContainingIgnoreCase(String)
     */
    public List<RentalPoint> searchByPointName(String pointName) {
        return rentalPointRepository.findByPointNameContainingIgnoreCase(pointName);
    }

    /**
     * Ищет пункты проката по части адреса.
     * <p>
     * Поиск выполняется без учета регистра символов.
     * </p>
     *
     * @param location подстрока для поиска в адресе пункта проката
     * @return список найденных пунктов проката (может быть пустым)
     * @see RentalPointRepository#findByLocationContainingIgnoreCase(String)
     */
    public List<RentalPoint> searchByLocation(String location) {
        return rentalPointRepository.findByLocationContainingIgnoreCase(location);
    }

    /**
     * Ищет пункты проката по части часов работы.
     * <p>
     * Поиск выполняется с учетом регистра символов, так как часы работы
     * могут содержать специфические сокращения и символы.
     * </p>
     *
     * @param openingHours подстрока для поиска в часах работы
     * @return список найденных пунктов проката (может быть пустым)
     * @see RentalPointRepository#findByOpeningHoursContaining(String)
     */
    public List<RentalPoint> searchByOpeningHours(String openingHours) {
        return rentalPointRepository.findByOpeningHoursContaining(openingHours);
    }

    /**
     *Возвращает общее количество пунктов проката в системе.
     *
     * @return количество пунктов проката
     * @see RentalPointRepository#count()
     */
    public long getRentalPointCount() {
        return rentalPointRepository.count();
    }
}
