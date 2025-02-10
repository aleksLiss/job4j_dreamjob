package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CandidateService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateService candidateService;
    private CandidateController candidateController;
    private HttpSession httpSession;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        httpSession = mock(HttpSession.class);
        candidateController = new CandidateController(candidateService);
    }

    @Test
    public void whenDontSaveCandidatesThenGetAllEmptyCollection() {
        Collection<Candidate> expectedCandidates = List.of();
        Model model = new ConcurrentModel();
        when(candidateService.findAll()).thenReturn(expectedCandidates);
        String view = candidateController.getAll(model, httpSession);
        var resultCandidates = model.getAttribute("candidates");
        assertThat(view).isEqualTo("candidates/list");
        assertThat(resultCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenSaveTwoCandidatesThenGetAllSavedCandidates() {
        Candidate candidate1 = new Candidate(1, "vova", "Java", now());
        Candidate candidate2 = new Candidate(2, "nina", "Kotlin", now());
        Collection<Candidate> expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);
        Model model = new ConcurrentModel();
        String view = candidateController.getAll(model, httpSession);
        var actualCandidates = model.getAttribute("candidates");
        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenCreateNewCandidateThenOk() {
        Candidate candidate = new Candidate(1, "vova", "Java", now());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        when(candidateService.save(candidateArgumentCaptor.capture())).thenReturn(candidate);
        Model model = new ConcurrentModel();
        String view = candidateController.create(candidate, model);
        Candidate resCandidate = candidateArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(candidate).isEqualTo(resCandidate);
    }

    @Test
    public void whenCreateNewCandidateWasFailedThenReturnError() {
        Exception expectedException = new RuntimeException();
        when(candidateService.save(any(Candidate.class))).thenThrow(expectedException);
        Model model = new ConcurrentModel();
        String view = candidateController.create(new Candidate(), model);
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenDontSaveCandidateAndGetByIdThenThrowException() {
        Exception expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден.");
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.empty());
        Model model = new ConcurrentModel();
        String view = candidateController.getById(model, 0, httpSession);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo(expectedException.getMessage());

    }

    @Test
    public void whenSaveCandidateAndGetByIdThenReturnSavedCandidate() {
        Candidate candidate = new Candidate(1, "vova", "Java", now());
        when(candidateService.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        Model model = new ConcurrentModel();
        String view = candidateController.getById(model, candidate.getId(), httpSession);
        assertThat(view).isEqualTo("candidates/one");
        assertThat(model.getAttribute("candidate")).isEqualTo(candidate);
    }

    @Test
    public void whenUpdateSavedCandidateThenOk() {
        Candidate candidate = new Candidate(1, "vova", "Java", now());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        when(candidateService.update(candidateArgumentCaptor.capture())).thenReturn(true);
        Model model = new ConcurrentModel();
        String view = candidateController.update(candidate, model);
        Candidate actualCandidate = candidateArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
    }

    @Test
    public void whenUpdateDontSavedCandidateThenThrowException() {
        Exception expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден.");
        when(candidateService.update(any(Candidate.class))).thenReturn(false);
        Model model = new ConcurrentModel();
        String view = candidateController.update(new Candidate(), model);
        String resultException = (String) model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(resultException).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenDeleteSavedCandidateThenOk() {
        Candidate candidate = new Candidate(1, "vova", "Java", now());
        when(candidateService.deleteById(candidate.getId())).thenReturn(true);
        Model model = new ConcurrentModel();
        String view = candidateController.deleteById(model, candidate.getId());
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteDontSavedCandidateThenThrowException() {
        Exception expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден.");
        when(candidateService.deleteById(any(Integer.class))).thenReturn(false);
        Model model = new ConcurrentModel();
        String view = candidateController.deleteById(model, 0);
        String resultException = (String) model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(expectedException.getMessage()).isEqualTo(resultException);

    }
}