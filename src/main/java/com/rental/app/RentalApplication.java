package com.rental.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс веб-приложения для управления прокатом инвентаря.
 * Содержит точку входа для запуска приложения.
 * <p>
 * Приложение использует Spring MVC для создания веб-интерфейса:
 * <ul>
 *     <li><b>Model</b> - данные об инвентаре, пунктах проката</li>
 *     <li><b>View</b> - HTML страницы для отображения данных</li>
 *     <li><b>Controller</b> - обработка запросов пользователя</li>
 * </ul>
 * Предоставляет следующие функции управления инвентарем в пунктах проката:
 * <ul>
 *   <li>Просмотр доступного инвентаря в пунктах проката</li>
 *   <li>Добавление нового инвентаря</li>
 *   <li>Редактирование существующих записей</li>
 *   <li>Удаление записей об инвентаре</li>
 * </ul>
 *
 * @see SpringBootApplication
 * @see SpringApplication
 */
@SpringBootApplication
public class RentalApplication {
    /**
     * Точка входа в приложение.
     * <p>
     * Метод запускает веб-приложение и делает его доступным через веб-браузер.
     * Инициализирует сервер, подключает базу данных и активирует все компоненты системы.
     * <p>
     * После успешного запуска приложение будет доступно
     * по стандартному адресу http://localhost:8080
     *
     * @param args аргументы командной строки, могут использоваться для
     * дополнительной настройки приложения
     * @throws Exception если произошла ошибка при запуске приложения
     * @see SpringApplication#run(Class, String...)
     */
    public static void main(String[] args) {
        SpringApplication.run(RentalApplication.class, args);
    }
}
