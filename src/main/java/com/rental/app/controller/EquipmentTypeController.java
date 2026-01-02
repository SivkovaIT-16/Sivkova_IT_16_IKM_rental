package com.rental.app.controller;

import com.rental.app.model.entity.EquipmentType;
import com.rental.app.service.EquipmentTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/equipmentTypes")
public class EquipmentTypeController {
    private final EquipmentTypeService equipmentTypeService;

    @Autowired
    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    // Главная страница - список всех типов инвентаря
    @GetMapping
    public String listEquipmentType(Model model) {
        List<EquipmentType> equipmentTypes = equipmentTypeService.getAllEquipmentTypes();
        model.addAttribute("equipmentTypes", equipmentTypes);
        model.addAttribute("equipmentTypeCount", equipmentTypes.size());
        return "equipmentTypes/list";
    }

    // Страница добавления нового типа инвентаря
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("equipmentType", new EquipmentType());
        model.addAttribute("action", "create");
        return "equipmentTypes/form";
    }

    // Обработка создания нового типа инвентаря
    @PostMapping
    public String createEquipmentType(@Valid @ModelAttribute("equipmentType") EquipmentType equipmentType,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "create");
            return "equipmentTypes/form";
        }

        try {
            equipmentTypeService.saveEquipmentType(equipmentType);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Тип инвентаря " + equipmentType.getTypeName() + " успешно добавлен.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("action", "create");
            model.addAttribute("errorMessage", e.getMessage());
            return "equipmentTypes/form";
        } catch (Exception e) {
            model.addAttribute("action", "create");
            model.addAttribute("errorMessage", "Ошибка при сохранении: " + e.getMessage());
            return "equipmentTypes/form";
        }
        return "redirect:/equipmentTypes";
    }

    // Страница редактирования типа инвентаря
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        EquipmentType equipmentType = equipmentTypeService.getEquipmentTypeById(id)
                .orElse(null);

        if (equipmentType == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Тип инвентаря не найден.");
            return "redirect:/equipmentTypes";
        }

        model.addAttribute("equipmentType", equipmentType);
        model.addAttribute("action", "edit");
        return "equipmentTypes/form";
    }

    // Обработка обновления типа инвентаря
    @PostMapping("/update/{id}")
    public String updateEquipmentType(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("equipmentType") EquipmentType equipmentType,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            equipmentType.setId(id);
            return "equipmentTypes/form";
        }

        try {
            equipmentTypeService.updateEquipmentType(id, equipmentType);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Тип инвентаря " + equipmentType.getTypeName() + " успешно обновлён.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("action", "edit");
            equipmentType.setId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "equipmentTypes/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/equipmentTypes";
    }

    // Удаление типа инвентаря
    @GetMapping("/delete/{id}")
    public String deleteEquipmentType(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            EquipmentType equipmentType = equipmentTypeService.getEquipmentTypeById(id)
                    .orElse(null);

            if (equipmentType != null) {
                equipmentTypeService.deleteEquipmentType(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Тип инвентаря " + equipmentType.getTypeName() + " успешно удален.");
                return "redirect:/equipmentTypes";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Тип инвентаря не найден.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении типа инвентаря: " + e.getMessage());
        }

        return "redirect:/equipmentTypes";
    }

    // Просмотр деталей типа инвентаря
    @GetMapping("/view/{id}")
    public String viewEquipmentType(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        EquipmentType equipmentType = equipmentTypeService.getEquipmentTypeById(id)
                .orElse(null);

        if (equipmentType == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Тип инвентаря не найден.");
            return "redirect:/equipmentTypes";
        }

        model.addAttribute("equipmentType", equipmentType);
        return "equipmentTypes/view";
    }

    // Поиск типов инвентаря
    @GetMapping("/search")
    public String searchEquipmentTypes(@RequestParam(required = false) String searchType,
                                     @RequestParam(required = false) String searchQuery,
                                     Model model) {
        List<EquipmentType> equipmentTypes;

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            equipmentTypes = equipmentTypeService.getAllEquipmentTypes();
        } else {
            switch (searchType != null ? searchType : "typeName") {
                case "category":
                    equipmentTypes = equipmentTypeService.searchByCategory(searchQuery);
                    break;
                default:
                    equipmentTypes = equipmentTypeService.searchByTypeName(searchQuery);
                    break;
            }
        }

        model.addAttribute("equipmentTypes", equipmentTypes);
        model.addAttribute("equipmentTypeCount", equipmentTypes.size());
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchQuery", searchQuery);
        return "equipmentTypes/list";
    }
}
