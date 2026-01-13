package org.skypro.projects.personaloffers.component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class PrimaryDatabaseChecker {
    private final DataSource dataSource;
    private final Logger logger;

    public PrimaryDatabaseChecker(@Qualifier("primaryDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
        this.logger = LoggerFactory.getLogger(PrimaryDatabaseChecker.class);

    }

    @PostConstruct
    public void init() {
        checkConnection();
    }

    public void checkConnection() {
        try (Connection conn = dataSource.getConnection()) {
            logger.info("✅ Primary Database connection successful");
            logger.info("URL: {}", conn.getMetaData().getURL());
            logger.info("Database: {}", conn.getMetaData().getDatabaseProductName());
            logger.info("Version: {}", conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            logger.error("❌ Primary Database connection error", e);
        }
    }
}
