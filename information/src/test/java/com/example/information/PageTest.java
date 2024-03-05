package com.example.information;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.information.dto.HelplinePage;
import com.example.information.entity.Helpline;
import com.example.information.service.IHelplineService;
import com.example.information.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Slf4j
public class PageTest {
    @Autowired
    private IHelplineService helplineService;

    @Test
    void testPage() {
        int size=10;
        Page<Helpline> result = helplineService.page(new Page<>(1, size));
        log.debug("{}",result);
    }
}
