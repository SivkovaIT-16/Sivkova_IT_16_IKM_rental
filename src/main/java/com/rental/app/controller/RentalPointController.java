package com.rental.app.controller;

import com.rental.app.model.entity.RentalPoint;
import com.rental.app.service.RentalPointService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/rentalPoints")
public class RentalPointController {
    private final RentalPointService rentalPointService;

    @Autowired
    public RentalPointController(RentalPointService rentalPointService) {
        this.rentalPointService = rentalPointService;
    }

    // Главная страница - список всех точек проката
    @GetMapping
    public String listRentalPoints(Model model) {
        List<RentalPoint> rentalPoints = rentalPointService.getAllRentalPoints();
        model.addAttribute("rentalPoints", rentalPoints);
        model.addAttribute("rentalPointCount", rentalPoints.size());
        return "rentalPoints/list";
    }

    // Страница добавления нового пункта проката
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("rentalPoint", new RentalPoint());
        model.addAttribute("action", "create");
        return "rentalPoints/form";
    }

    // Обработка создания нового пункта проката
    @PostMapping
    public String createRentalPoint(@Valid @ModelAttribute("rentalPoint") RentalPoint rentalPoint,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "create");
            return "rentalPoints/form";
        }
        try {
            rentalPointService.saveRentalPoint(rentalPoint);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Пункт проката " + rentalPoint.getPointName() + " успешно добавлен.");
            return "redirect:/rentalPoints";
        } catch (Exception e) {
            model.addAttribute("action", "create");
            model.addAttribute("errorMessage", "Ошибка при сохранении: " + e.getMessage());
            return "rentalPoints/form";
        }
    }

    // Страница редактирования пункта проката
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        RentalPoint rentalPoint = rentalPointService.getRentalPointById(id)
                .orElse(null);

        if (rentalPoint == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пункт проката не найден.");
            return "redirect:/rentalPoints";
        }

        model.addAttribute("rentalPoint", rentalPoint);
        model.addAttribute("action", "edit");
        return "rentalPoints/form";
    }

    // Обработка обновления пункта проката
    @PostMapping("/update/{id}")
    public String updateRentalPoint(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("rentalPoint") RentalPoint rentalPoint,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            rentalPoint.setId(id);
            return "rentalPoints/form";
        }

        try {
            rentalPointService.updateRentalPoint(id, rentalPoint);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Пункт проката " + rentalPoint.getPointName() + " успешно обновлён.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("action", "edit");
            rentalPoint.setId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "rentalPoints/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/rentalPoints";
    }

    // Удаление пункта проката
    @GetMapping("/delete/{id}")
    public String deleteRentalPoint(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            RentalPoint rentalPoint = rentalPointService.getRentalPointById(id)
                    .orElse(null);

            if (rentalPoint != null) {
                rentalPointService.deleteRentalPoint(id);
                redirectAttributes.addFlashAttribute("successMessage", "Пункт проката " + rentalPoint.getPointName() + " успешно удален.");
                return "redirect:/rentalPoints";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Пункт проката не найден.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении пункта проката: " + e.getMessage());
        }

        return "redirect:/rentalPoints";
    }

    // Просмотр деталей пункта проката
    @GetMapping("/view/{id}")
    public String viewRentalPoint(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        RentalPoint rentalPoint = rentalPointService.getRentalPointById(id)
                .orElse(null);

        if (rentalPoint == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пункт проката не найден.");
            return "redirect:/rentalPoints";
        }

        model.addAttribute("rentalPoint", rentalPoint);
        return "rentalPoints/view";
    }

    // Поиск пунктов проката
    @GetMapping("/search")
    public String searchRentalPoints(@RequestParam(required = false) String searchType,
                                     @RequestParam(required = false) String searchQuery,
                                     Model model) {
        List<RentalPoint> rentalPoints;

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            rentalPoints = rentalPointService.getAllRentalPoints();
        } else {
            switch (searchType != null ? searchType : "pointName") {
                case "location":
                    rentalPoints = rentalPointService.searchByLocation(searchQuery);
                    break;
                case "openingHours":
                    rentalPoints = rentalPointService.searchByOpeningHours(searchQuery);
                    break;
                default:
                    rentalPoints = rentalPointService.searchByPointName(searchQuery);
                    break;
            }
        }

        model.addAttribute("rentalPoints", rentalPoints);
        model.addAttribute("rentalPointCount", rentalPoints.size());
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchQuery", searchQuery);
        return "rentalPoints/list";
    }
}
