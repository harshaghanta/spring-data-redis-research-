package com.redislabs.edu.redi2read.services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CartRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private JReJSON redisJson = new JReJSON();
    private Path cartItemsPath = Path.of(".cartItems");

    public Cart get(String id) {
        return cartRepository.findById(id).get();
    }

    public void addToCart(String id, CartItem cartItem) {
        
        Optional<Book> optionalBook = bookRepository.findById(cartItem.getIsbn());
        if(optionalBook.isPresent()) {
            cartItem.setPrice(optionalBook.get().getPrice());
            String cartKey = CartRepository.getKey(id);
            redisJson.arrAppend(cartKey, cartItemsPath, cartItem);
        }
    }

    public void removeFromCart(String id, String isbn) {
        Optional<Cart> optionalCart = cartRepository.findById(id);
        if(optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            String cartKey = CartRepository.getKey(id);
            ArrayList<CartItem> cartItems = new ArrayList<CartItem>(cart.getCartItems());
            OptionalLong cartItemIndex = LongStream.range(0, cartItems.size()).filter(i -> cartItems.get((int) i ).getIsbn().equals(isbn)).findFirst();
            if(cartItemIndex.isPresent()) {
                redisJson.arrPop(cartKey, CartItem.class, cartItemsPath, cartItemIndex.getAsLong());
            }
        }
    }

    public void checkout(String id) {

        Cart cart = cartRepository.findById(id).get();
        User user = userRepository.findById(cart.getUserId()).get();

        cart.getCartItems().forEach(cartitem -> {
            Book book = bookRepository.findById(cartitem.getIsbn()).get();
            user.addBook(book);
            userRepository.save(user);
        });
    }
    
}
