package com.rental.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "equipment_types")
public class EquipmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название типа инвентаря не может быть пустым")
    @Size(min = 1, max = 100, message = "Название типа инвентаря должно быть от 1 до 100 символов")
    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @NotBlank(message = "Название категории инвентаря не может быть пустым")
    @Size(min = 1, max = 50, message = "Название категории инвентаря должно быть от 1 до 50 символов")
    @Column(name = "category", nullable = false)
    private String category;

    @Size(max = 500, message = "Описание типа инвентаря не должно превышать 500 символов")
    @Column(columnDefinition = "TEXT")
    private String description;

    public EquipmentType() {
    }

    public EquipmentType(String typeName, String category, String description) {
        this.typeName = typeName;
        this.category = category;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EquipmentType{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}