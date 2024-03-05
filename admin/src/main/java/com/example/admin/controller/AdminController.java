package com.example.admin.controller;

import com.alibaba.fastjson.JSON;
import com.example.admin.common.MyException;
import com.example.admin.entity.Admin;
import com.example.admin.entity.Helpline;
import com.example.admin.entity.Question;
import com.example.admin.service.IAdminService;
import com.example.admin.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Validated
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RestTemplate template;

    @Value("${helpline}")
    private String helplineUrl;

    @Value("${news}")
    private String newsUrl;

    @Value("${animal}")
    private String animalUrl;

    @Value("${question}")
    private String questionUrl;

    @Value("${user}")
    private String userUrl;

    @PutMapping("/{aid}/password")
    public ResultVO updateSuperAdmin(
            @PathVariable("aid") String aid,
            @NotNull(message = "新密码不能为空")
            @NotEmpty(message = "新密码不能为空")
            String newPassword1,
            @NotNull(message = "确认新密码不能为空")
            @NotEmpty(message = "确认新密码不能为空")
            String newPassword2,
            @NotNull(message = "旧密码不能为空")
            @NotEmpty(message = "旧密码不能为空")
            String oldPassword
    ){
        Admin admin = adminService.getById(Long.valueOf(aid));
        if( !encoder.matches(oldPassword,admin.getPassword()) ){
            throw new MyException(400,"旧密码输入错误");
        }
        if(!newPassword1.equals(newPassword2)){
            throw new MyException(400,"两次新密码输入不一致");
        }
        admin.setPassword(encoder.encode(newPassword1));
        adminService.updateById(admin);
        return ResultVO.success(Map.of());
    }

    @GetMapping("/helpline/search/{search}/list/{page}")
    public ResultVO getHelplineList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        ResultVO resultVO = template.getForObject(helplineUrl +"/search/{search}/list/{page}", ResultVO.class,search,page);
        return resultVO;
    }

    @PostMapping("/helpline")
    public ResultVO addHelpline(@RequestBody Helpline helpline){
        ResultVO resultVO=template.postForObject(helplineUrl,helpline,ResultVO.class);
        return resultVO;
    }

    @PutMapping("/helpline/{hid}")
    public ResultVO updateHelpline(@PathVariable("hid") String hid,@RequestBody Helpline helpline){
        try {
            String url=helplineUrl+"/{hid}";
            url=url.replace("{hid}",hid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(helpline,null, HttpMethod.PUT, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }

    @DeleteMapping("/helpline/{hid}")
    public ResultVO deleteHelpline(@PathVariable("hid") String hid){
        try {
            String url=helplineUrl+"/{hid}";
            url=url.replace("{hid}",hid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(null, HttpMethod.DELETE, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }

    @GetMapping("/news/search/{search}/list/{page}")
    public ResultVO getNewsList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        ResultVO resultVO = template.getForObject(newsUrl +"/search/{search}/list/{page}", ResultVO.class,search,page);
        return resultVO;
    }

    @PostMapping("/news")
    public ResultVO addNews(
            String title,
            String tag,
            String content,
            String origin,
            @NotNull(message = "资讯图片不能为空")
            MultipartFile file
    ){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("title",title);
            form.add("tag",tag);
            form.add("content",content);
            form.add("origin",origin);
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            form.add("file",resource);
            HttpEntity<MultiValueMap<String, Object>> data = new HttpEntity<>(form, headers);
            ResponseEntity<ResultVO> responseEntity = template.exchange(newsUrl, HttpMethod.POST, data, ResultVO.class);
            return responseEntity.getBody();
        } catch (IOException | RestClientException e) {
           throw new MyException(400,"添加失败，请检查图片！");
        }
    }

    @DeleteMapping("/news/{nid}")
    public ResultVO deleteNews(@PathVariable("nid") String nid){
        try {
            String url=newsUrl+"/{nid}";
            url=url.replace("{nid}",nid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(null, HttpMethod.DELETE, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }

    @PutMapping("/news/{nid}")
    public ResultVO updateNews(
            @PathVariable("nid") String nid,
            String title,
            String tag,
            String content,
            String origin,
            Integer version,
            @NotNull(message = "修改动物信息需重新上传图片")
            MultipartFile file
    ){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("title",title);
            form.add("tag",tag);
            form.add("content",content);
            form.add("origin",origin);
            form.add("version",version);
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            form.add("file",resource);
            HttpEntity<MultiValueMap<String, Object>> data = new HttpEntity<>(form, headers);
            ResponseEntity<ResultVO> responseEntity = template.exchange(newsUrl+"/"+nid, HttpMethod.PUT, data, ResultVO.class);
            return responseEntity.getBody();
        } catch (IOException e) {
            log.debug("{}",e);
            throw new MyException(400,"更新失败，请检查图片！");
        } catch (RestClientException e){
            log.debug("{}",e);
            throw new MyException(500,"服务器异常");
        }
    }

    @GetMapping("/animal/search/{search}/list/{page}")
    public ResultVO getAnimalList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        ResultVO resultVO = template.getForObject(animalUrl +"/search/{search}/list/{page}", ResultVO.class,search,page);
        return resultVO;
    }

    @PostMapping("/animal")
    public ResultVO addAnimal(
            String name,
            String type,
            String description,
            String tag,
            String area,
            @NotNull(message = "动物图片不能为空")
            MultipartFile file
    ){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("name",name);
            form.add("type",type);
            form.add("description",description);
            form.add("tag",tag);
            form.add("area",area);
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            form.add("file",resource);
            HttpEntity<MultiValueMap<String, Object>> data = new HttpEntity<>(form, headers);
            ResponseEntity<ResultVO> responseEntity = template.exchange(animalUrl, HttpMethod.POST, data, ResultVO.class);
            return responseEntity.getBody();
        } catch (IOException | RestClientException e) {
            throw new MyException(400,"添加失败，请检查图片！");
        }
    }

    @DeleteMapping("/animal/{aid}")
    public ResultVO deleteAnimal(@PathVariable("aid") String aid){
        try {
            String url=animalUrl+"/{aid}";
            url=url.replace("{aid}",aid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(null, HttpMethod.DELETE, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }

    @PutMapping("/animal/{aid}")
    public ResultVO updateAnimal(
            @PathVariable("aid") String aid,
            String name,
            String type,
            String description,
            String tag,
            String area,
            Integer version,
            @NotNull(message = "修改动物信息需重新上传图片")
            MultipartFile file
    ){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("name",name);
            form.add("type",type);
            form.add("description",description);
            form.add("tag",tag);
            form.add("area",area);
            form.add("version",version);
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            form.add("file",resource);
            HttpEntity<MultiValueMap<String, Object>> data = new HttpEntity<>(form, headers);
            ResponseEntity<ResultVO> responseEntity = template.exchange(animalUrl+"/"+aid, HttpMethod.PUT, data, ResultVO.class);
            return responseEntity.getBody();
        } catch (IOException | RestClientException e) {
            throw new MyException(400,"更新失败，请检查图片!");
        }
    }

    @GetMapping("/question/search/{search}/list/{page}")
    public ResultVO getQuestionList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        ResultVO resultVO = template.getForObject(questionUrl +"/search/{search}/list/{page}", ResultVO.class,search,page);
        return resultVO;
    }

    @PostMapping("/question")
    public ResultVO addQuestion(@RequestBody Question question){
        ResultVO resultVO=template.postForObject(questionUrl,question,ResultVO.class);
        return resultVO;
    }

    @PutMapping("/question/{qid}")
    public ResultVO updateQuestion(@PathVariable("qid") String qid,@RequestBody Question question){
        try {
            String url=questionUrl+"/{qid}";
            url=url.replace("{qid}",qid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(question,null, HttpMethod.PUT, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }

    @DeleteMapping("/question/{qid}")
    public ResultVO deleteQuestion(@PathVariable("qid") String qid){
        try {
            String url=questionUrl+"/{qid}";
            url=url.replace("{qid}",qid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(null, HttpMethod.DELETE, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }

    @GetMapping("/user/search/{search}/list/{page}")
    public ResultVO getUserList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        ResultVO resultVO = template.getForObject(userUrl +"/search/{search}/list/{page}", ResultVO.class,search,page);
        return resultVO;
    }

    @DeleteMapping("/user/{uid}")
    public ResultVO deleteUser(@PathVariable("uid") String uid){
        try {
            String url=userUrl+"/{uid}";
            url=url.replace("{uid}",uid);
            //构造请求对象
            RequestEntity<Object> requestEntity = new RequestEntity<>(null, HttpMethod.DELETE, new URI(url));
            //构造 返回类型
            ParameterizedTypeReference<ResultVO> typeReference = new ParameterizedTypeReference<ResultVO>(){};
            ResponseEntity<ResultVO> exchange = template.exchange(requestEntity, typeReference);
            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new MyException(500,"服务器请求错误");
        }
    }
}
