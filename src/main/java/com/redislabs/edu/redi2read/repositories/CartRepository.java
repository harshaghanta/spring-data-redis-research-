package com.redislabs.edu.redi2read.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.modules.rejson.JReJSON;

@Repository
public class CartRepository implements CrudRepository<Cart, String> {

    private JReJSON redisJson = new JReJSON();

    private  final static String idPrefix = Cart.class.getName();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public <S extends Cart> S save(S cart) {
        if(cart.getId() == null) {
            cart.setId(UUID.randomUUID().toString());
        }

        String key = getKey(cart);
        redisJson.set(key, cart);
        redisTemplate.opsForSet().add(idPrefix, key);
        redisTemplate.opsForHash().put("carts-by-user-id-idx", cart.getUserId().toString(), cart.getId().toString());

        return cart;
    }

    private static String getKey(Cart cart) {
        return String.format("%s:%s", idPrefix, cart.getId());
    }

    @Override
    public <S extends Cart> Iterable<S> saveAll(Iterable<S> carts) {
        return StreamSupport.stream(carts.spliterator(), false)
        .map(cart -> save(cart))
        .collect(Collectors.toList());
    }

    @Override
    public Optional<Cart> findById(String id) {
        Cart cart = redisJson.get(getKey(id),Cart.class);
        return Optional.ofNullable(cart);
    }

    public static String getKey(String id) {
        return String.format("%s:%s", idPrefix, id);
    }

    @Override
    public boolean existsById(String id) {
        return redisTemplate.hasKey(getKey(id));
    }

    @Override
    public Iterable<Cart> findAll() {
        String[] keys = redisTemplate.opsForSet().members(idPrefix).stream().toArray(String[]::new);
        List<Cart> carts = redisJson.mget(Cart.class, keys);
        return carts;
    }

    @Override
    public Iterable<Cart> findAllById(Iterable<String> ids) {
        String[] keys = StreamSupport.stream(ids.spliterator(), false).map(id -> getKey(id)).toArray(String[]::new);
        List<Cart> carts = redisJson.mget(Cart.class, keys);
        return carts;
    }

    @Override
    public long count() {
        return redisTemplate.opsForSet().size(idPrefix);
    }

    @Override
    public void deleteById(String id) {
        redisJson.del(getKey(id));
        
    }

    @Override
    public void delete(Cart cart) {
        deleteById(cart.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        List<String> keys = StreamSupport.stream(ids.spliterator(), false)
            .map(id -> getKey(id))
            .collect(Collectors.toList());
        redisTemplate.opsForSet().getOperations().delete(keys);
        
    }

    @Override
    public void deleteAll(Iterable<? extends Cart> carts) {
        
        List<String> keys = StreamSupport.stream(carts.spliterator(), false)
            .map(cart -> cart.getId())
            .collect(Collectors.toList());

        redisTemplate.opsForSet().getOperations().delete(keys);
        
    }

    @Override
    public void deleteAll() {
        redisTemplate.opsForSet().getOperations().delete(redisTemplate.opsForSet().members(idPrefix));        
    }
    
}
