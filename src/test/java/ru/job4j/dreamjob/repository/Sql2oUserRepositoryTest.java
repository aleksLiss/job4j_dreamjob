package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepositoryTest.class
                .getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }

        String url = properties.getProperty("datasource.url");
        String name = properties.getProperty("datasource.name");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, name, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void deleteUser() {
        Collection<User> users = sql2oUserRepository.findAll();
        for (User user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveOneUserWithUniqueEmailThenReturnThisUser() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        User savedUser = sql2oUserRepository.save(u1).get();
        assertThat(u1).isEqualTo(savedUser);
    }

    @Test
    public void whenSaveTwoUsersWithEqualEmailThenThrownException() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        User u2 = new User(1, "vova@mail.ru", "vova", "5555");
        sql2oUserRepository.save(u1);
        assertThatThrownBy(() -> sql2oUserRepository.save(u2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Пользователь с такой почтой уже существует.");
    }

    @Test
    public void whenSaveUserAndFindByEmailAndPasswordThenReturnSavedUser() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        sql2oUserRepository.save(u1);
        User foundUser = sql2oUserRepository.findByEmailAndPassword(u1.getEmail(), u1.getPassword()).get();
        assertThat(foundUser.getEmail()).isEqualTo(u1.getEmail());
        assertThat(foundUser.getName()).isEqualTo(u1.getName());
    }

    @Test
    public void whenSaveUserAndFindOnlyByEmailThenReturnNull() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        sql2oUserRepository.save(u1);
        Optional<User> foundUser = sql2oUserRepository.findByEmailAndPassword(u1.getEmail(), "1111");
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    public void whenSaveUserAndFindOnlyByPasswordThenReturnNull() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        sql2oUserRepository.save(u1);
        Optional<User> foundUser = sql2oUserRepository.findByEmailAndPassword("kolya@mail.ru", u1.getPassword());
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    public void whenSaveTwoUsersThenReturnCollectionOfThisUsers() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        User u2 = new User(0, "kolya@mail.ru", "kolya", "4444");
        sql2oUserRepository.save(u1);
        sql2oUserRepository.save(u2);
        Collection<User> savedUsers = sql2oUserRepository.findAll();
        assertThat(savedUsers)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    public void whenDeleteSavedUserThenReturnEmptyCollection() {
        User u1 = new User(0, "vova@mail.ru", "vova", "1234");
        sql2oUserRepository.save(u1);
        Optional<User> savedUser = sql2oUserRepository.findByEmailAndPassword(u1.getEmail(), u1.getPassword());
        sql2oUserRepository.deleteById(savedUser.get().getId());
        Optional<User> foundUser = sql2oUserRepository.findByEmailAndPassword(u1.getEmail(), u1.getPassword());
        assertThat(foundUser).isEqualTo(Optional.empty());
    }
}