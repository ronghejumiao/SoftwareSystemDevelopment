package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DocumentTextExtractorImplTest {

    @Mock
    private Tika tika;

    @InjectMocks
    private DocumentTextExtractorImpl documentTextExtractor;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testExtractTextFromFileNotFound() {
        String filePath = "/nonexistent/file.txt";

        String result = documentTextExtractor.extractTextFromFile(filePath);

        assertEquals("", result);
    }

    @Test
    void testExtractTextFromFileTooLarge() throws IOException {
        String filePath = tempDir.getAbsolutePath() + "/large.txt";
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        Files.write(Paths.get(filePath), largeContent);

        String result = documentTextExtractor.extractTextFromFile(filePath);

        assertEquals("", result);
    }

    @Test
    void testExtractTextFromFilesMultiple() throws IOException, TikaException {
        String file1 = tempDir.getAbsolutePath() + "/file1.txt";
        String file2 = tempDir.getAbsolutePath() + "/file2.txt";
        Files.write(Paths.get(file1), "Content 1".getBytes());
        Files.write(Paths.get(file2), "Content 2".getBytes());

        when(tika.parseToString(any(File.class)))
                .thenReturn("Content 1")
                .thenReturn("Content 2");

        String result = documentTextExtractor.extractTextFromFiles(new String[]{file1, file2});

        assertTrue(result.contains("Content 1"));
        assertTrue(result.contains("Content 2"));
    }



}