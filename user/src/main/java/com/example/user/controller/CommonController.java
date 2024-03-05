package com.example.user.controller;

import com.example.user.common.MyException;
import com.example.user.dto.RequestParams;
import com.example.user.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Autowired
    private RestTemplate template;

    @Value("${helpline}")
    private String helplineUrl;

    @Value("${news}")
    private String newsUrl;

    @Value("${animal}")
    private String animalUrl;

    @GetMapping("/helpline")
    public ResultVO getAllHelpline(){
        ResultVO resultVO = template.getForObject(helplineUrl +"/all", ResultVO.class);
        return resultVO;
    }

    @GetMapping("/news/latest")
    public ResultVO getLatestNews(){
        ResultVO resultVO = template.getForObject(newsUrl +"/latest", ResultVO.class);
        return resultVO;
    }

    @GetMapping("/news/tag/{tag}/page/{page}")
    public ResultVO getNewsByTag(
            @PathVariable("tag") String tag,
            @PathVariable("page") String page
    ){
        ResultVO resultVO = template.getForObject(newsUrl +"/tag/{tag}/page/{page}", ResultVO.class,tag,page);
        return resultVO;
    }

    @PostMapping("/animal/list")
    public ResultVO getAnimalList(@RequestBody RequestParams requestParams){
        ResultVO resultVO=template.postForObject(animalUrl+"/list",requestParams,ResultVO.class);
        return resultVO;
    }

    @PostMapping("/animal/filters")
    public ResultVO getAnimalFilters(@RequestBody RequestParams requestParams){
        ResultVO resultVO=template.postForObject(animalUrl+"/filters",requestParams,ResultVO.class);
        return resultVO;
    }

    @PostMapping("/animal/img")
    public ResultVO searchByImg(
            @NotNull(message = "资讯图片不能为空")
            MultipartFile file
    ){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            form.add("file",resource);
            HttpEntity<MultiValueMap<String, Object>> data = new HttpEntity<>(form, headers);
            ResponseEntity<ResultVO> responseEntity = template.exchange("http://graduation-design-animal/api/animal/img", HttpMethod.POST, data, ResultVO.class);
            return responseEntity.getBody();
        } catch (IOException | RestClientException e) {
            e.printStackTrace();
            throw new MyException(400,"添加失败，请检查图片！");
        }
    }
}
