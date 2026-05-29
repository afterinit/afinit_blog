package top.afinit.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.afinit.domain.dto.BlogDTO;
import top.afinit.domain.vo.BlogVO;
import top.afinit.service.BlogService;

@SpringBootTest
public class BlogServiceTests {

    @Autowired
    private BlogService blogService;


    @Test
    void testGetPublicByPage(){
        IPage<BlogVO> page = blogService.getPublicByPage(1L,2L);
        System.out.println(page.getRecords());
        System.out.println(page.getPages());
        System.out.println(page.getCurrent());
        System.out.println(page.getSize());
        System.out.println(page.getTotal());
    }


    @Test
    void testInsert(){
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setTitle("测试标题");
        blogDTO.setSummary("摘要test");
        blogDTO.setContent("大文本");
        System.out.println(blogService.save(blogDTO));
    }




    @Test
    void testDelete(){
        blogService.delete(2052691773125779457L);
    }

}
