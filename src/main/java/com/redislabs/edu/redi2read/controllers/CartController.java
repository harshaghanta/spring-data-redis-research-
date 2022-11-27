package com.redislabs.edu.redi2read.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.services.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/carts")
public class CartController {
    
    private final CartService cartService;

    @RequestMapping("/{id}")
    public Cart get(@PathVariable String id) {

        return cartService.get(id);
    }

    @PostMapping("/{id}")
    public void addToCart(@PathVariable String id, @RequestBody CartItem cartItem) {

         cartService.addToCart(id, cartItem);
    }

    @DeleteMapping("/{id}")
    public void removeFromCart(@PathVariable String id, @RequestBody String isbn) {
        cartService.removeFromCart(id, isbn);
    }

    @PostMapping("/{id}/checkout")
    public void checkout(String cartId) {
        cartService.checkout(cartId);
    }

}
