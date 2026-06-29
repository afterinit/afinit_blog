package top.afinit.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.afinit.domain.entity.User;


@Mapper
public interface UserDao extends BaseMapper<User> {
}
