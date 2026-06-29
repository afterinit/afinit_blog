package top.afinit.service;

import top.afinit.domain.dto.BarrageDTO;
import top.afinit.domain.vo.BarrageVO;

import java.util.List;

public interface BarrageService {

    //通过文章id获取该文章的所有弹幕
    List<BarrageVO> getBarrageByBlogId(Long blogId);

    //存储弹幕并返回id
    void saveBarrage(BarrageDTO barrageDTO);

    //删除弹幕
    void deleteBarrage(Long id);



}
