package maintenance.dev;

import maintenance.dev.models.Client;
import maintenance.dev.models.Coupon;
import maintenance.dev.models.Order;
import maintenance.dev.models.Profile;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateConfig {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();
    private HibernateConfig() {}
    private static SessionFactory buildSessionFactory() {
        Properties prop = new Properties();
        prop.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        prop.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/testdb");
        prop.put("hibernate.connection.username", "root");
        prop.put("hibernate.connection.password", "secret");

        prop.put("hibernate.hbm2ddl.auto", "update");
        prop.put("hibernate.show_sql", "true");
        prop.put("hibernate.format_sql", "true"); //настройка для красивого форматирования SQL в консоли (необязательно)
        return  new Configuration().setProperties(prop).addAnnotatedClasses(Client.class, Coupon.class, Order.class, Profile.class)
                .buildSessionFactory();
    }
    public static SessionFactory getSessionFactory() {return SESSION_FACTORY; }
    public static void shutdown() { SESSION_FACTORY.close(); }
}
