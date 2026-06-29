package top.afinit.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.afinit.common.result.BarrageResultCode;
import top.afinit.common.result.Result;
import top.afinit.domain.dto.BarrageDTO;
import top.afinit.domain.vo.BarrageVO;
import top.afinit.service.BarrageService;

import java.util.List;


@RestController
@RequestMapping("/barrage")
@RequiredArgsConstructor
@Validated
public class BarrageDaoController {

    private final BarrageService barrageService;

    @GetMapping("/{blogId}")
    public Result<List<BarrageVO>> getBarrageByBlogId(@PathVariable
                                                          @NotNull(message = "id不能为空")
                                                          @Min(value = 1,message = "ID格式不合法")
                                                          Long blogId){
        List<BarrageVO> barrageVOs = barrageService.getBarrageByBlogId(blogId);
        return Result.success(BarrageResultCode.BARRAGE_GET_OK,barrageVOs);
    }


    @PostMapping
    public Result<Void> saveBarrage(@RequestBody @Validated BarrageDTO barrageDTO){
        barrageService.saveBarrage(barrageDTO);
        return Result.success(BarrageResultCode.BARRAGE_SEND_OK);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBarrage(@PathVariable
                                          @NotNull(message = "id不能为空")
                                          @Min(value = 1,message = "ID格式不合法")
                                          Long id){
        barrageService.deleteBarrage(id);
        return Result.error(BarrageResultCode.BARRAGE_DELETE_OK);
    }

}
