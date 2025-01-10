package io.github.dziodzi.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Admin", description = "Admin management API")
@RequestMapping("/admin")
public interface AdminAPI {

    @Operation(summary = "Get admin greeting")
    @GetMapping
    @ResponseBody
    String index();
}
