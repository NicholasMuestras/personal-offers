package org.skypro.projects.personaloffers.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.skypro.projects.personaloffers.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class ProductExternalRepository {

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private final Cache<QueryKey, Boolean> queryCache;

    
    @Value("${product.external.repository.cache.expiration.minutes:1}")
    private int cacheExpirationMinutes;

    @Value("${product.external.repository.cache.maximum.size:1000}")
    private int cacheMaximumSize;

    
    public ProductExternalRepository() {
        this.queryCache = Caffeine.newBuilder()
            .expireAfterWrite(cacheExpirationMinutes, TimeUnit.MINUTES)
            .maximumSize(cacheMaximumSize)
            .build();
    }

    public boolean evaluateQuery(String queryName, UUID userId, List<String> arguments) {
        QueryKey key = new QueryKey(queryName, userId, arguments);
        
        Boolean cachedResult = queryCache.getIfPresent(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        StringBuilder sqlBuilder = new StringBuilder();

        switch (queryName) {
            case "USER_OF":
                sqlBuilder.append("SELECT COUNT(*) ");
                sqlBuilder.append("FROM PRODUCTS p ");
                sqlBuilder.append("JOIN TRANSACTIONS t ON p.ID = t.PRODUCT_ID ");
                sqlBuilder.append("WHERE t.USER_ID = ? AND p.TYPE IN (");
                sqlBuilder.append(getPlaceholdersForInClause(arguments.size()));
                sqlBuilder.append(")");
                break;
            case "ACTIVE_USER_OF":
                sqlBuilder.append("SELECT COUNT(*) ");
                sqlBuilder.append("FROM TRANSACTIONS t ");
                sqlBuilder.append("JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID ");
                sqlBuilder.append("WHERE t.USER_ID = ? AND p.TYPE IN (");
                sqlBuilder.append(getPlaceholdersForInClause(arguments.size()));
                sqlBuilder.append(") AND t.AMOUNT > 4");
                break;
            case "TRANSACTION_SUM_COMPARE":
                validateTransactionSumCompareArguments(arguments);
                sqlBuilder.append("SELECT COALESCE(SUM(t.AMOUNT), 0) ");
                sqlBuilder.append("FROM TRANSACTIONS t ");
                sqlBuilder.append("JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID ");
                sqlBuilder.append("WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?");
                break;
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                validateTransactionSumCompareDepositWithdrawArguments(arguments);
                sqlBuilder.append("WITH deposit_sum AS (SELECT COALESCE(SUM(t1.AMOUNT), 0) as sum FROM TRANSACTIONS t1 JOIN PRODUCTS p1 ON t1.PRODUCT_ID = p1.ID WHERE t1.USER_ID = ? AND p1.TYPE = ? AND t1.TYPE = 'DEPOSIT'), ");
                sqlBuilder.append("withdraw_sum AS (SELECT COALESCE(SUM(t2.AMOUNT), 0) as sum FROM TRANSACTIONS t2 JOIN PRODUCTS p2 ON t2.PRODUCT_ID = p2.ID WHERE t2.USER_ID = ? AND p2.TYPE = ? AND t2.TYPE = 'WITHDRAW') ");
                sqlBuilder.append("SELECT CASE WHEN d.sum ");
                sqlBuilder.append(arguments.get(1));
                sqlBuilder.append(" w.sum THEN 1 ELSE 0 END FROM deposit_sum d, withdraw_sum w");
                break;
            default:
                throw new IllegalArgumentException("Unknown query: " + queryName);
        }

        String sql = sqlBuilder.toString();

        try {
            if (arguments.isEmpty()) {
                queryCache.put(key, false);
                return false;
            }

            for (String arg : arguments) {
                if (!isValidProductType(arg)) {
                    queryCache.put(key, false);
                    return false;
                }
            }

            Object[] params;
            Integer count;
            boolean result;

            if ("TRANSACTION_SUM_COMPARE".equals(queryName)) {
                validateTransactionSumCompareArguments(arguments);

                String productType = arguments.get(0);
                String transactionType = arguments.get(1);
                String operator = arguments.get(2);
                int comparisonValue = Integer.parseInt(arguments.get(3));

                Double sum = jdbcTemplate.queryForObject(sql, Double.class, userId, productType, transactionType);

                result = compareValues(sum != null ? sum : 0, comparisonValue, operator);
            } else if ("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW".equals(queryName)) {
                validateTransactionSumCompareDepositWithdrawArguments(arguments);

                String productType = arguments.get(0);
                String operator = arguments.get(1);

                Integer queryResult = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType, userId, productType);
                result = queryResult != null && queryResult == 1;
            } else {
                params = new Object[arguments.size() + 1];
                params[0] = userId;
                for (int i = 0; i < arguments.size(); i++) {
                    params[i + 1] = arguments.get(i);
                }

                count = jdbcTemplate.queryForObject(sql, Integer.class, params);
                result = count != null && count > 0;
            }
            
            queryCache.put(key, result);
            return result;
        } catch (Exception e) {
            queryCache.put(key, false);
            return false;
        }
    }

    private boolean isValidProductType(String type) {
        return List.of("DEBIT", "CREDIT", "INVEST", "SAVING").contains(type);
    }

    private boolean isValidTransactionType(String type) {
        return List.of("DEPOSIT", "WITHDRAW").contains(type);
    }

    private boolean isValidOperator(String operator) {
        return List.of("<", "<=" , ">", ">=", "=").contains(operator);
    }

    private void validateTransactionSumCompareArguments(List<String> arguments) {
        if (arguments.size() != 4) {
            throw new IllegalArgumentException("TRANSACTION_SUM_COMPARE requires exactly 4 arguments: productType, transactionType, operator, comparisonValue");
        }

        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        String comparisonValue = arguments.get(3);

        if (!isValidProductType(productType)) {
            throw new IllegalArgumentException("Invalid product type: " + productType);
        }

        if (!isValidTransactionType(transactionType)) {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }

        if (!isValidOperator(operator)) {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }

        try {
            Integer.parseInt(comparisonValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Comparison value must be an integer: " + comparisonValue);
        }
    }

    private void validateTransactionSumCompareDepositWithdrawArguments(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW requires exactly 2 arguments: productType, operator");
        }

        String productType = arguments.get(0);
        String operator = arguments.get(1);

        if (!isValidProductType(productType)) {
            throw new IllegalArgumentException("Invalid product type: " + productType);
        }

        if (!isValidOperator(operator)) {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private boolean compareValues(double actualValue, int expectedValue, String operator) {
        return switch (operator) {
            case ">" -> actualValue > expectedValue;
            case "<" -> actualValue < expectedValue;
            case "=" -> actualValue == expectedValue;
            case ">=" -> actualValue >= expectedValue;
            case "<=" -> actualValue <= expectedValue;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private static String getPlaceholdersForInClause(int size) {
        if (size <= 0) {
            return "";
        }
        return String.join(",", "?".repeat(size));
    }

    // StaticRulesSets support here:
    
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
