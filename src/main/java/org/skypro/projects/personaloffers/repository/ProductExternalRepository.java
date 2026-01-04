package org.skypro.projects.personaloffers.repository;

import org.skypro.projects.personaloffers.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductExternalRepository {

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public List<Product> findProductsByUserIdAndType(UUID userId, String type) {
        String sql = "SELECT DISTINCT p.ID, p.TYPE, p.NAME " +
                "FROM PRODUCTS p " +
                "JOIN TRANSACTIONS t ON p.ID = t.PRODUCT_ID " +
                "WHERE t.USER_ID = ? AND p.TYPE = ?";
        return jdbcTemplate.query(sql, new Object[]{userId, type}, this::mapRowToProduct);
    }

    public List<Product> findProductsByUserId(UUID userId) {
        String sql = "SELECT DISTINCT p.ID, p.TYPE, p.NAME " +
                "FROM PRODUCTS p " +
                "JOIN TRANSACTIONS t ON p.ID = t.PRODUCT_ID " +
                "WHERE t.USER_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, this::mapRowToProduct);
    }

    public double getTopUpAmountForSavingProducts(UUID userId) {
        String sql = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
                "FROM TRANSACTIONS t " +
                "JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID " +
                "WHERE t.USER_ID = ? " +
                "AND p.TYPE = 'SAVING' " +
                "AND t.TYPE = 'DEPOSIT'";
        return jdbcTemplate.queryForObject(sql, Double.class, userId);
    }

    public double getTopUpAmountForDebitProducts(UUID userId) {
        String sql = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
                "FROM TRANSACTIONS t " +
                "JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID " +
                "WHERE t.USER_ID = ? " +
                "AND p.TYPE = 'DEBIT' " +
                "AND t.TYPE = 'DEPOSIT'";
        return jdbcTemplate.queryForObject(sql, Double.class, userId);
    }

    public double getExpenseAmountForDebitProducts(UUID userId) {
        String sql = "SELECT COALESCE(SUM(t.AMOUNT), 0) " +
                "FROM TRANSACTIONS t " +
                "JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID " +
                "WHERE t.USER_ID = ? " +
                "AND p.TYPE = 'DEBIT' " +
                "AND t.TYPE = 'WITHDRAW'";
        return jdbcTemplate.queryForObject(sql, Double.class, userId);
    }

    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                UUID.fromString(rs.getString("ID")),
                rs.getString("TYPE"),
                rs.getString("NAME")
        );
    }
}
