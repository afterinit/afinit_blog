package top.afinit.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
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
            throw new BusinessException(BarrageResultCode.BARRAGE_CONTENT_ILLEGAL);
        }

        Barrage barrage = BeanUtil.copyProperties(barrageDTO, Barrage.class);
        barrage.setUserId(authUser.getId());
        barrageDao.insert(barrage);
    }

    @Override
    public void deleteBarrage(Long id) {
        Barrage barrage = barrageDao.selectById(id);
        if(ObjectUtil.isEmpty(barrage)){
            throw new BusinessException(CommonResultCode.DATA_NOT_EXIST);
        }

        AuthHolder.judgmentAuth(barrage.getUserId());

        barrageDao.deleteById(id);
    }
}
