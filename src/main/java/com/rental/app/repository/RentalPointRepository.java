package com.rental.app.repository;

import com.rental.app.model.entity.RentalPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentalPointRepository extends JpaRepository<RentalPoint, Long> {
    // Поиск пункта проката по названию
    List<RentalPoint> findByPointNameContainingIgnoreCase(String pointName);

    // Поиск пункта проката по расположению
    List<RentalPoint> findByLocationContainingIgnoreCase(String location);

    // Поиск пункта проката по часам работы
    List<RentalPoint> findByOpeningHoursContaining(String openingHours);

    // Проверка существования пункта проката по расположению
    boolean existsByLocation(String location);

}

