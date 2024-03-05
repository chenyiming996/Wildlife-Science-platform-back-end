package com.example.animal.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.animal.common.MyException;
import com.example.animal.constants.MqConstants;
import com.example.animal.entity.Animal;
import com.example.animal.service.IAnimalService;
import com.example.animal.utils.GiteeImgBed;
import com.example.animal.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.element.Name;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/animal/img")
@Slf4j
public class AnimalImageController {
    @Autowired
    private IAnimalService animalService;

    @PostMapping()
    public ResultVO searchByImg(MultipartFile file){
//        try {
//            // 将文件保存在服务器目录中
//            // 文件名称
//            String uuid = UUID.randomUUID().toString();
//            // 得到上传文件后缀
//            String originalName = file.getOriginalFilename();
//            String ext = "." + FilenameUtils.getExtension(originalName);
//            // 新生成的文件名称
//            String fileName = uuid + ext;
//            // 复制文件
//            File targetFile = new File("D:\\PycharmProjects\\graduation-design\\animal", fileName);
//            FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
//            HashMap<String, Object> paramMap = new HashMap<>();
//            paramMap.put("url", fileName);
//            String resultJson= HttpUtil.post("http://127.0.0.1:5000/img",paramMap);
//            JSONObject jsonObject = JSONUtil.parseObj(resultJson);
//            String name=jsonObject.getStr("name");
//            String matchValue=jsonObject.getStr("matchValue");
//            return ResultVO.success(Map.of("name",name,"matchValue",matchValue));
//        } catch (IOException e) {
//            throw new MyException(500,"上传失败");
//        }
        try {
            //上传图片
            JSONObject jsonObject = addImg(file);
            //表示操作失败
            if (jsonObject == null || jsonObject.getObj("commit") == null) {
                throw new MyException(500, "图片上传失败");
            }
            JSONObject res_content = JSONUtil.parseObj(jsonObject.getObj("content"));
            String url = res_content.getStr("download_url");
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("url", url);
            String resultJson= HttpUtil.post("http://127.0.0.1:5000/img",paramMap);
            JSONObject jsonObject1 = JSONUtil.parseObj(resultJson);
            String name=jsonObject1.getStr("name");
            String matchValue=jsonObject1.getStr("matchValue");
            return ResultVO.success(Map.of("name",name,"matchValue",matchValue));
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
        String targetDir = GiteeImgBed.SEARCH_PATH + fileName;
        //设置请求路径
        String requestUrl = String.format(GiteeImgBed.CREATE_REPOS_URL, GiteeImgBed.OWNER,
                GiteeImgBed.REPO_NAME, targetDir);


        String resultJson = HttpUtil.post(requestUrl, paramMap);
        return JSONUtil.parseObj(resultJson);
    }
}
