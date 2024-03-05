package com.example.question.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.question.common.MyException;
import com.example.question.dto.*;
import com.example.question.entity.Practice;
import com.example.question.entity.Question;
import com.example.question.service.IPracticeService;
import com.example.question.service.IQuestionService;
import com.example.question.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class QuestionController {
    @Autowired
    private IQuestionService questionService;

    @Autowired
    private IPracticeService practiceService;

    @GetMapping("question/search/{search}/list/{page}")
    @Cacheable(value = "questionPage",key = "#search+'-'+#page")
    public ResultVO getQuestionList(
            @PathVariable("page") String page,
            @PathVariable("search") String search
    ){
        int size=10;
        QueryWrapper<Question> qw=new QueryWrapper<>();
        qw.orderByDesc("create_time");
        if(!search.equals("all")){
            qw.like("description",search);
        }
        Page<Question> result = questionService.page(new Page<>(Integer.valueOf(page),size),qw);
        QuestionPage questionPage = new QuestionPage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("questionPage",questionPage));
    }

    @GetMapping("question/random")
    public ResultVO getRandomQuestion(){
        int count = questionService.count();
        if(count<10){
            throw new MyException(500,"服务器题库异常");
        }
        // 随机数起始位置
        int randomCount =(int) (Math.random()*count);
        // 保证能展示10个数据
        if (randomCount > count-10) {
            randomCount = count-10;
        }
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        wrapper.last("limit "+String.valueOf(randomCount)+", 10");
        List<Question> questionList = questionService.list(wrapper);
        return ResultVO.success(Map.of("questionList",questionList));
    }

    @PostMapping("question")
    @CacheEvict(value = "questionPage",allEntries = true)
    public ResultVO addQuestion(@Valid @RequestBody Question question){
        question.setNum(0);
        question.setRightNum(0);
        question.setRate(0.5);
        questionService.save(question);
        return ResultVO.success(Map.of());

    }

    @DeleteMapping("question/{qid}")
    @CacheEvict(value = "questionPage",allEntries = true)
    public ResultVO deleteQuestion(@PathVariable("qid") String qid){
        Long id = Long.valueOf(qid);
        questionService.removeById(id);
        return ResultVO.success(Map.of());
    }

    @PutMapping("question/{qid}")
    @CacheEvict(value = "questionPage",allEntries = true)
    public ResultVO updateQuestion(@PathVariable("qid") String qid,@Valid @RequestBody Question question){

        question.setId(Long.valueOf(qid));
        if(question.getNum()<question.getRightNum()){
            throw new MyException(400,"正确次数大于总次数，请检查输入！");
        }
        else{
            if (question.getNum() >= 100) {
                DecimalFormat df = new DecimalFormat("0.000");
                String s = df.format((double) question.getRightNum() / question.getNum());
                question.setRate(Double.valueOf(s));
            }else if(question.getNum()>0&&question.getNum()<100){
                question.setRate(0.5);
            }
            else {
                question.setRate(0.5);
            }
            questionService.updateById(question);
            return ResultVO.success(Map.of());

        }

    }

    @PostMapping("question/user/{uid}/challenge")
    public ResultVO getChallengeQuestion(
            @PathVariable("uid") String uid,
            @RequestBody UserData userData
    ){
        if(userData.getNum()<=60){
            ArrayList<Question> questionChallengeList = new ArrayList<>();
            QueryWrapper<Question> qw1 = new QueryWrapper<>();
            qw1.eq("type","兽类");
            qw1.last("limit 2");
            List<Question> beastList = questionService.list(qw1);
            beastList.forEach(q->{
                questionChallengeList.add(q);
            });
            QueryWrapper<Question> qw2 = new QueryWrapper<>();
            qw2.eq("type","鸟类");
            qw2.last("limit 3");
            List<Question> birdList = questionService.list(qw2);
            birdList.forEach(q->{
                questionChallengeList.add(q);
            });
            QueryWrapper<Question> qw3 = new QueryWrapper<>();
            qw3.eq("type","爬行类");
            qw3.last("limit 2");
            List<Question> reptileList = questionService.list(qw3);
            reptileList.forEach(q->{
                questionChallengeList.add(q);
            });
            QueryWrapper<Question> qw4 = new QueryWrapper<>();
            qw4.eq("type","鱼类");
            qw4.last("limit 3");
            List<Question> fishList = questionService.list(qw4);
            fishList.forEach(q->{
                questionChallengeList.add(q);
            });
            return ResultVO.success(Map.of("questionList", questionChallengeList));
        }
        else {
            Long userId = Long.valueOf(uid);
            QueryWrapper<Practice> pqw1 = new QueryWrapper<>();
            pqw1.eq("user_id", userId);
            pqw1.orderByDesc("create_time");
            pqw1.eq("answer", true);
            pqw1.last("limit 5");
            List<Practice> practiceRightList = practiceService.list(pqw1);
            QueryWrapper<Practice> pqw2 = new QueryWrapper<>();
            pqw2.eq("user_id", userId);
            pqw2.orderByDesc("create_time");
            pqw2.eq("answer", false);
            pqw2.last("limit 5");
            List<Practice> practiceWrongList = practiceService.list(pqw2);
            ArrayList<Long> questionOutList = new ArrayList<>();
            ArrayList<Long> questionWrongList = new ArrayList<>();
            practiceRightList.forEach(p -> {
                questionOutList.add(p.getQuestionId());
            });
            practiceWrongList.forEach(p -> {
                questionOutList.add(p.getQuestionId());
                questionWrongList.add(p.getQuestionId());
            });
            QueryWrapper<Question> qqw = new QueryWrapper<>();
            qqw.notIn("id", questionOutList);
            List<Question> questionList = questionService.list(qqw);
            ArrayList<QuestionCalculate> questionCalculates = new ArrayList<>();
            questionList.forEach(q -> {
                QuestionCalculate questionCalculate = new QuestionCalculate(q);
                double matchValue = 0;
                if (q.getType().equals("兽类")) {
                    matchValue = Math.cos(
                            (userData.getBeastRate() + userData.getBeastRightRate() * q.getRate() + userData.getBeastAverageTime() * q.getAverageTime()) /
                                    (Math.sqrt(Math.pow(userData.getBeastRate(), 2) + Math.pow(userData.getBeastRightRate(), 2) + Math.pow(userData.getBeastAverageTime(), 2)) *
                                            Math.sqrt(Math.pow(1, 2) + Math.pow(q.getRate(), 2) + Math.pow(q.getAverageTime(), 2)))
                    );
                }
                if (q.getType().equals("鸟类")) {
                    matchValue = Math.cos(
                            (userData.getBirdRate() + userData.getBirdRightRate() * q.getRate() + userData.getBirdAverageTime() * q.getAverageTime()) /
                                    (Math.sqrt(Math.pow(userData.getBirdRate(), 2) + Math.pow(userData.getBirdRightRate(), 2) + Math.pow(userData.getBirdAverageTime(), 2)) *
                                            Math.sqrt(Math.pow(1, 2) + Math.pow(q.getRate(), 2) + Math.pow(q.getAverageTime(), 2)))
                    );
                }
                if (q.getType().equals("爬行类")) {
                    matchValue = Math.cos(
                            (userData.getReptileRate() + userData.getReptileRightRate() * q.getRate() + userData.getReptileAverageTime() * q.getAverageTime()) /
                                    (Math.sqrt(Math.pow(userData.getReptileRate(), 2) + Math.pow(userData.getReptileRightRate(), 2) + Math.pow(userData.getReptileAverageTime(), 2)) *
                                            Math.sqrt(Math.pow(1, 2) + Math.pow(q.getRate(), 2) + Math.pow(q.getAverageTime(), 2)))
                    );
                }
                if (q.getType().equals("鱼类")) {
                    matchValue = Math.cos(
                            (userData.getFishRate() + userData.getFishRate() * q.getRate() + userData.getFishAverageTime() * q.getAverageTime()) /
                                    (Math.sqrt(Math.pow(userData.getFishRate(), 2) + Math.pow(userData.getFishRightRate(), 2) + Math.pow(userData.getFishAverageTime(), 2)) *
                                            Math.sqrt(Math.pow(1, 2) + Math.pow(q.getRate(), 2) + Math.pow(q.getAverageTime(), 2)))
                    );
                }
                questionCalculate.setMatchValue(matchValue);
                questionCalculates.add(questionCalculate);
            });
            questionCalculates.sort(Comparator.comparing(QuestionCalculate::getMatchValue).reversed());
            List<QuestionCalculate> questionChallengeList = new ArrayList<>();
            Integer beastNum = userData.getBeastNum();
            Integer birdNum = userData.getBirdNum();
            Integer reptileNum = userData.getReptileNum();
            Integer fishNum = userData.getFishNum();
            for (QuestionCalculate q :
                    questionCalculates) {
                if (q.getType().equals("兽类")) {
                    if (beastNum > 0) {
                        questionChallengeList.add(q);
                        beastNum = beastNum - 1;
                    }
                }
                if (q.getType().equals("鸟类")) {
                    if (birdNum > 0) {
                        questionChallengeList.add(q);
                        birdNum = birdNum - 1;
                    }
                }
                if (q.getType().equals("爬行类")) {
                    if (reptileNum > 0) {
                        questionChallengeList.add(q);
                        reptileNum = reptileNum - 1;
                    }
                }
                if (q.getType().equals("鱼类")) {
                    if (fishNum > 0) {
                        questionChallengeList.add(q);
                        fishNum = fishNum - 1;
                    }
                }
            }
            int number = 10 - userData.getBeastNum() - userData.getBirdNum() - userData.getReptileNum() - userData.getFishNum();
            if (number > 0) {
                for (int i = 0; i < number; i++) {
                    Question q = questionService.getById(questionWrongList.get(i));
                    QuestionCalculate questionCalculate = new QuestionCalculate(q);
                    questionChallengeList.add(questionCalculate);
                }
            }
            return ResultVO.success(Map.of("questionList", questionChallengeList));
        }
    }

    @PostMapping("question/user/{uid}/deal")
    public ResultVO dealQuestion(
            @PathVariable("uid") String uid,
            @RequestBody QuestionList questionList
    ){
        questionList.getQuestions().forEach(questionDto -> {
            if(questionDto.getFlag()!=null){
                Practice practice=Practice.builder()
                        .userId(Long.valueOf(uid))
                        .questionId(Long.valueOf(questionDto.getId()))
                        .answer(questionDto.getFlag())
                        .type(questionDto.getType())
                        .build();
                practiceService.save(practice);
                UpdateWrapper<Question> qw=new UpdateWrapper<>();
                qw.eq("id",Long.valueOf(questionDto.getId()));
                qw.set("num",questionDto.getNum());
                qw.set("right_num",questionDto.getRightNum());
                qw.set("rate",questionDto.getRate());
                qw.set("all_time",questionDto.getAllTime());
                qw.set("average_time",questionDto.getAverageTime());
                questionService.update(qw);
            }

        });
        return ResultVO.success(Map.of());
    }


}
