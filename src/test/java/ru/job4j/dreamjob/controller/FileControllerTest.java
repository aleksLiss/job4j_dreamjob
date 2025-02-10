package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;
    private MultipartFile testFile;

    @BeforeEach
    public void initService() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("test.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenFileNotFoundThenReturnResponseEntityNotFound() {
        ResponseEntity<?> expectedEntity = ResponseEntity.notFound().build();
        ResponseEntity<?> resultEntity = fileController.getById(0);
        assertThat(resultEntity).isEqualTo(expectedEntity);
    }

    @Test
    public void whenFileWasFoundThenReturnResponseEntityOk() throws IOException {
        int fileId = 1;
        File file = new File(testFile.getOriginalFilename(), Arrays.toString(testFile.getBytes()));
        file.setId(fileId);
        FileDto fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ResponseEntity<?> expEnt = ResponseEntity.ok(fileDto);
        fileService.save(fileDto);
        when(fileService.getFileById(fileId)).thenReturn(Optional.of(fileDto));
        ResponseEntity<?> res = fileController.getById(fileId);
        assertThat(res.getStatusCode()).isEqualTo(expEnt.getStatusCode());
        assertThat(res).usingRecursiveComparison().isEqualTo(expEnt);
    }
}