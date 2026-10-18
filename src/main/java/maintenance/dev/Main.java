package maintenance.dev;

import maintenance.dev.models.Client;
import maintenance.dev.models.Coupon;
import maintenance.dev.models.Profile;
import maintenance.dev.repository.CouponRepository;
import maintenance.dev.repository.implRespo.CouponRepoImpl;
import maintenance.dev.service.ClientService;
import maintenance.dev.service.CouponService;
import maintenance.dev.service.OrderService;
import maintenance.dev.service.ProfileService;
import maintenance.dev.service.impl.ClientServiceImpl;
import maintenance.dev.service.impl.CouponServiceImpl;
import maintenance.dev.service.impl.OrderServiceImpl;
import maintenance.dev.service.impl.ProfileServiceImpl;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        HibernateConfig.getSessionFactory(); //обращаясь к классу HibernateConfig, Java загружает его и выполняет static-поле. И тогда происходит соединение с БД
//        HibernateConfig.shutdown(); //закрывает SessionFactory
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        TransactionHelper transactionHelper = new TransactionHelper(sessionFactory);
        ClientService clientService = new ClientServiceImpl(transactionHelper);
        ProfileService profileService = new ProfileServiceImpl(transactionHelper);
        OrderService orderService = new OrderServiceImpl(transactionHelper);
        CouponService couponService = new CouponServiceImpl(transactionHelper);
        Scanner scanner = new Scanner(System.in);
        try {
            boolean run =  true;
            while (run) {
                printMenu();
                switch (scanner.nextLine().trim()) {
                    case "1" -> addClient(scanner, clientService);
                    case "2" -> deleteClient(scanner, clientService);
                    case "3" -> updateProfile(scanner, profileService);
                    case "4" -> createOrder(scanner, orderService);
                    case "5" -> updateCoupon(scanner, couponService);
                    case "6" -> searchOrders(scanner, orderService);
                    case "7" -> run = false;
                    case "8" -> { //Демонстрация N+1: нужны "хороший" и "плохой" метод
                        System.out.println("--- БЕЗ fetch join ---");
                        clientService.printClientsWithOrders(false);
                        System.out.println("\n--- С fetch join ---");
                        clientService.printClientsWithOrders(true);
                    }
                    default -> System.out.println("Invalid input");
                }
            }
        }
        finally { HibernateConfig.shutdown(); }
    }

    private static void printMenu() {
        System.out.println("""
               
                Выберите действие:
                1. Добавить клиента
                2. Удалить клиента
                3. Редактировать профиль
                4. Добавить заказ
                5. Редактировать купоны
                6. Найти заказы
                7. Выход
                Введите номер команды:""");
    }

    private static void addClient(Scanner scanner, ClientService clientService) {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Адрес: ");
        String address = scanner.nextLine();
        System.out.print("Телефон: ");
        String phone = scanner.nextLine();

        System.out.println("ID купонов через запятую (Enter - без купонов): ");
        String couponLine = scanner.nextLine().trim();
        List<Long> couponIds = new ArrayList<>();
        if(!couponLine.isEmpty()) {
            for(String part : couponLine.split(",")) {
                couponIds.add(Long.parseLong(part.trim()));
            }
        }
        Double orderAmount = null;
        String orderStatus = null;
        System.out.println("Добавить заказ (+ / x)? : ");
        if (scanner.nextLine().trim().equals("+")) {
            System.out.println("Сумма заказа: ");
            orderAmount = Double.parseDouble(scanner.nextLine().trim());
            System.out.println("Статус заказа: ");
            orderStatus = scanner.nextLine().trim();
        }
        try {
            Long id = clientService.addClient(name, email, address, phone, couponIds, orderAmount, orderStatus);
            System.out.printf("Создан id клиента: %d%n", id);
        }catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteClient(Scanner scanner, ClientService clientService) {
        System.out.println("ID для удаление клиента: ");
        try{
            Long id = Long.parseLong(scanner.nextLine().trim());
            clientService.deleteClient(id);
            System.out.println("Клиент " + id + " удален вместе с профилем и заказами");
        }catch (NumberFormatException e) {
            System.out.println("ID клиента должен быть числом");
        }catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    private static void updateProfile(Scanner scanner, ProfileService profileService) {
        try{
            System.out.println("Введите ID клиента: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            System.out.println("Ведите новый адрес (Enter - не менять): ");
            String address = scanner.nextLine().trim();
            System.out.println("Введите новый номер (Enter - не менять): ");
            String phone = scanner.nextLine().trim();
            profileService.updateProfile(id, address.isEmpty() ? null : address, phone.isEmpty() ? null : phone);
            System.out.println("Профиль изменен!");
        }catch (NumberFormatException e) {
            System.out.println("ID должен быть числом!");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    private static void createOrder(Scanner scanner, OrderService orderService) {
        try {
            System.out.println("Введите ID клиента: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            System.out.println("Введите сумму: ");
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            System.out.println("Введите статус заказа: ");
            String orderStatus = scanner.nextLine().trim();
            orderService.createOrder(id, LocalDateTime.now(), amount, orderStatus);
            System.out.println("Заказ создан");
        }catch (NumberFormatException e) {
            System.out.println("ID должен быть числом!");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    private static void updateCoupon(Scanner scanner, CouponService couponService) {
        try {
            System.out.println("Введите ID купона: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            boolean expiredDate = couponService.isExpiredDate(id);
            LocalDateTime newExpirationDate = null;
            if (expiredDate) {
                System.out.println("Срок купода истек. Нужно ввести новый!");
                long days;
                do{
                    System.out.println("На ск-ко дней продлить (больше 0): ");
                    days = Long.parseLong(scanner.nextLine().trim());
                }while (days < 0);
                newExpirationDate = LocalDateTime.now().plusDays(days);
            }
            System.out.println("Новый код промокода (Enter - не менять): ");
            String code = scanner.nextLine().trim();
            System.out.println("Новая скидка (Enter - не менять): ");
            String discount = scanner.nextLine().trim();
            couponService.updateCoupon(id, code.isEmpty() ? null : code, discount.isEmpty() ? null : Float.parseFloat(discount), newExpirationDate );
            System.out.println("Купон сохарнен" + id);
        }catch (NumberFormatException e) {
            System.out.println("ID должен быть числом!");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    private static void searchOrders(Scanner scanner, OrderService orderService) {
        try{
            System.out.print("Дата (гггг-мм-дд, Enter - пропустить): ");
            String d = scanner.nextLine().trim();
            System.out.print("Минимальная сумма (Enter - пропустить): ");
            String a = scanner.nextLine().trim();
            System.out.print("Статус (Enter - пропустить): ");
            String s = scanner.nextLine().trim();
            List<String> result = orderService.searchOrders(d.isEmpty() ? null : LocalDateTime.parse(d), a.isEmpty() ? null : Double.parseDouble(a), s.isEmpty() ? null : s);
            if(result.isEmpty()){
                System.out.println("Заказы не найдены");
            }else {
                System.out.println("Найдено заказов: " + result.size());
                result.forEach(System.out::println);
            }
        }catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты, нужно гггг-мм-дд");
        } catch (NumberFormatException e) {
            System.out.println("Сумма должна быть числом");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}