package org.digitalbooks.controller;

import org.digitalbooks.entity.Book;
import org.digitalbooks.entity.BookSubscription;
import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.service.BookService;
import org.digitalbooks.service.SubscriptionService;
import org.digitalbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/digitalbooks/")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:4200/subscribe","localhost:4200"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("sign-in")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        User storedUser = userService.loginUser(user.getEmailId(), user.getPassword());
        return ResponseEntity.ok(storedUser);
    }

    @PostMapping("sign-up")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User storedUser = userService.addUser(user);
        return ResponseEntity.ok(storedUser);
    }

    @GetMapping
    public ResponseEntity<List<Book>> retrieveAllBooks() {
        return ResponseEntity.ok(bookService.callBookServiceToGetAllBooks());
    }

    @GetMapping("search")
    public ResponseEntity<List<Book>> searchQuery(@RequestParam String category, @RequestParam String title, @RequestParam String author, @RequestParam String price, @RequestParam String publisher) {
        return ResponseEntity.ok(bookService.searchUsingQuery(category, title, author, price, publisher));
    }

    //BOOK STUFF
    @GetMapping("/{authorId}/books")
    public ResponseEntity<List<Book>> searchBooksByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.searchBooksByAuthorId(authorId));
    }

    @PostMapping("/{authorId}/books")
    public ResponseEntity<Long> createBookByAuthor(@PathVariable Long authorId, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.addBook(authorId, book));
    }

    @GetMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<Long> toggleBookBlockStatus(@PathVariable Long authorId, @PathVariable Long bookId, @RequestParam boolean block) {
        return ResponseEntity.ok(bookService.toggleBookBlock(authorId, bookId, block));
    }

    @PutMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<Long> updateBook(@PathVariable Long authorId, @PathVariable Long bookId, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(authorId, bookId, book));
    }

    //Subscriber STUFF
    @GetMapping("/subscribe/{bookId}/{userId}")
    public ResponseEntity<Long> subscribeUserToBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Long sizeOfSubscription = subscriptionService.subscribeToBook(userId, bookId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @GetMapping("/unsubscribe/{bookId}/{userId}")
    public ResponseEntity<Long> unSubscribeUserToBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Long sizeOfSubscription = subscriptionService.unSubscribeToBook(userId, bookId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @GetMapping("/subscribe/{userId}")
    public ResponseEntity<List<BookSubscription>> retrieveSubscribedBookList(@PathVariable Long userId) {
        List<BookSubscription> sizeOfSubscription = subscriptionService.retrieveSubscribedBooksForUser(userId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    //Reader Stuff
    @CrossOrigin
    @GetMapping("/readers/{emailId}/books/{subscriptionId}")
    public ResponseEntity<BookSubscription> fetchSubscribedBookData(@PathVariable String emailId,@PathVariable Long subscriptionId){
        User user = userService.retrieveUserByEmail(emailId).orElseThrow(() -> new UserServiceException("Read Data Error: Email is Invalid"));
        BookSubscription subscription = subscriptionService.retrieveSubscribedBooksForUser(user.getId()).stream()
                .filter(bookSubscription -> Objects.equals(bookSubscription.getSubscription().getId(), subscriptionId))
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Read Data Error: Subscription Id is invalid"));
        return ResponseEntity.ok(subscription);
    }

    @CrossOrigin
    @GetMapping("/readers/{emailId}/books/{subscriptionId}/read")
    public ResponseEntity<String> fetchSubscribedBookContent(@PathVariable String emailId,@PathVariable Long subscriptionId){
        User user = userService.retrieveUserByEmail(emailId).orElseThrow(() -> new UserServiceException("Read Data Error: Email is Invalid"));
        BookSubscription subscription = subscriptionService.retrieveSubscribedBooksForUser(user.getId()).stream()
                .filter(bookSubscription -> Objects.equals(bookSubscription.getSubscription().getId(), subscriptionId))
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Read Data Error: Subscription Id is invalid"));
        return ResponseEntity.ok(subscription.getBook().getContent());
    }
}
