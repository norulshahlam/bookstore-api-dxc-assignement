package com.dxc.bookstoreapi.service;

import com.dxc.bookstoreapi.exception.BookException;
import com.dxc.bookstoreapi.model.entity.Author;
import com.dxc.bookstoreapi.model.entity.Book;
import com.dxc.bookstoreapi.model.request.CreateBookRequest;
import com.dxc.bookstoreapi.model.request.UpdateBookRequest;
import com.dxc.bookstoreapi.model.response.BookResponse;
import com.dxc.bookstoreapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static com.dxc.bookstoreapi.model.ResponseStatus.SUCCESS;
import static com.dxc.bookstoreapi.service.BookService.BOOK_NOT_FOUND;
import static com.dxc.bookstoreapi.service.BookService.ENTER_TITLE_OR_AUTHOR_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    public static final UUID ISBN = UUID.randomUUID();
    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    private CreateBookRequest createBookRequest;

    private Author author1;
    private Author author2;

    private Book book1;
    ArrayList<Book> emptyResult = new ArrayList<>();
    BookException bookException = null;
    List<Book> foundBook = null;

    @BeforeEach
    void setUp() {
        author1 = Author.builder()
                .name("John")
                .birthday(LocalDate.of(1985, 8, 30))
                .build();
        author2 = Author.builder()
                .name("Bob")
                .birthday(LocalDate.of(1970, 8, 30))
                .build();
        book1 = Book.builder()
                .author(Arrays.asList(author1, author2))
                .title("Ghostbusters")
                .genre("Horror")
                .year(Year.of(2000))
                .price(BigDecimal.valueOf(25.50))
                .build();
        foundBook = Collections.singletonList(book1);
        createBookRequest = CreateBookRequest.builder()
                .author(Arrays.asList(author1, author2))
                .title("Ghostbusters")
                .genre("Horror")
                .year(Year.of(2000))
                .price(BigDecimal.valueOf(25.50))
                .build();


    }

    @Test
    void addBook() {
        when(repository.save(any())).thenReturn(book1);
        BookResponse<Book> addBookResponse = service.addBook(createBookRequest);
        Book data = addBookResponse.getData();

        assertThat(data.getAuthor()).isNotInstanceOf(Author.class);
        assertThat(addBookResponse.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void getBookByTitleAndOrAuthor() {

        when(repository.findByTitleIgnoreCaseAndAuthorNameIgnoreCase(any(), any())).thenReturn(foundBook);
        when(repository.findByTitleIgnoreCase(any())).thenReturn(foundBook);
        when(repository.findByAuthorNameIgnoreCase(any())).thenReturn(foundBook);

        // Search by title and author - found
        BookResponse<List<Book>> bookResponse = service.getBookByTitleAndOrAuthor("Ghostbusters", "bob");
        assertThat(bookResponse.getStatus()).isEqualTo(SUCCESS);
        assertThat(bookResponse.getData()).isNotEmpty();

        // Search by title - found
        bookResponse = service.getBookByTitleAndOrAuthor("Ghostbusters", "");
        assertThat(bookResponse.getStatus()).isEqualTo(SUCCESS);
        assertThat(bookResponse.getData()).isNotEmpty();

        // Search by author - found
        bookResponse = service.getBookByTitleAndOrAuthor("", "bob");
        assertThat(bookResponse.getStatus()).isEqualTo(SUCCESS);
        assertThat(bookResponse.getData()).isNotEmpty();


        when(repository.findByTitleIgnoreCaseAndAuthorNameIgnoreCase(any(), any())).thenReturn(emptyResult);
        when(repository.findByTitleIgnoreCase(any())).thenReturn(emptyResult);
        when(repository.findByAuthorNameIgnoreCase(any())).thenReturn(emptyResult);

        // Search by title and author - NOT found
        bookException = assertThrows(BookException.class, () -> service.getBookByTitleAndOrAuthor("Ghostbusters", "bob"));
        assertThat(bookException.getErrorMessage()).isEqualTo(BOOK_NOT_FOUND);

        // Search by title - NOT found
        bookException = assertThrows(BookException.class, () -> service.getBookByTitleAndOrAuthor("Ghostbusters", ""));
        assertThat(bookException.getErrorMessage()).isEqualTo(BOOK_NOT_FOUND);

        // Search by author - NOT found
        bookException = assertThrows(BookException.class, () -> service.getBookByTitleAndOrAuthor("", "bob"));
        assertThat(bookException.getErrorMessage()).isEqualTo(BOOK_NOT_FOUND);

        // Search - title and author not entered
        bookException = assertThrows(BookException.class, () -> service.getBookByTitleAndOrAuthor("", ""));
        assertThat(bookException.getErrorMessage()).isEqualTo(ENTER_TITLE_OR_AUTHOR_NAME);
    }

    @Test
    void deleteBook() {
        // Book not found
        when(repository.findById(any())).thenReturn(Optional.empty());
        bookException = assertThrows(BookException.class, () -> service.deleteBook(ISBN));
        assertThat(bookException.getErrorMessage()).isEqualTo(BOOK_NOT_FOUND);

        // Delete success
        when(repository.findById(any())).thenReturn(Optional.of(book1));
        BookResponse<UUID> deletedBook = service.deleteBook(ISBN);
        assertThat(deletedBook.getData()).isInstanceOf(UUID.class);
        assertThat(deletedBook.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void updateBook() {
        UpdateBookRequest updateBookRequest2 = new UpdateBookRequest();

        // Book NOT found
        BeanUtils.copyProperties(book1,updateBookRequest2);
        when(repository.findById(any())).thenReturn(Optional.empty());
        bookException = assertThrows(BookException.class, () -> service.updateBook(updateBookRequest2));
        assertThat(bookException.getErrorMessage()).isEqualTo(BOOK_NOT_FOUND);

        // Book found
        when(repository.findById(any())).thenReturn(Optional.of(book1));
        when(repository.save(any())).thenReturn(book1);

        // Update title
        updateBookRequest2.setTitle("Changed title");
        BookResponse<Book> updatedBook = service.updateBook(updateBookRequest2);
        assertThat(updatedBook.getData().getTitle()).isEqualTo("Changed title");
        assertThat(updatedBook.getStatus()).isEqualTo(SUCCESS);
    }
}