package ${project.basePackage}.common.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private Long pageNum;

    private Long pageSize;

    private Long total;

    private List<T> records;

    public static <T> PageResponse<T> of(Long pageNum, Long pageSize, Long total, List<T> records) {
        PageResponse<T> response = new PageResponse<>();
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setTotal(total);
        response.setRecords(records);
        return response;
    }
}
