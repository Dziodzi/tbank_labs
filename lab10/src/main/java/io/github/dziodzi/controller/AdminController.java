package io.github.dziodzi.controller;

import io.github.dziodzi.controller.api.AdminAPI;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController implements AdminAPI {

    @Override
    public String index() {
        return "Hello World";
    }
}
