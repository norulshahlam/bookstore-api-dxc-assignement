package com.dxc.bookstoreapi.service;

import com.dxc.bookstoreapi.exception.BookException;
import com.dxc.bookstoreapi.model.entity.Book;
import com.dxc.bookstoreapi.model.request.CreateBookRequest;
import com.dxc.bookstoreapi.model.request.UpdateBookRequest;
import com.dxc.bookstoreapi.model.response.BookResponse;
import com.dxc.bookstoreapi.repository.BookRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dxc.bookstoreapi.model.response.BookResponse.successResponse;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class BookService {

    public static final String ENTER_TITLE_OR_AUTHOR_NAME = "Enter title or author name";
    Gson gson = new Gson();

    public static final String BOOK_NOT_FOUND = "Book not found";
    public static final String BOOK_FOUND = "Book found: {}";
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public BookResponse<Book> addBook(CreateBookRequest book) {
        log.info("in BookService::addBook");

        // Deep copy
        Book book1 = gson.fromJson(gson.toJson(book), Book.class);
        Book savedBook = repository.save(book1);
        log.info("New book created: {}", savedBook);
        return successResponse(savedBook);
    }

    /**
     * Search by author and/or by author using Jpa query method
     * Source: https://evonsdesigns.medium.com/spring-jpa-one-to-many-query-examples-281078bc457b
     * @param title
     * @param author
     * @return
     */
    public BookResponse<List<Book>> getBookByTitleAndOrAuthor(String title, String author) {
        log.info("in BookService::getBookByTitleAndOrAuthor");
        if (isNotBlank(title) && isNotBlank(author)) {
            return findByTitleAndAuthor(title, author);
        } else if (isNotBlank(title) && isBlank(author)) {
            return findByTitle(title);
        } else if (isBlank(title) && isNotBlank(author)) {
            return findByAuthor(author);
        } else
            throw new BookException(ENTER_TITLE_OR_AUTHOR_NAME);
    }

    private BookResponse<List<Book>> findByTitleAndAuthor(String title, String author) {
        log.info("Finding book by title: {} and author: {}", title, author);
        List<Book> byTitleAndAuthor = repository.findByTitleIgnoreCaseAndAuthorNameIgnoreCase(title, author);
        if (!byTitleAndAuthor.isEmpty()) {
            log.info(BOOK_FOUND, byTitleAndAuthor);
            return successResponse(byTitleAndAuthor);
        }
        log.info(BOOK_NOT_FOUND);
        throw new BookException(BOOK_NOT_FOUND);
    }

    private BookResponse<List<Book>> findByTitle(String title) {
        log.info("Finding book by title: {} ", title);
        List<Book> byTitleName = repository.findByTitleIgnoreCase(title);
        if (!byTitleName.isEmpty()) {
            log.info(BOOK_FOUND, byTitleName);
            return successResponse(byTitleName);
        }
        log.info(BOOK_NOT_FOUND);
        throw new BookException(BOOK_NOT_FOUND);
    }

    private BookResponse<List<Book>> findByAuthor(String author) {
        log.info("Finding book by author: {}", author);
        List<Book> byAuthorName = repository.findByAuthorNameIgnoreCase(author);
        if (!byAuthorName.isEmpty()) {
            log.info(BOOK_FOUND, byAuthorName);
            return successResponse(byAuthorName);
        }
        log.info(BOOK_NOT_FOUND);
        throw new BookException(BOOK_NOT_FOUND);
    }

    public BookResponse<UUID> deleteBook(UUID isbn) {
        log.info("in BookService::deleteBook");
        Optional<Book> book = repository.findById(isbn);
        if (book.isEmpty())
            throw new BookException(BOOK_NOT_FOUND);
        repository.deleteById(isbn);
        return successResponse(isbn);
    }

    public BookResponse<Book> updateBook(UpdateBookRequest updateBook) {
        log.info("in BookService::updateBook");
        Optional<Book> book = repository.findById(updateBook.getIsbn());
        if (book.isEmpty())
            throw new BookException(BOOK_NOT_FOUND);
        BeanUtils.copyProperties(updateBook, book.get());
        Book savedBook = repository.save(book.get());
        return successResponse(savedBook);
    }
}
