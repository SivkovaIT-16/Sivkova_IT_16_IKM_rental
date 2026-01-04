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

/**
 * Контроллер для работы с доступным инвентарем через веб-интерфейс.
 * <p>
 * Обрабатывает HTTP-запросы, связанные с управлением наличием инвентаря в пунктах проката ({@link AvailableEquipment}).
 * Предоставляет пользовательский интерфейс для выполнения CRUD-операций над связями "пункт проката - тип инвентаря".
 * Отображает статистику по доступному инвентарю и обеспечивает выбор пунктов проката и типов инвентаря из выпадающих списков.
 * </p>
 * <p>
 * <b>Маршруты:</b> Все методы работают по базовому пути {@code /availableEquipments}.
 * Использует Thymeleaf шаблоны для отображения HTML-страниц.
 * </p>
 * <p>
 * <b>Особенности:</b> Обеспечивает уникальность комбинации "пункт проката + тип инвентаря".
 * Нельзя добавить один и тот же тип инвентаря в один пункт проката дважды.
 * При создании и редактировании загружает списки всех пунктов проката и типов инвентаря для выбора.
 * </p>
 *
 * @see AvailableEquipment
 * @see AvailableEquipmentService
 * @see Controller
 * @see GetMapping
 * @see PostMapping
 */
@Controller
@RequestMapping("/availableEquipments")
public class AvailableEquipmentController {
    private final AvailableEquipmentService availableEquipmentService;

    /**
     * Создает контроллер с указанным сервисом доступного инвентаря.
     *
     * @param availableEquipmentService сервис для бизнес-логики доступного инвентаря
     */
    @Autowired
    public AvailableEquipmentController(AvailableEquipmentService availableEquipmentService) {
        this.availableEquipmentService = availableEquipmentService;
    }

    /**
     * Отображает главную страницу со списком всего доступного инвентаря.
     * <p>
     * Получает все записи о доступном инвентаре из системы и передает их в модель для отображения.
     * Вычисляет и передает две статистики:
     * <ul>
     *   <li>Общее количество записей</li>
     *   <li>Количество записей с положительным остатком (availableCount > 0)</li>
     * </ul>
     * </p>
     *
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "availableEquipments/list"
     */
    @GetMapping
    public String listAvailableEquipments(Model model) {
        List<AvailableEquipment> availableEquipments = availableEquipmentService.getAllAvailableEquipments();
        model.addAttribute("availableEquipments", availableEquipments);
        model.addAttribute("equipmentCount", availableEquipments.size());
        model.addAttribute("availableEquipmentCount", availableEquipmentService.getAvailableEquipmentsCountWithStock());
        return "availableEquipments/list";
    }

    /**
     * Отображает форму для создания новой записи о доступном инвентаре.
     * <p>
     * Подготавливает пустой объект {@link AvailableEquipment} для заполнения в форме.
     * Загружает списки всех пунктов проката и типов инвентаря для отображения в выпадающих списках.
     * Устанавливает атрибут "action" в значение "create" для правильного отображения формы.
     * </p>
     *
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "availableEquipments/form"
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("availableEquipment", new AvailableEquipment());
        model.addAttribute("rentalPoints", availableEquipmentService.getAllRentalPoints());
        model.addAttribute("equipmentTypes", availableEquipmentService.getAllEquipmentTypes());
        model.addAttribute("action", "create");
        return "availableEquipments/form";
    }

    /**
     * Обрабатывает создание новой записи о доступном инвентаре.
     * <p>
     * Принимает данные из формы, выполняет валидацию с помощью аннотаций сущности.
     * Проверяет уникальность комбинации "пункт проката + тип инвентаря".
     * При успешном сохранении перенаправляет на список записей с сообщением об успехе.
     * В случае ошибки заново загружает списки пунктов проката и типов инвентаря.
     * </p>
     *
     * @param availableEquipment объект доступного инвентаря с данными из формы
     * @param result результат валидации (содержит ошибки, если есть)
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона или редирект на список записей
     * @throws IllegalArgumentException если комбинация "пункт проката + тип инвентаря" уже существует,
     * или если указанные пункт проката или тип инвентаря не существуют
     * @see Valid
     * @see BindingResult
     */
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

    /**
     * Отображает форму для редактирования существующей записи о доступном инвентаре.
     * <p>
     * Находит запись по идентификатору и передает ее в форму для редактирования.
     * Загружает списки всех пунктов проката и типов инвентаря для отображения в выпадающих списках.
     * Если запись не найдена, перенаправляет на список с сообщением об ошибке.
     * Устанавливает атрибут "action" в значение "edit" для правильного отображения формы.
     * </p>
     *
     * @param id идентификатор редактируемой записи
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона или редирект на список записей
     */
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

    /**
     * Обрабатывает обновление существующей записи о доступном инвентаре.
     * <p>
     * Находит запись по идентификатору, обновляет ее данные из формы.
     * Проверяет уникальность новой комбинации "пункт проката + тип инвентаря", если пункт или тип были изменены.
     * При успешном обновлении перенаправляет на список записей с сообщением об успехе.
     * В случае ошибки заново загружает списки пунктов проката и типов инвентаря.
     * </p>
     *
     * @param id идентификатор обновляемой записи
     * @param availableEquipment объект с новыми данными записи
     * @param result результат валидации
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона или редирект на список записей
     * @throws IllegalArgumentException если новая комбинация "пункт проката + тип инвентаря" уже существует,
     * или если указанные пункт проката или тип инвентаря не существуют
     * @throws RuntimeException если запись с указанным id не найдена
     */
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

    /**
     * Удаляет запись о доступном инвентаре по идентификатору.
     * <p>
     * Находит запись по идентификатору, удаляет ее из системы.
     * При успешном удалении перенаправляет на список записей с сообщением об успехе.
     * Если запись не найдена или произошла ошибка, показывает соответствующее сообщение.
     * </p>
     *
     * @param id идентификатор удаляемой записи
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return редирект на список записей
     */
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

    /**
     * Отображает страницу с детальной информацией о записи доступного инвентаря.
     * <p>
     * Находит запись по идентификатору и отображает все ее данные, включая связанные
     * пункт проката и тип инвентаря.
     * Если запись не найдена, перенаправляет на список с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор просматриваемой записи
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона или редирект на список записей
     */
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
