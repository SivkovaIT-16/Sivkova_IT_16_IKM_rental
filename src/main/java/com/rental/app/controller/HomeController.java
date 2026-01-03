package com.rental.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер домашней страницы приложения для управления прокатом инвентаря.
 * <p>
 * Отвечает за обработку запросов к корневому URL.
 * Обеспечивает точку входа в систему, автоматически направляя
 * пользователей на главный раздел приложения.
 * </p>
 *
 * @see AvailableEquipmentController
 * @see Controller
 */
@Controller
public class HomeController {
    /**
     * Обрабатывает запросы к корневому URL приложения ("/").
     * <p>
     * Автоматически перенаправляет пользователей на страницу управления инвентарем,
     * которая является основной функциональной страницей приложения.
     * </p>
     *
     * @return строка "redirect:/availableEquipments" для перенаправления на страницу инвентаря
     * @see AvailableEquipmentController
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/availableEquipments";
    }
}
