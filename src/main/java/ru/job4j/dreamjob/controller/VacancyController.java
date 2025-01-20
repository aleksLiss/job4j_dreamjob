package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.MemoryVacancyRepository;
import ru.job4j.dreamjob.repository.VacancyRepository;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyRepository vacancyRepository = MemoryVacancyRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyRepository.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy) {
        vacancyRepository.save(vacancy);
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        return vacancyRepository.findById(id).map(
                        vacancy -> setMessageAndAttr(model, "vacancy", vacancy))
                .orElseGet(
                        () -> setMessageAndAttr(model, "message", "Вакансия с указанным идентификатором не найдена."));
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        return !vacancyRepository.update(vacancy)
                ? setMessageAndAttr(model, "message", "Вакансия с указанным идентификатором не найдена.")
                : "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        return !vacancyRepository.deleteById(id)
                ? setMessageAndAttr(model, "message", "Вакансия с указанным идентификатором не найдена.")
                : "redirect:/vacancies";
    }

    private String setMessageAndAttr(Model model, String message, Object attr) {
        model.addAttribute(message, attr);
        return "message".equals(message)
                ? "errors/404"
                : "vacancies/one";
    }

}
