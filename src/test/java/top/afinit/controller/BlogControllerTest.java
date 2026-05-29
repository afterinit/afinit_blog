package top.afinit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;
import top.afinit.domain.entity.Blog;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 注入 Spring Boot 自带的 Jackson 工具类，用于对象与 JSON 的相互转换
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testGetPublicById() throws Exception {
        String s = mockMvc.perform(
                MockMvcRequestBuilders.get("/blog?page=1&size=1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        System.out.println(s);

    }

    @Test
    void testInsertBlog() throws Exception {
// 1. 组装测试数据（只传递你需要的三个字段）
        Blog blog = new Blog();
        blog.setTitle("MockMvc测试文章标题");
        blog.setSummary("这是一段用于测试的摘要内容");
        blog.setContent("## 这是正文\n测试通过 MockMvc 发送纯文本或 Markdown 内容。");

        // 2. 将 Java 对象序列化为 JSON 字符串
        String blogJson = objectMapper.writeValueAsString(blog);

        // 3. 发送 POST 请求并验证
        String s = mockMvc.perform(
                MockMvcRequestBuilders.post("/blog") // 确保 Controller 类上有 @RequestMapping("/blog")
                        .contentType(MediaType.APPLICATION_JSON) // 告诉服务器我发的是 JSON
                        .content(blogJson)                       // 将 JSON 字符串放入请求体
        ).andReturn().getResponse().getContentAsString();

        System.out.println(s);
    }

}
