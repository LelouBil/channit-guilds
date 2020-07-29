package net.leloubil.channitguilds;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.leloubil.channitguilds.entities.ChannitGuild;
import net.leloubil.channitguilds.entities.ChannitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.reflections.Reflections;

import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DatabaseLink {
    @Getter
    private final ConfigManager configManager;

    private SessionFactory sessionFactory;

    public void initDatabase() {
        ConfigManager.ChannitGuildsConfig conf = configManager.getGuildsConfig();
        final StandardServiceRegistry reg = getRegistry(conf.getDatabaseAddress(),conf.getDatabasePort(),conf.getDatabaseName(),conf.getDatabaseUserName(),conf.getDatabasePassword());
        MetadataSources metadataSources = new MetadataSources(reg);

        Reflections reflections = new Reflections("net.leloubil.channitguilds.entities");
        reflections.getTypesAnnotatedWith(Entity.class).forEach(metadataSources::addAnnotatedClass);
        sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
    }

    @Transactional
    public void saveObject(Object... o){
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        try{
            for (Object o1 : o) {
                s.saveOrUpdate(o1);
            }
            tx.commit();
        } catch (HibernateException e){
            e.printStackTrace();
            tx.rollback();
        }
        finally {
            s.close();
        }
    }

    private static StandardServiceRegistry getRegistry(String hostname, int port, String
            schema, String username, String password) {
        StandardServiceRegistry registry = null;
            registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.connection.username", username)
                    .applySetting("hibernate.connection.password", password)
                    .applySetting("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
                    .applySetting("hibernate.connection.url", "jdbc:mysql://" + hostname + ":" + port + "/" + schema + "?verifyServerCertificate=false&useSSL=false&useUnicode=true&characterEncoding=utf-8")
                    .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect")
                    .applySetting("connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
                    .applySetting("hibernate.hbm2ddl.auto","update")
                    .applySetting("hibernate.hikari.connectionTimeout", "20000")
                    .applySetting("hibernate.hikari.minimumIdle", "10")
                    .applySetting("hibernate.hikari.maximumPoolSize", "5")
                    .applySetting("hibernate.hikari.idleTimeout", "300000")
                    .applySetting("hibernate.enable_lazy_load_no_trans", true)
                    .applySetting("hibernate.show_sql", false)
                    .applySetting("hibernate.format_sql", false)
                    .build();
            return registry;
    }

    public ChannitPlayer getChannitPlayer(Player p){
        try (Session s = sessionFactory.openSession()) {
            ChannitPlayer db = s.get(ChannitPlayer.class, p.getUniqueId());

            if (db == null) {
                return new ChannitPlayer(p);
            }
            db.setPlayer(p);
            return db;
        }
    }
    public ChannitPlayer getChannitPlayer(UUID u){
        try (Session s = sessionFactory.openSession()) {
            ChannitPlayer db = s.get(ChannitPlayer.class, u);

            if (db == null) {
                return new ChannitPlayer(Bukkit.getOfflinePlayer(u));
            }
            db.setPlayer(Bukkit.getOfflinePlayer(u));
            return db;
        }
    }

    public ChannitPlayer getChannitPlayer(String username){
        OfflinePlayer p = Bukkit.getOfflinePlayer(username);
        return getChannitPlayer(p.getUniqueId());
    }

    public Optional<ChannitGuild> getGuildByName(String name){
        try(Session s = sessionFactory.openSession()){
            ChannitGuild g = s.get(ChannitGuild.class,name);
            return Optional.ofNullable(g);
        }
    }

    public List<ChannitGuild> getAllGuilds(){
        try(Session s = sessionFactory.openSession()){
            CriteriaQuery<ChannitGuild> query = s.getCriteriaBuilder().createQuery(ChannitGuild.class);
            return s.createQuery(query).getResultList();
        }
    }
}
