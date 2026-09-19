package com.mithra.mithramart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DataSourceListener implements ServletContextListener {

    private static HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:./data/mithramart;AUTO_SERVER=TRUE");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
        System.out.println("HikariCP connection pool started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("HikariCP connection pool closed.");
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
