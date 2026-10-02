package top.afinit.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.afinit.common.auth.AuthHolder;
import top.afinit.common.auth.AuthUser;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.AuthResultCode;
import top.afinit.common.result.BlogResultCode;
import top.afinit.common.result.CommonResultCode;
import top.afinit.dao.BlogDao;
import top.afinit.dao.UserDao;
import top.afinit.domain.dto.BlogDTO;
import top.afinit.domain.entity.Blog;
import top.afinit.domain.entity.User;
import top.afinit.domain.vo.BlogVO;
import top.afinit.domain.vo.UserNicknameVO;
import top.afinit.service.BarrageService;
import top.afinit.service.BlogService;
import top.afinit.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {

    private final BlogDao blogDao;

    private final UserService userService;
    private final UserDao userDao;

    private final BarrageService barrageService;

    @Override
    public Long saveBlog(BlogDTO blogDTO) {
        Blog blog = BeanUtil.copyProperties(blogDTO, Blog.class);

        if(!AuthHolder.isAdmin()){
            blog.setStatus(0);
            log.info("[发布文章-文章状态改变-成功]:非管理员文章设为不可见。blog_id={},user_id={}", blog.getId(),AuthHolder.getUser().getId());
        }

        AuthUser authUser = AuthHolder.getUser();
        blog.setUserId(authUser.getId());

        blogDao.insert(blog);
        log.info("[文章发布-成功]:blog_id={},user_id={},blog_status={}", blog.getId(), blog.getUserId(), blog.getStatus());

        return blog.getId();
    }

    @Override
    public void updateById(BlogDTO blogDTO) {

        Blog blog = blogDao.selectById(blogDTO.getId());

        if(ObjectUtil.isEmpty(blog)){
            log.warn("[更新博客内容-失败]:不存在blog_id={}", blogDTO.getId());
            throw new BusinessException(CommonResultCode.DATA_NOT_EXIST);
        }

        AuthHolder.judgmentAuth(blog.getUserId());

        Blog newBlog = BeanUtil.copyProperties(blogDTO, Blog.class);

        if(!AuthHolder.isAdmin()){
            newBlog.setStatus(0);
            log.info("[修改文章-文章状态改变-成功]:非管理员设为不可见");
        }

        blogDao.updateById(newBlog);
        log.info("[文章更新-成功]:blog_id={},user_id={},blog_status={}", newBlog.getId(), newBlog.getUserId(), newBlog.getStatus());

    }

    @Override
    public void deleteBlog(Long id) {
        Blog blog = blogDao.selectById(id);

        if(ObjectUtil.isEmpty(blog)){
            log.warn("[删除文章-失败]:不存在blog_id={}",id);
            throw new BusinessException(CommonResultCode.DATA_NOT_EXIST);
        }

        AuthHolder.judgmentAuth(blog.getUserId());

        blogDao.deleteById(id);
        log.info("[删除文章-成功]:blog_id={},user_id={}", blog.getId(), blog.getUserId());
        barrageService.deleteBarrageByBlogId(id);

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
        Long userId = AuthHolder.getUser().getId();
        if(!AuthHolder.isAdmin()) {
            wrapper.eq(Blog::getUserId, userId);
        }
        wrapper.orderByDesc(Blog::getCreateTime);

        Page<Blog> blogIPage = new Page<>(page, size);
        log.info("[获取私有分页文章-成功]:user_id={}", userId);
        return getByPage(blogIPage,wrapper);
    }

    @Override
    public BlogVO getPrivateById(Long id) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getId,id)
                .eq(Blog::getStatus,0);
        //非管理员只能查询自己的草稿文章
        Long userId = AuthHolder.getUser().getId();
        if(!AuthHolder.isAdmin()){
            wrapper.eq(Blog::getUserId, userId);
        }
        log.info("[获取私有单个文章-成功]:blog_id={},user_id={}", id, userId);
        return getById(wrapper);
    }

    @Override
    public void publicBlog(Long id,Integer status) {
        Long userId = AuthHolder.getUser().getId();
        if(!AuthHolder.isAdmin()){
            log.warn("[公开文章-失败]:权限不足user_id={}", userId);
            throw new BusinessException(AuthResultCode.AUTH_PERMISSION_DENIED);
        }
        Blog blog = new Blog();
        blog.setId(id);
        blog.setStatus(status);
        blogDao.updateById(blog);
        log.info("[管理员-文章状态改变-成功]:blog_id={},user_id={},status={}", blog.getId(),userId,status);

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
            log.warn("[通过id查询文章-失败]:文章不存在");
            throw new BusinessException(BlogResultCode.GET_ERR);
        }

        BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);


        User user = userDao.selectById(blog.getUserId());
        if (ObjectUtil.isNotEmpty(user)) {
            blogVO.setNickname(user.getNickname());
        }

        log.info("[通过id查询文章-成功]:blog_id={},user_id={},blog_status={}", blog.getId(), blog.getUserId(), blog.getStatus());
        return blogVO;
    }


    //按页查询博客
    private IPage<BlogVO> getByPage(IPage<Blog> blogIPage,LambdaQueryWrapper<Blog> wrapper){

        blogDao.selectPage(blogIPage, wrapper);

        List<Blog> blogRecords = blogIPage.getRecords();

        if (CollUtil.isEmpty(blogRecords)) {
            log.warn("[按页查询文章-失败]:文章不存在");
            return blogIPage.convert(blog -> BeanUtil.copyProperties(blog, BlogVO.class));
        }

        //获取文章对应的userIds
        Set<Long> userIds = blogRecords.stream()
                .map(Blog::getUserId)
                .collect(Collectors.toSet());

        //通过userIds得到userNickname
        List<UserNicknameVO> users = userService.listByIds(userIds);

        Map<Long, String> userMap = users.stream()
                .collect(Collectors.toMap(UserNicknameVO::getId, UserNicknameVO::getNickname));

        return blogIPage.convert(blog -> {
            BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
            blogVO.setNickname(userMap.getOrDefault(blog.getUserId(), "未知用户"));
            return blogVO;
        });
    }


    @Override
    public IPage<BlogVO> getPersonalByPage(Long page, Long size){

        //获取用户id
        Long userId = AuthHolder.getUser().getId();

        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Blog.class,fieldInfo -> !fieldInfo.getColumn().equals("content"));
        wrapper.eq(Blog::getUserId, userId);
        wrapper.eq(Blog::getDeleted, 0);
        wrapper.orderByDesc(Blog::getCreateTime);

        Page<Blog> blogIPage = new Page<>(page, size);
        return getByPage(blogIPage,wrapper);
    }

}
