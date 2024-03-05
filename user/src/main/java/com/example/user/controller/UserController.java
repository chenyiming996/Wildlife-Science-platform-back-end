package com.example.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.user.dto.QuestionList;
import com.example.user.dto.UserData;
import com.example.user.entity.User;
import com.example.user.service.IUserService;
import com.example.user.vo.ResultVO;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/service")
@Slf4j
public class UserController {
    @Autowired
    private RestTemplate template;

    @Autowired
    private IUserService userService;

    @Value("${question}")
    private String questionUrl;

    @GetMapping("/question/random")
    public ResultVO getRandomQuestion(){
        ResultVO resultVO = template.getForObject(questionUrl +"/random", ResultVO.class);
        return resultVO;
    }

    @GetMapping("/rank")
    public ResultVO getRank(){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.orderByDesc("integral");
        qw.last("limit 20");
        List<User> userList = userService.list(qw);
        return ResultVO.success(Map.of("rank",userList));
    }

    @PostMapping("/question/user/{uid}/challenge")
    public ResultVO getChallengeQuestion(
            @PathVariable("uid") String uid,
            @RequestBody UserData userData
    ){
        ResultVO resultVO=template.postForObject(questionUrl+"/user/"+uid+"/challenge",userData,ResultVO.class);
        return resultVO;
    }

    @PostMapping("/question/user/{uid}/deal")
    public ResultVO dealQuestion(
            @PathVariable("uid") String uid,
            @RequestBody QuestionList questionList
    ){
        ResultVO resultVO=template.postForObject(questionUrl+"/user/"+uid+"/deal",questionList,ResultVO.class);
        return resultVO;
    }

    @PostMapping("/{uid}/info")
    public ResultVO updateUserInfo(
            @PathVariable("uid") String uid,
            @RequestBody User user
    ){
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("id",Long.valueOf(uid));
        uw.set("integral",user.getIntegral());
        uw.set("games_num",user.getGamesNum());
        userService.update(uw);
        User u = userService.getById(Long.valueOf(uid));
        return ResultVO.success(Map.of("user",u));
    }
}
