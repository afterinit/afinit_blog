package top.afinit.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import top.afinit.domain.dto.BlogDTO;
import top.afinit.domain.vo.BlogVO;

import java.util.List;


public interface BlogService {
    /**
     * 插入
     * @param blogDTO 博客内容
     * @return 该博客的id
     */
    Long saveBlog(BlogDTO blogDTO);

    /**
     * 修改
     * @param blogDTO 博客内容
     */
    void updateById(BlogDTO blogDTO);

    /**
     * 按id删除
     * @param id 博客id
     */
    void deleteBlog(Long id);

    /**
     * 按id查找
     * @param id 博客id
     * @return BlogVO内容视图
     */
    BlogVO getPublicById(Long id);

    /**
     * 分页查询
     * @param page 第几页
     * @param size 数量
     * @return IPage<BlogVO>
     */
    IPage<BlogVO> getPublicByPage(Long page, Long size);


    /**
     *
     * @param page 第几页以及数量
     * @param size 数量
     * @return IPage<BlogVO>
     */
    IPage<BlogVO> getPrivateByPage(Long page, Long size);

    /**
     * 按id查找
     * @param id 博客id
     * @return BlogVO内容视图
     */
    BlogVO getPrivateById(Long id);


    /**
     * 公开博客
     * @param id 博客id
     */
    void publicBlog(Long id);

    List<Long> getAllPublicBlogIds();
}
