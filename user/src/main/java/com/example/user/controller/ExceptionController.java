package com.example.user.controller;

import com.example.user.common.MyException;
import com.example.user.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(MyException.class)
    public ResultVO handleValidException(MyException exception) {
        return ResultVO.error(exception.getCode(), exception.getMessage());
    }

}
