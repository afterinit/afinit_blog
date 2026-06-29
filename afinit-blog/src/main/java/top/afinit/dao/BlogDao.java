package top.afinit.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.afinit.domain.entity.Blog;

@Mapper
public interface BlogDao extends BaseMapper<Blog> {

}
