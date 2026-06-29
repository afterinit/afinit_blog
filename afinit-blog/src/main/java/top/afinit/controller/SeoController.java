package top.afinit.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.afinit.domain.vo.BlogVO;
import top.afinit.service.BlogService;

import java.util.List;

@Controller
@RequestMapping("/seo")
@RequiredArgsConstructor
@Validated
public class SeoController {

    private final BlogService blogService;

    /**
     * 查询博客具体信息
     * @param id 博客id
     * @return HTML
     */
    @GetMapping("/{id}")
    public String getSeoPublicById(@PathVariable
                                        @NotNull(message = "id不能为空")
                                        @Min(value = 1,message = "文章ID格式不合法")
                                        Long id,
                                           Model model
                                           ){
        BlogVO blogVO = blogService.getPublicById(id);
        model.addAttribute("blog",blogVO);
        return "seo-blog";
    }


    @GetMapping(value = "/sitemap.xml",produces = "application/xml;charset=UTF-8")
    public String getSitemap(Model model){
        List<Long> allPublicBlogIds = blogService.getAllPublicBlogIds();
        model.addAttribute("ids",allPublicBlogIds);
        return "sitemap";

    }


}
