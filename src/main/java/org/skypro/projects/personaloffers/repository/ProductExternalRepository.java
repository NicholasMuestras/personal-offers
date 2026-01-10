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

    /**
     * Выполняет запрос по его имени с параметрами и userId
     */
    public boolean evaluateQuery(String queryName, UUID userId, List<String> arguments) {
        // Формируем SQL-запрос динамически, чтобы корректно поддерживать IN-клаузу
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
                sqlBuilder.append(arguments.get(1)); // оператор сравнения
                sqlBuilder.append(" w.sum THEN 1 ELSE 0 END FROM deposit_sum d, withdraw_sum w");
                break;
            default:
                throw new IllegalArgumentException("Unknown query: " + queryName);
        }

        String sql = sqlBuilder.toString();

        try {
            // Проверяем, что передан хотя бы один тип продукта
            if (arguments.isEmpty()) {
                return false;
            }

            // Проверяем, что все аргументы - допустимые типы продуктов
            for (String arg : arguments) {
                if (!isValidProductType(arg)) {
                    return false;
                }
            }

            Object[] params;
            Integer count;

            if ("TRANSACTION_SUM_COMPARE".equals(queryName)) {
                // Для TRANSACTION_SUM_COMPARE используем специальную логику
                validateTransactionSumCompareArguments(arguments);

                // Извлекаем параметры
                String productType = arguments.get(0);
                String transactionType = arguments.get(1);
                String operator = arguments.get(2);
                int comparisonValue = Integer.parseInt(arguments.get(3));

                // Выполняем запрос для получения суммы транзакций
                Double sum = jdbcTemplate.queryForObject(sql, Double.class, userId, productType, transactionType);

                // Сравниваем сумму с пороговым значением
                return compareValues(sum != null ? sum : 0, comparisonValue, operator);
            } else if ("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW".equals(queryName)) {
                // Для TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW используем специальную логику
                validateTransactionSumCompareDepositWithdrawArguments(arguments);

                // Извлекаем параметры
                String productType = arguments.get(0);
                String operator = arguments.get(1);

                // Выполняем запрос для получения результата сравнения
                Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType, userId, productType);
                return result != null && result == 1;
            } else {
                // Формируем массив параметров: userId + все типы продуктов
                params = new Object[arguments.size() + 1];
                params[0] = userId;
                for (int i = 0; i < arguments.size(); i++) {
                    params[i + 1] = arguments.get(i);
                }

                // Выполняем один запрос с IN-клаузой
                count = jdbcTemplate.queryForObject(sql, Integer.class, params);
                if (count == null || count == 0) {
                    return false;
                }

                // Все проверки пройдены
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли тип продукта допустимым
     */
    private boolean isValidProductType(String type) {
        return List.of("DEBIT", "CREDIT", "INVEST", "SAVING").contains(type);
    }

    /**
     * Проверяет, является ли тип транзакции допустимым
     */
    private boolean isValidTransactionType(String type) {
        return List.of("DEPOSIT", "WITHDRAW").contains(type);
    }

    /**
     * Проверяет, является ли оператор сравнения допустимым
     */
    private boolean isValidOperator(String operator) {
        return List.of("<", "<=", ">", ">=", "=").contains(operator);
    }

    /**
     * Проверяет корректность аргументов для TRANSACTION_SUM_COMPARE
     */
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

    /**
     * Проверяет корректность аргументов для TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW
     */
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

    // Static RulesSets support here:

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
