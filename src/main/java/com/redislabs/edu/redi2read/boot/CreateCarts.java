package com.redislabs.edu.redi2read.boot;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CartRepository;
import com.redislabs.edu.redi2read.services.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(5)
@RequiredArgsConstructor
public class CreateCarts implements CommandLineRunner {

    private final RedisTemplate<String, String> redisTemplate;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;

    @Value("${app.numberOfRatings}")
    private Integer noofCarts;

    @Override
    public void run(String... args) throws Exception {

        if (cartRepository.count() == 0) {
            Random random = new Random();

            IntStream.range(0, noofCarts).forEach(n -> {

                String userId = redisTemplate.opsForSet().randomMember(User.class.getName());

                Cart cart = Cart.builder().userId(userId).build();

                Set<Book> books = getRandomBooks(7);
                cart.setCartItems(getCartItemsForBooks(books));
                cartRepository.save(cart);

                if (random.nextBoolean()) {
                    cartService.checkout(cart.getId());
                }
            });

            log.info(">>> Created carts....");
        }

    }

    private Set<CartItem> getCartItemsForBooks(Set<Book> books) {

        Set<CartItem> cartItems = new HashSet<>();

        books.forEach(book -> {
            CartItem cartItem = CartItem.builder().isbn(book.getId())
                    .price(book.getPrice())
                    .quantity(1L)
                    .isbn(book.getId())
                    .build();

            cartItems.add(cartItem);
        });
        return cartItems;

    }

    private Set<Book> getRandomBooks(int max) {

        Random random = new Random();
        int booksCount = random.nextInt(max) + 1;

        Set<Book> books = new HashSet<>();

        IntStream.range(0, booksCount).forEach(n -> {
            String randomBookId = redisTemplate.opsForSet().randomMember(Book.class.getName());
            Book book = bookRepository.findById(randomBookId).get();
            books.add(book);
        });
        return books;
    }

}
