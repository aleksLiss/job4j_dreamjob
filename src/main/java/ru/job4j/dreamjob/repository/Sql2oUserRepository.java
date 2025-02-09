package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO users (email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getResult();
            user.setId(generatedId);
            return Optional.ofNullable(user);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (Connection connection = sql2o.open()) {
            String sql = "SELECT * FROM users WHERE email = :email AND password = :password";
            Query query = connection.createQuery(sql)
                    .addParameter("email", email)
                    .addParameter("password", password);
            return Optional.ofNullable(query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class));
        }
    }

    @Override
    public Collection<User> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM users");
            Collection<User> users = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetch(User.class);
            return users;
        }
    }

    @Override
    public boolean deleteById(int id) {
        boolean isDeleted;
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM users WHERE id = :id")
                    .addParameter("id", id);
            query.executeUpdate();
            isDeleted = connection.getResult() != 0;
        }
        return isDeleted;
    }

    private Optional<User> isFoundUserWithEqualEmail(String email) {
        Optional<User> foundUser;
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM users WHERE email = :email")
                    .addParameter("email", email);
            foundUser = Optional.ofNullable(query.setColumnMappings(User.COLUMN_MAPPING)
                    .executeAndFetchFirst(User.class));
        }
        return foundUser;
    }
}
