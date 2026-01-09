package com.rental.app.controller;

import com.rental.app.entity.EquipmentType;
import com.rental.app.service.EquipmentTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контроллер для работы с типами инвентаря через веб-интерфейс.
 * <p>
 * Обрабатывает HTTP-запросы, связанные с управлением типами инвентаря ({@link EquipmentType}).
 * Предоставляет пользовательский интерфейс для выполнения CRUD-операций: создание,
 * просмотр, редактирование и удаление типов инвентаря, а также поиск по названию и категории.
 * </p>
 * <p>
 * <b>Маршруты:</b> Все методы работают по базовому пути {@code /equipmentTypes}.
 * Использует Thymeleaf шаблоны для отображения HTML-страниц.
 * </p>
 * <p>
 * <b>Особенности:</b> Гарантирует уникальность названий типов инвентаря независимо от регистра.
 * При попытке создания или редактирования типа с уже существующим названием (в любом регистре)
 * выбрасывается исключение {@link IllegalArgumentException}.
 * </p>
 *
 * @see EquipmentType
 * @see EquipmentTypeService
 * @see Controller
 * @see GetMapping
 * @see PostMapping
 */
@Controller
@RequestMapping("/equipmentTypes")
public class EquipmentTypeController {
    private final EquipmentTypeService equipmentTypeService;

    /**
     * Создает контроллер с указанным сервисом типов инвентаря.
     *
     * @param equipmentTypeService сервис для бизнес-логики типов инвентаря
     */
    @Autowired
    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    /**
     * Отображает главную страницу со списком всех типов инвентаря.
     * <p>
     * Получает все типы инвентаря из системы и передает их в модель для отображения.
     * Вычисляет и передает общее количество типов инвентаря.
     * </p>
     *
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "equipmentTypes/list"
     */
    @GetMapping
    public String listEquipmentType(Model model) {
        List<EquipmentType> equipmentTypes = equipmentTypeService.getAllEquipmentTypes();
        model.addAttribute("equipmentTypes", equipmentTypes);
        model.addAttribute("equipmentTypeCount", equipmentTypes.size());
        return "equipmentTypes/list";
    }

    /**
     * Отображает форму для создания нового типа инвентаря.
     * <p>
     * Подготавливает пустой объект {@link EquipmentType} для заполнения в форме.
     * Устанавливает атрибут "action" в значение "create" для правильного отображения формы.
     * </p>
     *
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "equipmentTypes/form"
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("equipmentType", new EquipmentType());
        model.addAttribute("action", "create");
        return "equipmentTypes/form";
    }

    /**
     * Обрабатывает создание нового типа инвентаря.
     * <p>
     * Принимает данные из формы, выполняет валидацию с помощью аннотаций сущности.
     * Проверяет уникальность названия типа инвентаря (без учета регистра).
     * При успешном сохранении перенаправляет на список типов инвентаря с сообщением об успехе.
     * </p>
     *
     * @param equipmentType объект типа инвентаря с данными из формы
     * @param result результат валидации (содержит ошибки, если есть)
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона или редирект на список типов инвентаря
     * @throws IllegalArgumentException если тип инвентаря с таким названием уже существует
     * @see Valid
     * @see BindingResult
     */
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

    /**
     * Отображает форму для редактирования существующего типа инвентаря.
     * <p>
     * Находит тип инвентаря по идентификатору и передает его в форму для редактирования.
     * Если тип инвентаря не найден, перенаправляет на список с сообщением об ошибке.
     * Устанавливает атрибут "action" в значение "edit" для правильного отображения формы.
     * </p>
     *
     * @param id идентификатор редактируемого типа инвентаря
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона или редирект на список типов инвентаря
     */
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

    /**
     * Обрабатывает обновление существующего типа инвентаря.
     * <p>
     * Находит тип инвентаря по идентификатору, обновляет его данные из формы.
     * Проверяет уникальность нового названия (без учета регистра), если название изменилось.
     * При успешном обновлении перенаправляет на список типов инвентаря с сообщением об успехе.
     * </p>
     *
     * @param id идентификатор обновляемого типа инвентаря
     * @param equipmentType объект с новыми данными типа инвентаря
     * @param result результат валидации
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона или редирект на список типов инвентаря
     * @throws IllegalArgumentException если новое название уже используется другим типом инвентаря
     * @throws RuntimeException если тип инвентаря с указанным id не найден
     */
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

    /**
     * Удаляет тип инвентаря по идентификатору.
     * <p>
     * Находит тип инвентаря по идентификатору, удаляет его из системы.
     * При успешном удалении перенаправляет на список типов инвентаря с сообщением об успехе.
     * Если тип инвентаря не найден или произошла ошибка, показывает соответствующее сообщение.
     * </p>
     *
     * @param id идентификатор удаляемого типа инвентаря
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return редирект на список типов инвентаря
     */
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

    /**
     * Отображает страницу с детальной информацией о типе инвентаря.
     * <p>
     * Находит тип инвентаря по идентификатору и отображает все его данные.
     * Если тип инвентаря не найден, перенаправляет на список с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор просматриваемого типа инвентаря
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона или редирект на список типов инвентаря
     */
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

    /**
     * Выполняет поиск типов инвентаря по различным критериям.
     * <p>
     * Поддерживает поиск по названию и категории.
     * Если поисковый запрос пустой, возвращает все типы инвентаря.
     * Поиск выполняется без учета регистра для обоих критериев.
     * По умолчанию используется поиск по названию.
     * </p>
     *
     * @param searchType тип поиска ("typeName", "category")
     * @param searchQuery поисковый запрос (подстрока для поиска)
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "equipmentTypes/list"
     */
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
