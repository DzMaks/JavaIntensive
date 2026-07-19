package com.aston.documentservice.client;


import com.aston.documentservice.config.FeignErrorDecoder;
import com.aston.documentservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "user-service",
        configuration = FeignErrorDecoder.class,
        fallbackFactory = UserClientFallbackFactory.class
)
public interface UserClient {
    @GetMapping("/users/internal/{id}")
    UserResponse getUser(
            @PathVariable("id") Long id
    );

}