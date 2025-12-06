package org.skypro.projects.personaloffers.component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseChecker {
    private final DataSource dataSource;
    private final Logger logger;

    public DatabaseChecker(DataSource dataSource) {
        this.dataSource = dataSource;
        this.logger = LoggerFactory.getLogger(DatabaseChecker.class);

    }

    @PostConstruct
    public void init() {
        checkConnection();
    }

    public void checkConnection() {
        try (Connection conn = dataSource.getConnection()) {
            logger.info("✅ Database connection successful");
            logger.info("URL: {}", conn.getMetaData().getURL());
            logger.info("Database: {}", conn.getMetaData().getDatabaseProductName());
            logger.info("Version: {}", conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            logger.error("❌ Database connection error", e);
        }
    }
}
