package top.afinit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.afinit.dao.BlogDao;
import top.afinit.domain.entity.Blog;

import java.util.List;

@SpringBootTest
class BlogApplicationTests {

    @Autowired
    private BlogDao blogDao;


    @Test
    void testGetAll() {
        List<Blog> blogs = blogDao.selectList(null);
        System.out.println(blogs);
    }

    @Test
    void testInsert(){
        Blog blog = new Blog();
        blog.setTitle("测试标题");
        blog.setSummary("摘要test");
        blog.setContent("大文本");
        blogDao.insert(blog);
    }



}
