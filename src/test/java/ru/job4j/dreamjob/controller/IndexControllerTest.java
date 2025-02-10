package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.*;

class IndexControllerTest {

    @Test
    public void whenGetIndexThenReturnIndexPage() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ConcurrentModel();
        IndexController indexController = new IndexController();
        assertThat(indexController.getIndex(model, session)).isEqualTo("index");
    }
}