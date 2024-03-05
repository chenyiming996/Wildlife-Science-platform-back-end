package com.example.animal.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.animal.common.MyException;
import com.example.animal.constants.MqConstants;
import com.example.animal.dto.AnimalPage;
import com.example.animal.entity.Animal;
import com.example.animal.mapper.AnimalMapper;
import com.example.animal.service.IAnimalService;
import com.example.animal.utils.GiteeImgBed;
import com.example.animal.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/animal")
@Slf4j
@Validated
public class AdminAnimalController {
    @Autowired
    private IAnimalService animalService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AnimalMapper animalMapper;

    @GetMapping("/search/{search}/list/{page}")
    @Cacheable(value = "animalPage", key = "#search+'-'+#page")
    public ResultVO getAnimalList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ) {
        int size = 10;
        QueryWrapper<Animal> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        if (!search.equals("all")) {
            qw.like("name", search);
        }
        Page<Animal> result = animalService.page(new Page<>(Integer.valueOf(page), size), qw);
        AnimalPage animalPage = new AnimalPage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("animalPage", animalPage));
    }

    @PostMapping()
    @CacheEvict(value = "animalPage", allEntries = true)
    public ResultVO addAnimal(
            @NotNull(message = "动物名不能为空")
            @NotEmpty(message = "动物名不能为空")
                    String name,
            @NotNull(message = "动物类型不能为空")
            @NotEmpty(message = "动物类型不能为空")
                    String type,
            @NotNull(message = "动物描述不能为空")
            @NotEmpty(message = "动物描述不能为空")
                    String description,
            @NotNull(message = "保护级别不能为空")
            @NotEmpty(message = "保护级别不能为空")
                    String tag,
            @NotNull(message = "分布地区不能为空")
            @NotEmpty(message = "分布地区不能为空")
                    String area,
            @NotNull(message = "动物图片不能为空")
                    MultipartFile file
    ) {
        try {
            //上传图片
            JSONObject jsonObject = addImg(file);
            //表示操作失败
            if (jsonObject == null || jsonObject.getObj("commit") == null) {
                throw new MyException(500, "图片上传失败");
            }
            JSONObject res_content = JSONUtil.parseObj(jsonObject.getObj("content"));
            Animal animal = Animal.builder()
                    .name(name)
                    .type(type)
                    .description(description)
                    .area(area)
                    .tag(tag)
                    .url(res_content.getStr("download_url"))
                    .build();
            boolean save = animalService.save(animal);
            if (save) {
                rabbitTemplate.convertAndSend(MqConstants.ANIMAL_EXCHANGE, MqConstants.ANIMAL_INSERT_KEY, animal.getId());
            }
            return ResultVO.success(Map.of());
        } catch (IOException e) {
            throw new MyException(500, "服务器内部异常");
        }

    }

    private JSONObject addImg(MultipartFile file) throws IOException {
        //设置图片名字
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
        //图片转码
        String paramImgFile = Base64.encode(file.getBytes());
        //设置转存到Gitee仓库参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("access_token", GiteeImgBed.ACCESS_TOKEN);
        paramMap.put("message", GiteeImgBed.ADD_MESSAGE);
        paramMap.put("content", paramImgFile);

        //转存文件路径
        String targetDir = GiteeImgBed.PATH + fileName;
        //设置请求路径
        String requestUrl = String.format(GiteeImgBed.CREATE_REPOS_URL, GiteeImgBed.OWNER,
                GiteeImgBed.REPO_NAME, targetDir);


        String resultJson = HttpUtil.post(requestUrl, paramMap);
        return JSONUtil.parseObj(resultJson);
    }

    @DeleteMapping("/{aid}")
    @CacheEvict(value = "animalPage", allEntries = true)
    public ResultVO deleteAnimal(@PathVariable("aid") String aid) {
        Long id = Long.valueOf(aid);
        Animal animal = animalService.getById(id);
        JSONObject jsonObject = deleteImg(animal);
        if (jsonObject.getObj("commit") == null) {
            throw new MyException(500, "服务器内部异常");
        } else {
            animalService.removeById(id);
            rabbitTemplate.convertAndSend(MqConstants.ANIMAL_EXCHANGE, MqConstants.ANIMAL_DELETE_KEY, id);
            return ResultVO.success(Map.of());
        }

    }

    private JSONObject deleteImg(Animal animal) {
        String path = animal.getUrl().split("master/")[1];
        //设置请求路径
        String requestUrl = String.format(GiteeImgBed.GET_IMG_URL, GiteeImgBed.OWNER,
                GiteeImgBed.REPO_NAME, path);

        //获取图片所有信息
        String resultJson = HttpUtil.get(requestUrl);
        JSONObject jsonObject = JSONUtil.parseObj(resultJson);
        String sha = jsonObject.getStr("sha");

        //设置删除请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("access_token", GiteeImgBed.ACCESS_TOKEN);
        paramMap.put("sha", sha);
        paramMap.put("message", GiteeImgBed.DEl_MESSAGE);

        //设置删除路径
        requestUrl = String.format(GiteeImgBed.DEL_IMG_URL, GiteeImgBed.OWNER,
                GiteeImgBed.REPO_NAME, path);

        //删除文件请求路径
        resultJson = HttpRequest.delete(requestUrl).form(paramMap).execute().body();
        jsonObject = JSONUtil.parseObj(resultJson);
        return jsonObject;
    }

    @PutMapping("/{aid}")
    @CacheEvict(value = "animalPage", allEntries = true)
    public ResultVO updateAnimal(
            @PathVariable("aid") String aid,
            @NotNull(message = "动物名不能为空")
            @NotEmpty(message = "动物名不能为空")
                    String name,
            @NotNull(message = "动物类型不能为空")
            @NotEmpty(message = "动物类型不能为空")
                    String type,
            @NotNull(message = "动物描述不能为空")
            @NotEmpty(message = "动物描述不能为空")
                    String description,
            @NotNull(message = "保护级别不能为空")
            @NotEmpty(message = "保护级别不能为空")
                    String tag,
            @NotNull(message = "分布地区不能为空")
            @NotEmpty(message = "分布地区不能为空")
                    String area,
            @NotNull(message = "版本不能为空")
                    Integer version,
            @NotNull(message = "动物图片不能为空")
                    MultipartFile file
    ) {
        Long id = Long.valueOf(aid);
        Animal animal = animalService.getById(id);
        try {
            deleteImg(animal);
            JSONObject jsonObject = addImg(file);
            if (jsonObject == null || jsonObject.getObj("commit") == null) {
                throw new MyException(500, "服务器内部异常");
            } else {
                JSONObject res_content = JSONUtil.parseObj(jsonObject.getObj("content"));
                Animal a = Animal.builder()
                        .id(id)
                        .name(name)
                        .type(type)
                        .description(description)
                        .tag(tag)
                        .area(area)
                        .version(version)
                        .url(res_content.getStr("download_url"))
                        .build();

                animalService.updateById(a);
                rabbitTemplate.convertAndSend(MqConstants.ANIMAL_EXCHANGE, MqConstants.ANIMAL_INSERT_KEY, id);
                return ResultVO.success(Map.of());
            }
        } catch (IOException e) {
            throw new MyException(500, "更新图片失败");
        }
    }


