package top.afinit.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data // 自动生成所有 Getter/Setter
@NoArgsConstructor  // 自动生成无参构造
public class BlogDTO {

    private Long id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 128, message = "标题长度不能超过128个字符")
    private String title;

    /**
     * 文章摘要
     */
    @Size(max = 256, message = "摘要长度不能超过256个字符")
    private String summary;

    /**
     * Markdown纯文本正文
     */
    @NotBlank(message = "文章正文不能为空")
    @Size(max = 50000, message = "文章正文长度不能超过50000个字符")
    private String content;
}
