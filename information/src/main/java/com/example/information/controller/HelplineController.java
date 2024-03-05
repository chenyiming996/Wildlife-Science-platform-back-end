package com.example.information.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.information.common.MyException;
import com.example.information.dto.HelplinePage;
import com.example.information.entity.Helpline;
import com.example.information.service.IHelplineService;
import com.example.information.vo.ResultVO;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/helpline")
public class HelplineController {
    @Autowired
    private IHelplineService helplineService;


    @GetMapping("/search/{search}/list/{page}")
    @Cacheable(value = "helplinePage",key = "#search+'-'+#page")
    public ResultVO getHelplinesList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        int size=10;
        QueryWrapper<Helpline> qw=new QueryWrapper<>();
        qw.orderByDesc("create_time");
        if(!search.equals("all")){
            qw.like("province",search);
        }
        Page<Helpline> result = helplineService.page(new Page<>(Integer.valueOf(page),size),qw);
        HelplinePage helplinePage = new HelplinePage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("helplinePage",helplinePage));
    }

    @GetMapping("/all")
    @Cacheable(value = "helplinePage",key = "'all'")
    public ResultVO getAllHelplines(){
        List<Helpline> helplineList = helplineService.list();
        return ResultVO.success(Map.of("helplineList",helplineList));
    }

    @DeleteMapping ("/{hid}")
    @CacheEvict(value = "helplinePage",allEntries = true)
    public ResultVO deleteHelpline(@PathVariable("hid") String hid){
        Long id=Long.valueOf(hid);
        helplineService.removeById(id);
        return ResultVO.success(Map.of());
    }

    @PutMapping("/{hid}")
    @CacheEvict(value = "helplinePage",allEntries = true)
    public ResultVO updateHelpline(@PathVariable("hid") String hid,@Valid @RequestBody Helpline helpline){
        helpline.setId(Long.valueOf(hid));
        helpline.setProvince(helpline.getProvince().trim());
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//大写
        try {
            String[] indexes = PinyinHelper.toHanyuPinyinStringArray(helpline.getProvince().charAt(0), format);
            helpline.setFirstIndex(indexes[0].substring(0,1));
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new MyException(500,"系统添加索引失败");
        }
        helplineService.updateById(helpline);
        return ResultVO.success(Map.of());
    }

    @PostMapping()
    @CacheEvict(value = "helplinePage",allEntries = true)
    public ResultVO addHelpline(@Valid @RequestBody Helpline helpline){
        helpline.setProvince(helpline.getProvince().trim());
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//大写
        try {
            String[] indexes = PinyinHelper.toHanyuPinyinStringArray(helpline.getProvince().charAt(0), format);
            helpline.setFirstIndex(indexes[0].substring(0,1));
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new MyException(500,"系统添加索引失败");
        }
        helplineService.save(helpline);
        return ResultVO.success(Map.of());
    }

}
