package com.rental.app.controller;

import com.rental.app.entity.RentalPoint;
import com.rental.app.service.RentalPointService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контроллер для работы с пунктами проката через веб-интерфейс.
 * <p>
 * Обрабатывает HTTP-запросы, связанные с управлением пунктами проката ({@link RentalPoint}).
 * Предоставляет пользовательский интерфейс для выполнения CRUD-операций: создание,
 * просмотр, редактирование и удаление пунктов проката, а также поиск по различным критериям.
 * </p>
 * <p>
 * <b>Маршруты:</b> Все методы работают по базовому пути {@code /rentalPoints}.
 * Использует Thymeleaf шаблоны для отображения HTML-страниц.
 * </p>
 *
 * @see RentalPoint
 * @see RentalPointService
 * @see Controller
 * @see GetMapping
 * @see PostMapping
 */
@Controller
@RequestMapping("/rentalPoints")
public class RentalPointController {
    private final RentalPointService rentalPointService;

    /**
     * Создает контроллер с указанным сервисом пунктов проката.
     *
     * @param rentalPointService сервис для бизнес-логики пунктов проката
     */
    @Autowired
    public RentalPointController(RentalPointService rentalPointService) {
        this.rentalPointService = rentalPointService;
    }

    /**
     * Отображает главную страницу со списком всех пунктов проката.
     * <p>
     * Получает все пункты проката из системы и передает их в модель для отображения.
     * Вычисляет и передает общее количество пунктов проката.
     * </p>
     *
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "rentalPoints/list"
     */
    @GetMapping
    public String listRentalPoints(Model model) {
        List<RentalPoint> rentalPoints = rentalPointService.getAllRentalPoints();
        model.addAttribute("rentalPoints", rentalPoints);
        model.addAttribute("rentalPointCount", rentalPoints.size());
        return "rentalPoints/list";
    }

    /**
     * Отображает форму для создания нового пункта проката.
     * <p>
     * Подготавливает пустой объект {@link RentalPoint} для заполнения в форме.
     * Устанавливает атрибут "action" в значение "create" для правильного отображения формы.
     * </p>
     *
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "rentalPoints/form"
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("rentalPoint", new RentalPoint());
        model.addAttribute("action", "create");
        return "rentalPoints/form";
    }

    /**
     * Обрабатывает создание нового пункта проката.
     * <p>
     * Принимает данные из формы, выполняет валидацию с помощью аннотаций сущности.
     * Если есть ошибки валидации, возвращает форму с сообщениями об ошибках.
     * При успешном сохранении перенаправляет на список пунктов проката с сообщением об успехе.
     * </p>
     *
     * @param rentalPoint объект пункта проката с данными из формы
     * @param result результат валидации (содержит ошибки, если есть)
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона или редирект на список пунктов проката
     * @see Valid
     * @see BindingResult
     */
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

    /**
     * Отображает форму для редактирования существующего пункта проката.
     * <p>
     * Находит пункт проката по идентификатору и передает его в форму для редактирования.
     * Если пункт проката не найден, перенаправляет на список с сообщением об ошибке.
     * Устанавливает атрибут "action" в значение "edit" для правильного отображения формы.
     * </p>
     *
     * @param id идентификатор редактируемого пункта проката
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона или редирект на список пунктов проката
     */
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

    /**
     * Обрабатывает обновление существующего пункта проката.
     * <p>
     * Находит пункт проката по идентификатору, обновляет его данные из формы.
     * Выполняет валидацию и проверку уникальности нового адреса (если адрес изменился).
     * При успешном обновлении перенаправляет на список пунктов проката с сообщением об успехе.
     * </p>
     *
     * @param id идентификатор обновляемого пункта проката
     * @param rentalPoint объект с новыми данными пункта проката
     * @param result результат валидации
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона или редирект на список пунктов проката
     * @throws IllegalArgumentException если новый адрес уже используется другим пунктом проката
     */
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

    /**
     * Удаляет пункт проката по идентификатору.
     * <p>
     * Находит пункт проката по идентификатору, удаляет его из системы.
     * При успешном удалении перенаправляет на список пунктов проката с сообщением об успехе.
     * Если пункт проката не найден или произошла ошибка, показывает соответствующее сообщение.
     * </p>
     *
     * @param id идентификатор удаляемого пункта проката
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return редирект на список пунктов проката
     */
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

    /**
     * Отображает страницу с детальной информацией о пункте проката.
     * <p>
     * Находит пункт проката по идентификатору и отображает все его данные.
     * Если пункт проката не найден, перенаправляет на список с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор просматриваемого пункта проката
     * @param model модель для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона или редирект на список пунктов проката
     */
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

    /**
     * Выполняет поиск пунктов проката по различным критериям.
     * <p>
     * Поддерживает поиск по названию, адресу и часам работы.
     * Если поисковый запрос пустой, возвращает все пункты проката.
     * Поиск по названию и адресу выполняется без учета регистра.
     * </p>
     *
     * @param searchType тип поиска ("pointName", "location", "openingHours")
     * @param searchQuery поисковый запрос (подстрока для поиска)
     * @param model модель для передачи данных в представление
     * @return имя Thymeleaf шаблона "rentalPoints/list"
     */
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
