package top.afinit.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class CloudflareModerationResponseDTO {
    private CloudflareResult result;
    private Boolean success;
    private List<Object> errors;

    @Data
    public static class CloudflareResult{
        private String response;
    }
}
