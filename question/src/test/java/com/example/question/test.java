package com.example.question;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
public class test {
    @Test
    void test(){
        log.debug("{}", LocalDateTime.now());
    }
}
