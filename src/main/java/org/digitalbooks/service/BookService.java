package org.digitalbooks.service;

//import jakarta.transaction.Transactional;
import org.digitalbooks.entity.Book;
import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {
    @Autowired
    UserRepository userRepository;

    public List<Book> callBookServiceToGetAllBooks() {
        ResponseEntity<Book[]> allBooks = new RestTemplate().getForEntity("http://localhost:8081/api/v1/digitalbooks/books", Book[].class);
        return Arrays.stream(Objects.requireNonNull(allBooks.getBody())).collect(Collectors.toList());
    }

    public Long addBook(Long authorId, Book book) {
        userRepository.findById(authorId).orElseThrow(()-> new UserServiceException("Book Creation Error: AuthorID is invalid"));
        String addBookUrl = "http://localhost:8081/api/v1/digitalbooks/" + authorId + "/books";
        ResponseEntity<Long> addedBook = new RestTemplate().postForEntity(addBookUrl, book, Long.class);
        return addedBook.getBody();
    }

    public Long updateBook(Long authorId, Long bookId, Book book) {
        userRepository.findById(authorId).orElseThrow(()-> new UserServiceException("Book Creation Error: AuthorID is invalid"));
        String updateBookUrl = "http://localhost:8081/api/v1/digitalbooks/" + authorId + "/books/" + bookId;
        ResponseEntity<Long> updatedBook = new RestTemplate().exchange(updateBookUrl, HttpMethod.PUT, new HttpEntity<>(book), Long.class);
        return updatedBook.getBody();
    }

    public List<Book> searchBooksByAuthorId(Long authorId) {
        User user = userRepository.findById(authorId).orElseThrow(() -> new UserServiceException("Author Not Found"));
        if (!user.isAuthorUser()) throw new UserServiceException("User is not an author");
        String targetUrl = "http://localhost:8081/api/v1/digitalbooks/" + authorId + "/books";
        ResponseEntity<Book[]> booksByAuthor = new RestTemplate().getForEntity(targetUrl, Book[].class);
        return List.of(Objects.requireNonNull(booksByAuthor.getBody()));
    }

    public List<Book> searchUsingQuery(String category, String title, String author, String price, String publisher) {
        String targetUrl = "http://localhost:8081/api/v1/digitalbooks/search?category={category}&title={title}&price={price}&publisher={publisher}";
        HashMap<String, String> bookSearchRequestParameters = new HashMap<>();
        bookSearchRequestParameters.put("category", category);
        bookSearchRequestParameters.put("title", title);
        bookSearchRequestParameters.put("price", price);
        bookSearchRequestParameters.put("publisher", publisher);
        ResponseEntity<Book[]> allBooksWithoutAuthorSearch = new RestTemplate().getForEntity(targetUrl, Book[].class, bookSearchRequestParameters);
        //TODO: COMPLETE AUTHOR SEARCH
        //        userRepository.findByNameContainsIgnoreCaseAllIgnoreCase(author).stream().filter(User::isAuthorUser).map(User::getId);
        return Arrays.stream(Objects.requireNonNull(allBooksWithoutAuthorSearch.getBody())).collect(Collectors.toList());
    }

    public Long toggleBookBlock(Long authorId, Long bookId, boolean block) {
        Optional<User> author = userRepository.findById(authorId);
        User user = author.orElseThrow(() -> new UserServiceException("Block Error: Invalid Author ID!"));
        if (!user.isAuthorUser()) throw new UserServiceException("Block Error: User is not Author");

        //SEND REQUEST
        String targetUrl = "http://localhost:8081/api/v1/digitalbooks/" + authorId + "/books/" + bookId + "?block={block}";
        HashMap<String, Boolean> blockRequestParameter = new HashMap<>();
        blockRequestParameter.put("block", block);
        ResponseEntity<Long> toggleBookBlockResponse = new RestTemplate().getForEntity(targetUrl, Long.class, blockRequestParameter);
        return toggleBookBlockResponse.getBody();
    }
}
