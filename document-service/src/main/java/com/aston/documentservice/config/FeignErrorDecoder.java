package com.aston.documentservice.config;


import com.aston.documentservice.exception.UserNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignErrorDecoder {


    @Bean
    public ErrorDecoder errorDecoder() {


        return new ErrorDecoder() {


            private final ErrorDecoder defaultDecoder =
                    new Default();


            @Override
            public Exception decode(
                    String methodKey,
                    Response response) {


                if (response.status() == 404) {


                    return new UserNotFoundException(
                            "User not found"
                    );
                }


                return defaultDecoder.decode(
                        methodKey,
                        response
                );
            }
        };
    }
}