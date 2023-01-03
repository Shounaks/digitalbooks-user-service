package org.digitalbooks.utils;

import lombok.experimental.UtilityClass;
import org.digitalbooks.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@UtilityClass
public class ServiceUtils {
    public static Book getBookDataForBookId(Long bookId) {
        String retrieveBookUrl = "http://localhost:8081/api/v1/digitalbooks/books/" + bookId;
        ResponseEntity<Book> bookResponse = new RestTemplate().getForEntity(retrieveBookUrl, Book.class);
        return Objects.requireNonNull(bookResponse.getBody());
    }
}
