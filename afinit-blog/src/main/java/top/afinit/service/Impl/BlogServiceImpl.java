package top.afinit.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.afinit.common.auth.AuthHolder;
import top.afinit.common.auth.AuthUser;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.BlogResultCode;
import top.afinit.common.result.CommonResultCode;
import top.afinit.dao.BlogDao;
import top.afinit.dao.UserDao;
import top.afinit.domain.dto.BlogDTO;
import top.afinit.domain.entity.Blog;
import top.afinit.domain.entity.User;
import top.afinit.domain.vo.BlogVO;
import top.afinit.domain.vo.UserNicknameVO;
import top.afinit.service.BlogService;
import top.afinit.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {


    private final BlogDao blogDao;

    private final UserService userService;

    private final UserDao userDao;

    @Override
    public Long saveBlog(BlogDTO blogDTO) {
        Blog blog = BeanUtil.copyProperties(blogDTO, Blog.class);

        if(!AuthHolder.isAdmin()){
            blog.setStatus(0);
        }

        AuthUser authUser = AuthHolder.getUser();
        blog.setUserId(authUser.getId());

        blogDao.insert(blog);

        return blog.getId();
    }

    @Override
    public void updateById(BlogDTO blogDTO) {

        Blog blog = blogDao.selectById(blogDTO.getId());

        if(ObjectUtil.isEmpty(blog)){
            throw new BusinessException(CommonResultCode.DATA_NOT_EXIST);
        }

        AuthHolder.judgmentAuth(blog.getUserId());

        Blog newBlog = BeanUtil.copyProperties(blogDTO, Blog.class);

        if(!AuthHolder.isAdmin()){
            newBlog.setStatus(0);
        }

        blogDao.updateById(newBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        Blog blog = blogDao.selectById(id);

        if(ObjectUtil.isEmpty(blog)){
            throw new BusinessException(CommonResultCode.DATA_NOT_EXIST);
        }

        AuthHolder.judgmentAuth(blog.getUserId());

        blogDao.deleteById(id);

    }

    @Override
    public BlogVO getPublicById(Long id) {

        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getId,id)
                .eq(Blog::getStatus,1);

        return getById(wrapper);
    }

    @Override
    public IPage<BlogVO> getPublicByPage(Long page, Long size) {

        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        // 查询 Blog 类中的所有字段，但是排除数据库列名为 "content" 的字段
        wrapper.select(Blog.class, fieldInfo -> !fieldInfo.getColumn().equals("content"));
        wrapper.eq(Blog::getStatus,1);
        wrapper.orderByDesc(Blog::getCreateTime);

        IPage<Blog> blogIPage = new Page<>(page,size);

        return getByPage(blogIPage,wrapper);

    }

    @Override
    public IPage<BlogVO> getPrivateByPage(Long page, Long size) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Blog.class,fieldInfo -> !fieldInfo.getColumn().equals("content"));
        wrapper.eq(Blog::getStatus,0);
        //非管理员只能查询自己的草稿文章
        if(!AuthHolder.isAdmin()) {
            wrapper.eq(Blog::getUserId, AuthHolder.getUser().getId());
        }
        wrapper.orderByDesc(Blog::getCreateTime);

        Page<Blog> blogIPage = new Page<>(page, size);
        return getByPage(blogIPage,wrapper);
    }

    @Override
    public BlogVO getPrivateById(Long id) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getId,id)
                .eq(Blog::getStatus,0);
        //非管理员只能查询自己的草稿文章
        if(!AuthHolder.isAdmin()){
            wrapper.eq(Blog::getUserId, AuthHolder.getUser().getId());
        }

        return getById(wrapper);
    }

    @Override
    public void publicBlog(Long id) {

        Blog blog = new Blog();
        blog.setId(id);
        blog.setStatus(1);
        blogDao.updateById(blog);

    }

    @Override
    public List<Long> getAllPublicBlogIds() {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Blog::getId)
                .eq(Blog::getStatus,1);

        List<Blog> blogs = blogDao.selectList(wrapper);
        return blogs.stream().map(Blog::getId).toList();
    }


    //按id查询博客
    private BlogVO getById(LambdaQueryWrapper<Blog> wrapper){
        Blog blog = blogDao.selectOne(wrapper);

        if(ObjectUtil.isEmpty(blog)){
            throw new BusinessException(BlogResultCode.GET_ERR);
        }

        BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);


        User user = userDao.selectById(blog.getUserId());
        if (ObjectUtil.isNotEmpty(user)) {
            blogVO.setNickname(user.getNickname());
        }

        return blogVO;
    }


    //按页查询博客
    private IPage<BlogVO> getByPage(IPage<Blog> blogIPage,LambdaQueryWrapper<Blog> wrapper){

        blogDao.selectPage(blogIPage, wrapper);

        List<Blog> blogRecords = blogIPage.getRecords();

        if (CollUtil.isEmpty(blogRecords)) {
            return blogIPage.convert(blog -> BeanUtil.copyProperties(blog, BlogVO.class));
        }

        Set<Long> userIds = blogRecords.stream()
                .map(Blog::getUserId)
                .collect(Collectors.toSet());


        List<UserNicknameVO> users = userService.listByIds(userIds);

        Map<Long, String> userMap = users.stream()
                .collect(Collectors.toMap(UserNicknameVO::getId, UserNicknameVO::getNickname));

        return blogIPage.convert(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            blogVO.setNickname(userMap.getOrDefault(blog.getUserId(), "未知用户"));
            return blogVO;
        });
    }
}
