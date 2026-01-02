package com.rental.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "rental_points")
public class RentalPoint {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank (message = "Название пункта проката не может быть пустым.")
    @Size(min = 1, max = 200, message = "Название пункта проката должно быть от 1 до 200 символов.")
    @Column (name = "point_name", nullable = false)
    private String pointName;

    @NotBlank (message = "Расположение пункта проката не может быть пустым.")
    @Size(min = 1, max = 500, message = "Расположение пункта проката должно быть от 1 до 500 символов.")
    @Column (name = "location", nullable = false, unique = true)
    private String location;

    @NotBlank (message = "Часы работы пункта проката не могут быть пустыми.")
    @Size(min = 1, max = 300, message = "Часы работы должны быть от 1 до 300 символов.")
    @Column (name = "opening_hours", nullable = false, columnDefinition = "TEXT")
    private String openingHours;

    public RentalPoint() {
    }

    public RentalPoint(String pointName, String location, String openingHours) {
        this.pointName = pointName;
        this.location = location;
        this.openingHours = openingHours;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

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
