package com.edwin.cachedemo.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {

        com.edwin.cachedemo.test.User user = new User(1L, "1111111", "111111");

        ObjectMapper objectMapper = new ObjectMapper();

        String s = objectMapper.writeValueAsString(user);
        System.out.println(s);
        com.edwin.cachedemo.domain.User user1 = objectMapper.readValue(s, com.edwin.cachedemo.domain.User.class);
        System.out.println(user1);


        String s1 = objectMapper.writeValueAsString(user1);
        System.out.println(s1);
        com.edwin.cachedemo.test.User user2 = objectMapper.readValue(s1, com.edwin.cachedemo.test.User.class);
        System.out.println(user2);


    }
}
