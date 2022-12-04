package com.dxc.bookstoreapi.repository;

import com.dxc.bookstoreapi.model.entity.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void findByTitle() {
        List<Book> byTitle = repository.findByTitleIgnoreCase("Ghostbusters");
        if (byTitle.size() > 0) {
            log.info("Book found: {}", byTitle);
        } else
            log.info("Book not found");
        assertThat(byTitle.size()).isGreaterThan(0);
    }

    @Test
    void findByAuthor() {
        List<Book> byAuthor = repository.findByAuthorNameIgnoreCase("John");
        if (byAuthor.size() > 0) {
            log.info("Book found: {}", byAuthor);
        } else
            log.info("Book not found");
        assertThat(byAuthor.size()).isGreaterThan(0);
    }

    @Test
    void findByTitleAndAuthorName() {
        List<Book> byTitleAndAuthor = repository.findByTitleIgnoreCaseAndAuthorNameIgnoreCase("Stock trading", "Adam");
        if (byTitleAndAuthor.size() > 0) {
            log.info("Book found: {}", byTitleAndAuthor);
        } else
            log.info("Book not found");
        assertThat(byTitleAndAuthor.size()).isGreaterThan(0);
    }

    @Test
    void checkForPreLoadedData() throws JsonProcessingException {
        List<Book> preloadedBooks = repository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println(objectMapper.writeValueAsString(preloadedBooks));
        assertThat(preloadedBooks).hasSizeGreaterThan(2);
    }
}