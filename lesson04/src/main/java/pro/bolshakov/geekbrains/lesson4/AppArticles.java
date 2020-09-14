package pro.bolshakov.geekbrains.lesson4;

import org.hibernate.cfg.Configuration;
import pro.bolshakov.geekbrains.lesson4.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppArticles {

    public static void main(String[] args) {

        EntityManagerFactory entityFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

        EntityManager em = entityFactory.createEntityManager();

        InitData.initData(em);

        em.close();

        EntityManager emNew = entityFactory.createEntityManager();

//        exampleCascadePersist(emNew);
//        exampleCascadeAll(emNew);

        exampleCascadeRemove(emNew);

        emNew.close();

        entityFactory.close();
    }

    private static void exampleCascadePersist(EntityManager em) {
        //save holder
        {

            em.getTransaction().begin();

            CategoryHolderCascadePersist holder = new CategoryHolderCascadePersist();
            Category category_persist = new Category("Category persist");
            holder.setTitle("Holder Persist");
            holder.setCategory(category_persist);

            em.persist(holder);

            em.getTransaction().commit();
        }
        //find holder and change category through holder
        {
            em.getTransaction().begin();

            CategoryHolderCascadePersist foundHolder = em.find(CategoryHolderCascadePersist.class, 11L);
            foundHolder.setTitle(foundHolder.getTitle() + " updated");
            Category category = foundHolder.getCategory();
            category.setName(category.getName() + " updated");

            em.merge(foundHolder);

            em.getTransaction().commit();
        }

    }

    private static void exampleCascadeRemove(EntityManager em) {
        //https://stackoverflow.com/questions/18373383/jpa-onetoone-difference-between-cascade-merge-and-persist#:~:text=Persist%20and%20merge%20are%20designed,the%20object%20may%20already%20exist.
        //save holder
        {

            em.getTransaction().begin();

            Category category = em.merge(new Category("Category remove"));

            CategoryHolderCascadeRemove holder = new CategoryHolderCascadeRemove();
            holder.setTitle("Holder remove");
            holder.setCategory(category);

            em.persist(holder);

            em.getTransaction().commit();
        }
        //find holder and change category through holder
        {
            em.getTransaction().begin();

            CategoryHolderCascadeRemove foundHolder = em.find(CategoryHolderCascadeRemove.class, 12L);
            em.remove(foundHolder);

            em.getTransaction().commit();
        }

    }

    private static void exampleCascadeAll(EntityManager em) {
        em.getTransaction().begin();

        CategoryHolderCascadeAll holder = new CategoryHolderCascadeAll();
        holder.setTitle("Holder ALL");
        holder.setCategory(new Category("Category depended"));

        em.persist(holder);

        em.getTransaction().commit();
    }

    private static void examplePersistenceArea(EntityManager em) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        Object mutex = new Object();

        Runnable run1 = new Runnable() {
            @Override
            public void run() {
                synchronized (mutex) {
                    Category category1 = em.find(Category.class, InitData.getCategory1().getId());
                    category1.setName("Updated Name");
                    em.merge(category1);

                    System.out.println("Changed name.Waiting notification...");

                    try {
                        mutex.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Continue working after waiting");
                }
            }
        };

        Runnable run2 = new Runnable() {
            @Override
            public void run() {
                synchronized (mutex) {
                    Category category1 = em.find(Category.class, InitData.getCategory1().getId());
                    System.out.println("Read value: " + category1.getName());
                    mutex.notifyAll();
                }
            }
        };

        em.getTransaction().begin();

        threadPool.submit(run1);
        ThreadUtil.uncheckedSleep(2);
        threadPool.submit(run2);

        em.getTransaction().rollback();

        threadPool.shutdown();
    }

    private static void exampleFetching(EntityManager em){

        em.getTransaction().begin();

        Article article = em.find(Article.class, InitData.getArticle1().getId());
        System.out.println("Before reading category");
        System.out.println(article.getCategory());

        System.out.println("before reading lazy category");
        System.out.println(article.getCategoryLazy());

        em.getTransaction().commit();
    }
}