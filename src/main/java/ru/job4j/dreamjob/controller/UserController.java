package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

@Controller
@RequestMapping("/users")
@ThreadSafe
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/create")
    public String getRegistrationPage() {
        return "users/create";
    }

    @PostMapping
    public String register(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(Model model, @PathVariable int id) {
        boolean isDeleted = userService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Пользователь с указанным идентификатором не найден.");
            return "errors/404";
        }
        return "redirect:/users";
    }
}
