package top.afinit.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.afinit.common.auth.AuthHolder;
import top.afinit.common.auth.AuthUser;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.BarrageResultCode;
import top.afinit.common.result.CommonResultCode;
import top.afinit.dao.BarrageDao;
import top.afinit.domain.dto.BarrageDTO;
import top.afinit.domain.entity.Barrage;
import top.afinit.domain.vo.BarrageVO;
import top.afinit.service.BarrageService;
import top.afinit.service.CloudflareModerationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BarrageServiceImpl implements BarrageService {
    private final BarrageDao barrageDao;
    private final CloudflareModerationService cloudflareModerationService;

    @Override
    public List<BarrageVO> getBarrageByBlogId(Long blogId) {
        LambdaQueryWrapper<Barrage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barrage::getBlogId,blogId);
        wrapper.eq(Barrage::getStatus,1);
        List<Barrage> barrages = barrageDao.selectList(wrapper);
        return BeanUtil.copyToList(barrages, BarrageVO.class);

    }

    @Override
    public void saveBarrage(BarrageDTO barrageDTO) {

        AuthUser authUser = AuthHolder.getUser();

        if(!cloudflareModerationService.checkText(barrageDTO.getContent())){
            log.warn("[发送弹幕-失败]:用户Token不存在");
            throw new BusinessException(BarrageResultCode.BARRAGE_CONTENT_ILLEGAL);
        }

        Barrage barrage = BeanUtil.copyProperties(barrageDTO, Barrage.class);
        barrage.setUserId(authUser.getId());
        barrageDao.insert(barrage);
        log.info("[发送弹幕-成功]:user_id={},blog_id={},content={},scroll_percent={}",barrage.getUserId(),authUser.getId(),barrage.getContent(),barrage.getScrollPercent());
    }

    @Override
    public void deleteBarrageById(Long id) {
        Barrage barrage = barrageDao.selectById(id);
        if(ObjectUtil.isEmpty(barrage)){
            log.warn("[删除弹幕-失败]:弹幕id不存在");
            throw new BusinessException(CommonResultCode.DATA_NOT_EXIST);
        }

        AuthHolder.judgmentAuth(barrage.getUserId());

        barrageDao.deleteById(id);
        log.info("[删除弹幕-成功]:user_id={},barrage_id={},blog_id={},content={}",barrage.getUserId(),barrage.getId(),barrage.getBlogId(),barrage.getContent());
    }

    @Override
    public void deleteBarrageByBlogId(Long blogId) {
        LambdaQueryWrapper<Barrage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barrage::getBlogId,blogId);
        barrageDao.delete(wrapper);
        log.info("[删除关联弹幕-成功]:blog_id={}",blogId);
    }
}
