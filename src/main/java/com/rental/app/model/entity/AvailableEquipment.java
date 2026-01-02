package com.rental.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "available_equipment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"point_id", "type_id"}))
public class AvailableEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_id")
    private Long availableId;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false)
    @NotNull(message = "Точка проката должна быть указана.")
    private RentalPoint rentalPoint;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    @NotNull(message = "Тип инвентаря должен быть указан.")
    private EquipmentType equipmentType;

    @NotNull(message = "Общее количество инвентаря не может быть пустым.")
    @Min(value = 0, message = "Общее количество инвентаря не может быть отрицательным.")
    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Min(value = 0, message = "Доступное количество инвентаря не может быть отрицательным.")
    @Column(name = "available_count")
    private Integer availableCount;

    @NotNull(message = "Стоимость аренды не может быть пустой.")
    @Min(value = 1, message = "Стоимость аренды должна быть больше 0.")
    @Column(nullable = false)
    private Integer cost;

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

    public AvailableEquipment() {
    }

    public AvailableEquipment(RentalPoint rentalPoint, EquipmentType equipmentType,
                              Integer totalCount, Integer availableCount, Integer cost) {
        this.rentalPoint = rentalPoint;
        this.equipmentType = equipmentType;
        this.totalCount = totalCount;
        this.availableCount = availableCount;
        this.cost = cost;
    }

    public Long getAvailableId() {
        return availableId;
    }

    public void setAvailableId(Long availableId) {
        this.availableId = availableId;
    }

    public RentalPoint getRentalPoint() {
        return rentalPoint;
    }

    public void setRentalPoint(RentalPoint rentalPoint) {
        this.rentalPoint = rentalPoint;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getCostUnit() {
        return "руб./час";
    }

    // Метод проверки наличия инвентаря
    public boolean isAvailable() {
        return availableCount != null && availableCount > 0;
    }

    // Метод для аренды инвентаря
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

    // Метод для возврата инвентаря
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
