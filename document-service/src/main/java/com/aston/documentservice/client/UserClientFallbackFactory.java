package com.aston.documentservice.client;


import com.aston.documentservice.dto.UserResponse;
import com.aston.documentservice.exception.UserNotFoundException;
import com.aston.documentservice.exception.UserServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {


    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public UserResponse getUser(Long id) {
                if (cause instanceof UserNotFoundException) {
                    throw new UserNotFoundException(
                            "User not found"
                    );
                }
                throw new UserServiceUnavailableException(
                        "User service is temporarily unavailable"
                );
            }
        };
    }

}