//    private JSONObject updateImg(MultipartFile file, Animal animal) throws IOException {
//        //分割路径
//        String path= animal.getUrl().split("master/")[1];
//        //设置请求路径
//        String requestUrl = String.format(GiteeImgBed.GET_IMG_URL, GiteeImgBed.OWNER,
//                GiteeImgBed.REPO_NAME, path);
//
//        //获取图片所有信息
//        String resultJson = HttpUtil.get(requestUrl);
//        JSONObject jsonObject = JSONUtil.parseObj(resultJson);
//        String sha = jsonObject.getStr("sha");
//
//        //图片转码
//        String paramImgFile = Base64.encode(file.getBytes());
//
//        //设置更新请求参数
//        Map<String,Object> paramMap = new HashMap<>();
//        paramMap.put("access_token", GiteeImgBed.ACCESS_TOKEN);
//        paramMap.put("sha", sha);
//        paramMap.put("message", GiteeImgBed.UPDATE_MESSAGE);
//        paramMap.put("content", paramImgFile);
//
//        //设置更新路径
//        requestUrl = String.format(GiteeImgBed.UPDATE_IMG_URL, GiteeImgBed.OWNER,
//                GiteeImgBed.REPO_NAME, path);
//
//        //更新文件请求路径
//        resultJson = HttpRequest.put(requestUrl).form(paramMap).execute().body();
//        jsonObject = JSONUtil.parseObj(resultJson);
//        return jsonObject;
//    }
//
}
