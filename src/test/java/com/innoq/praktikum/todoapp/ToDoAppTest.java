package com.innoq.praktikum.todoapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoAppTest {

    @Test
    void readUserFromCookie() {
        var user = ToDoApp.readUserFromCookie("cookie1=123; user=torsten; cookie3=456;");

        assertEquals("torsten", user);
    }
}
