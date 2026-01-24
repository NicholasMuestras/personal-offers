package org.skypro.projects.personaloffers.repository;

import org.skypro.projects.personaloffers.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserExternalRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserExternalRepository(@Qualifier("secondaryJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findByUserName(String userName) {
        String sql = "SELECT id, first_name, last_name, username as user_name FROM users WHERE username = ?";
        return jdbcTemplate.query(sql, new Object[]{userName}, (rs, rowNum) -> {
            User user = new User();
            user.setId(UUID.fromString(rs.getString("id")));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setUserName(rs.getString("user_name"));

            return user;
        });
    }
}
