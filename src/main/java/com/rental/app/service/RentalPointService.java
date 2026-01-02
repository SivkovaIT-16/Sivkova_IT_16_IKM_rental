package com.rental.app.service;

import com.rental.app.model.entity.RentalPoint;
import com.rental.app.repository.RentalPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalPointService {
    private final RentalPointRepository rentalPointRepository;

    @Autowired
    public RentalPointService(RentalPointRepository rentalPointRepository) {
        this.rentalPointRepository = rentalPointRepository;
    }

    // Получение всех пунктов проката
    public List<RentalPoint> getAllRentalPoints() {
        return rentalPointRepository.findAll();
    }

    // Получение пунктов проката по Id
    public Optional<RentalPoint> getRentalPointById(Long id) {
        return rentalPointRepository.findById(id);
    }

    // Сохранение нового пункта проката
    public RentalPoint saveRentalPoint(RentalPoint rentalPoint) {
        // Проверка уникальности расположения
        if (rentalPointRepository.existsByLocation(rentalPoint.getLocation())) {
            throw new IllegalArgumentException(
                    "Пункт проката по адресу \"" + rentalPoint.getLocation() + "\" уже существует.");
        }
        return rentalPointRepository.save(rentalPoint);
    }

    // Обновление уже существующего пункта проката
    public RentalPoint updateRentalPoint(Long id, RentalPoint rentalPointDetails) {
        RentalPoint rentalPoint = rentalPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пункт проката с Id: " + id + " не найден."));

        // Проверка уникальности расположения
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

    // Удаление пункта проката
    public void deleteRentalPoint(Long id) {
        rentalPointRepository.deleteById(id);
    }

    // Поиск пункта проката по названию
    public List<RentalPoint> searchByPointName(String pointName) {
        return rentalPointRepository.findByPointNameContainingIgnoreCase(pointName);
    }

    // Поиск пункта проката по расположению
    public List<RentalPoint> searchByLocation(String location) {
        return rentalPointRepository.findByLocationContainingIgnoreCase(location);
    }

    // Поиск пункта проката по часам работы
    public List<RentalPoint> searchByOpeningHours(String openingHours) {
        return rentalPointRepository.findByOpeningHoursContaining(openingHours);
    }

    // Получение общего количества пунктов проката
    public long getRentalPointCount() {
        return rentalPointRepository.count();
    }
}
