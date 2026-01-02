package com.rental.app.controller;

import com.rental.app.model.entity.AvailableEquipment;
import com.rental.app.service.AvailableEquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/availableEquipments")
public class AvailableEquipmentController {
    private final AvailableEquipmentService availableEquipmentService;

    @Autowired
    public AvailableEquipmentController(AvailableEquipmentService availableEquipmentService) {
        this.availableEquipmentService = availableEquipmentService;
    }

    // Главная страница - список всего инвентаря
    @GetMapping
    public String listAvailableEquipments(Model model) {
        List<AvailableEquipment> availableEquipments = availableEquipmentService.getAllAvailableEquipments();
        model.addAttribute("availableEquipments", availableEquipments);
        model.addAttribute("equipmentCount", availableEquipments.size());
        model.addAttribute("availableEquipmentCount", availableEquipmentService.getAvailableEquipmentsCountWithStock());
        return "availableEquipments/list";
    }

    // Страница добавления новой записи
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("availableEquipment", new AvailableEquipment());
        model.addAttribute("rentalPoints", availableEquipmentService.getAllRentalPoints());
        model.addAttribute("equipmentTypes", availableEquipmentService.getAllEquipmentTypes());
        model.addAttribute("action", "create");
        return "availableEquipments/form";
    }

    // Обработка создания новой записи
    @PostMapping
    public String createAvailableEquipment(@Valid @ModelAttribute("availableEquipment") AvailableEquipment availableEquipment,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rentalPoints", availableEquipmentService.getAllRentalPoints());
            model.addAttribute("equipmentTypes", availableEquipmentService.getAllEquipmentTypes());
            model.addAttribute("action", "create");
            return "availableEquipments/form";
        }

        try {
            availableEquipmentService.saveAvailableEquipment(availableEquipment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Инвентарь успешно добавлен в пункт проката.");
            return "redirect:/availableEquipments";
        } catch (Exception e) {
            model.addAttribute("rentalPoints", availableEquipmentService.getAllRentalPoints());
            model.addAttribute("equipmentTypes", availableEquipmentService.getAllEquipmentTypes());
            model.addAttribute("action", "create");
            model.addAttribute("errorMessage", "Ошибка при сохранении: " + e.getMessage());
            return "availableEquipments/form";
        }
    }

    // Страница редактирования записи
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        AvailableEquipment availableEquipment = availableEquipmentService.getAvailableEquipmentById(id)
                .orElse(null);

        if (availableEquipment == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Запись не найдена.");
            return "redirect:/availableEquipments";
        }

        model.addAttribute("availableEquipment", availableEquipment);
        model.addAttribute("rentalPoints", availableEquipmentService.getAllRentalPoints());
        model.addAttribute("equipmentTypes", availableEquipmentService.getAllEquipmentTypes());
        model.addAttribute("action", "edit");
        return "availableEquipments/form";
    }

    // Обработка обновления записи
    @PostMapping("/update/{id}")
    public String updateAvailableEquipment(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("availableEquipment") AvailableEquipment availableEquipment,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rentalPoints", availableEquipmentService.getAllRentalPoints());
            model.addAttribute("equipmentTypes", availableEquipmentService.getAllEquipmentTypes());
            model.addAttribute("action", "edit");
            availableEquipment.setAvailableId(id);
            return "availableEquipments/form";
        }

        try {
            availableEquipmentService.updateAvailableEquipment(id, availableEquipment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Запись успешно обновлена.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("action", "edit");
            availableEquipment.setAvailableId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "availableEquipments/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/availableEquipments";
    }

    // Удаление записи
    @GetMapping("/delete/{id}")
    public String deleteAvailableEquipment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            AvailableEquipment availableEquipment = availableEquipmentService.getAvailableEquipmentById(id)
                    .orElse(null);

            if (availableEquipment != null) {
                availableEquipmentService.deleteAvailableEquipment(id);
                redirectAttributes.addFlashAttribute("successMessage", "Запись успешно удалена.");
                return "redirect:/availableEquipments";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Запись не найдена.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении записи: " + e.getMessage());
        }

        return "redirect:/availableEquipments";
    }

    // Просмотр деталей записи
    @GetMapping("/view/{id}")
    public String viewAvailableEquipment(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        AvailableEquipment availableEquipment = availableEquipmentService.getAvailableEquipmentById(id)
                .orElse(null);

        if (availableEquipment == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Запись не найдена.");
            return "redirect:/availableEquipments";
        }

        model.addAttribute("availableEquipment", availableEquipment);
        return "availableEquipments/view";
    }
}
