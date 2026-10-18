package maintenance.dev;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionHelper {
    private final SessionFactory sessionFactory;
    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    //ничего не возвращает: create / delete
    public void executeInResult(Consumer<Session> action) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        }catch(RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }finally {
            session.close();
        }
    }

    //возвращает результат: найти, получить список
    public  <T> T executeWithResult(Function<Session, T> action) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        }catch(RuntimeException e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw e;
        }finally {
            session.close();
        }
    }
}
