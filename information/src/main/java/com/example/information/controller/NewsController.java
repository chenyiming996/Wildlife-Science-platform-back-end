package com.example.information.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.information.common.MyException;
import com.example.information.dto.NewsPage;
import com.example.information.entity.Helpline;
import com.example.information.entity.News;
import com.example.information.service.INewsService;
import com.example.information.utils.GiteeImgBed;
import com.example.information.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@Slf4j
@Validated
public class NewsController {
    @Autowired
    private INewsService newsService;

    @GetMapping("/search/{search}/list/{page}")
    @Cacheable(value = "newsPage",key = "#search+'-'+#page")
    public ResultVO getNewsList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        int size=10;
        QueryWrapper<News> qw=new QueryWrapper<>();
        qw.orderByDesc("create_time");
        if(!search.equals("all")){
            qw.like("title",search);
        }
        Page<News> result = newsService.page(new Page<>(Integer.valueOf(page),size),qw);
        NewsPage newsPage = new NewsPage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("newsPage",newsPage));
    }

    @GetMapping("/latest")
    @Cacheable(value = "newsPage",key = "'latest'")
    public ResultVO getLatestNews(){
        int size=10;
        QueryWrapper<News> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        Page<News> result = newsService.page(new Page<>(1,size),qw);
        List<News> latestNews=result.getRecords();
        return ResultVO.success(Map.of("latestNews",latestNews));
    }

    @GetMapping("/tag/{tag}/page/{page}")
    @Cacheable(value = "newsPage",key = "#page+'-'+#tag")
    public ResultVO getNewsByTag(
            @PathVariable("tag") String tag,
            @PathVariable("page") String page
    ){
        int size=10;
        LambdaQueryWrapper<News> qw = new LambdaQueryWrapper<>();
        qw.eq(News::getTag,tag);
        qw.orderByDesc(News::getCreateTime);
        Page<News> result = newsService.page(new Page<>(Integer.valueOf(page),size),qw);
        NewsPage newsPage = new NewsPage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("newsPage",newsPage));
    }

    @PostMapping()
    @CacheEvict(value = "newsPage",allEntries = true)
    public ResultVO addNews(
            @NotNull(message = "标题不能为空")
            @NotEmpty(message = "标题不能为空")
            String title,
            @NotNull(message = "标签不能为空")
            @NotEmpty(message = "标签不能为空")
            String tag,
            @NotNull(message = "内容不能为空")
            @NotEmpty(message = "内容不能为空")
            String content,
            @NotNull(message = "来源不能为空")
            @NotEmpty(message = "来源不能为空")
            String origin,
            @NotNull(message = "图片不能为空")
            MultipartFile file
    ){
        try {
            //上传图片
            JSONObject jsonObject = addImg(file);
            //表示操作失败
            if (jsonObject==null || jsonObject.getObj("commit") == null) {
                throw new MyException(500,"图片上传失败");
            }
            JSONObject res_content = JSONUtil.parseObj(jsonObject.getObj("content"));
            News news = News.builder()
                    .title(title)
                    .tag(tag)
                    .content(content)
                    .origin(origin)
                    .url(res_content.getStr("download_url"))
                    .build();
            newsService.save(news);
            return ResultVO.success(Map.of());
        } catch (IOException e) {
            throw new MyException(500,"服务器内部异常");
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

    @DeleteMapping ("/{nid}")
    @CacheEvict(value = "newsPage",allEntries = true)
    public ResultVO deleteNews(@PathVariable("nid") String nid){
        Long id=Long.valueOf(nid);
        News news = newsService.getById(id);
        JSONObject jsonObject = deleteImg(news);
        if(jsonObject.getObj("commit") == null){
            throw new MyException(500,"服务器内部异常");
        }
        else {
            newsService.removeById(id);
            return ResultVO.success(Map.of());
        }
    }

    private JSONObject deleteImg(News news) {
        String path= news.getUrl().split("master/")[1];
        //设置请求路径
        String requestUrl = String.format(GiteeImgBed.GET_IMG_URL, GiteeImgBed.OWNER,
                GiteeImgBed.REPO_NAME, path);

        //获取图片所有信息
        String resultJson = HttpUtil.get(requestUrl);
        JSONObject jsonObject = JSONUtil.parseObj(resultJson);
        String sha = jsonObject.getStr("sha");

        //设置删除请求参数
        Map<String,Object> paramMap = new HashMap<>();
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

    @PutMapping("/{nid}")
    @CacheEvict(value = "newsPage",allEntries = true)
    public ResultVO updateNews(
            @PathVariable("nid") String nid,
            @NotNull(message = "标题不能为空")
            @NotEmpty(message = "标题不能为空")
            String title,
            @NotNull(message = "标签不能为空")
            @NotEmpty(message = "标签不能为空")
            String tag,
            @NotNull(message = "内容不能为空")
            @NotEmpty(message = "内容不能为空")
            String content,
            @NotNull(message = "来源不能为空")
            @NotEmpty(message = "来源不能为空")
            String origin,
            @NotNull(message = "版本不能为空")
            Integer version,
            @NotNull(message = "图片不能为空")
            MultipartFile file
    ){

        Long id = Long.valueOf(nid);
        News news = newsService.getById(id);
        try {
            deleteImg(news);
            JSONObject jsonObject = addImg(file);
            if(jsonObject==null || jsonObject.getObj("commit") == null){
                throw new MyException(500,"服务器内部异常");
            }
            else {
                JSONObject res_content = JSONUtil.parseObj(jsonObject.getObj("content"));
                News n = News.builder()
                        .id(id)
                        .title(title)
                        .tag(tag)
                        .content(content)
                        .origin(origin)
                        .url(res_content.getStr("download_url"))
                        .version(version)
                        .build();
                newsService.updateById(n);
                return ResultVO.success(Map.of());
            }


        } catch (IOException e) {
            throw new MyException(500,"更新图片失败");
        }




    }

//    private JSONObject updateImg(MultipartFile file, News news) throws IOException {
//        //分割路径
//        String path= news.getUrl().split("master/")[1];
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


}
