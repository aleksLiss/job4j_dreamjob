package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private final MemoryCandidateRepository candidateRepository = MemoryCandidateRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate) {
        candidateRepository.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        return candidateRepository.findById(id).map(
                candidate -> setMessageAndAttr(model, "candidate", candidate)
        ).orElseGet(() -> setMessageAndAttr(model, "message", "Кандидат с указанным идентификатором не найден."));
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        return !candidateRepository.update(candidate)
                ? setMessageAndAttr(model, "message", "Кандидат с указанным идентификатором не найден.")
                : "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(Model model, @PathVariable int id) {
        return !candidateRepository.deleteById(id)
                ? setMessageAndAttr(model, "message", "Кандидат с указанным идентификатором не найден.")
                : "redirect:/candidates";
    }

    private String setMessageAndAttr(Model model, String message, Object attr) {
        model.addAttribute(message, attr);
        return "message".equals(message)
                ? "errors/404"
                : "candidates/one";
    }
}
