package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpSession httpSession;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        httpSession = mock(HttpSession.class);
        httpServletRequest = mock(HttpServletRequest.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetLoginPageThenReturnLoginPage() {
        String view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenGetLogoutThenReturnLoginUserPage() {
        String view = userController.logout(httpSession);
        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenGetRegistrationPageThenReturnCreationPage() {
        String view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/create");
    }

    @Test
    public void whenDeleteRegisterUserThenOk() {
        User user = new User(1, "vova@gmail.com", "vova", "123");
        when(userService.deleteById(user.getId())).thenReturn(true);
        Model model = new ConcurrentModel();
        String view = userController.deleteById(model, user.getId());
        assertThat(view).isEqualTo("redirect:/users");
    }

    @Test
    public void whenDeleteUnregisterUserThenThrowException() {
        Exception expectedException = new RuntimeException("Пользователь с указанным идентификатором не найден.");
        when(userService.deleteById(any(Integer.class))).thenReturn(false);
        Model model = new ConcurrentModel();
        String view = userController.deleteById(model, 0);
        assertThat(view).isEqualTo("errors/404");
        var resultException = model.getAttribute("message");
        assertThat(expectedException.getMessage()).isEqualTo(resultException);

    }

    @Test
    public void whenCreateRegisteredUserThenThrowException() {
        Exception expectedException = new RuntimeException("Пользователь с такой почтой уже существует.");
        when(userService.save(any(User.class))).thenReturn(Optional.empty());
        Model model = new ConcurrentModel();
        String view = userController.create(new User(), model);
        assertThat(view).isEqualTo("errors/404");
        var resultException = model.getAttribute("message");
        assertThat(expectedException.getMessage()).isEqualTo(resultException);
    }

    @Test
    public void whenCreateUnregisterUserThenOk() {
        User user = new User(1, "email@email.com", "vova", "12345");
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));
        Model model = new ConcurrentModel();
        String view = userController.create(user, model);
        User actualUser = userArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenHasNotSavedUserThenFindAllReturnEmptyCollection() {
        List<User> expectedUsers = List.of();
        when(userService.findAll()).thenReturn(expectedUsers);
        Model model = new ConcurrentModel();
        String view = userController.getAll(model);
        var resultUsers = model.getAttribute("users");
        assertThat(view).isEqualTo("users/list");
        assertThat(resultUsers).isEqualTo(expectedUsers);
    }

    @Test
    public void whenSaveTwoUsersThenFindAllReturnAllSavedUsers() {
        User user1 = new User(1, "vova@gmail.com", "vova", "123");
        User user2 = new User(2, "nina@gmail.com", "nina", "456");
        Collection<User> expectedUsers = List.of(user1, user2);
        when(userService.findAll()).thenReturn(expectedUsers);
        Model model = new ConcurrentModel();
        String view = userController.getAll(model);
        var resultUsers = model.getAttribute("users");
        assertThat(view).isEqualTo("users/list");
        assertThat(resultUsers).isEqualTo(expectedUsers);
    }

    @Test
    public void whenLoginSavedUserThenOk() {
        User user = new User(1, "vova@gmail.com", "vova", "123");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        Model model = new ConcurrentModel();
        String view = userController.loginUser(user, model, httpServletRequest);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginUserWithUncorrectEmailThenThrowException() {
        Exception expectedException = new RuntimeException("Почта или пароль введены неверно");
        User user = new User(1, "vova@gmail.com", "vova", "123");
        when(userService.findByEmailAndPassword("nina@gmail.com", user.getPassword())).thenReturn(Optional.empty());
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        Model model = new ConcurrentModel();
        String view = userController.loginUser(user, model, httpServletRequest);
        var resultExc = model.getAttribute("error");
        assertThat(view).isEqualTo("users/login");
        assertThat(resultExc).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenLoginUserWithUncorrectPasswordThenThrowException() {
        Exception expectedException = new RuntimeException("Почта или пароль введены неверно");
        User user = new User(1, "vova@gmail.com", "vova", "123");
        when(userService.findByEmailAndPassword(user.getEmail(), "555")).thenReturn(Optional.empty());
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        Model model = new ConcurrentModel();
        String view = userController.loginUser(user, model, httpServletRequest);
        var resultExc = model.getAttribute("error");
        assertThat(view).isEqualTo("users/login");
        assertThat(resultExc).isEqualTo(expectedException.getMessage());
    }
}