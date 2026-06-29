package top.afinit.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import top.afinit.common.constant.HttpConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.config.properties.AiPromptProperties;
import top.afinit.config.properties.CloudflareModerProperties;
import top.afinit.domain.dto.CloudflareModerationResponseDTO;
import top.afinit.service.CloudflareModerationService;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CloudflareModerationServiceImpl implements CloudflareModerationService {

    private final CloudflareModerProperties cloudflareModerProperties;
    private final AiPromptProperties aiPromptProperties;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(cloudflareModerProperties.getUrl())
                .defaultHeader(HttpConstants.AUTH_HEADER, HttpConstants.Param.BEARER + cloudflareModerProperties.getApiToken())
                .defaultHeader(HttpConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public boolean checkText(String text){
        if (text == null || text.trim().isEmpty()) {
            return true;
        }


        //组装待检测用户文本
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put(HttpConstants.MessageParam.KEY_ROLE,
                HttpConstants.MessageParam.ROLE_USER);
        userMessage.put(HttpConstants.MessageParam.KEY_CONTENT,
                aiPromptProperties.getSafetyAuditPrompt()+text);


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(HttpConstants.MessageParam.KEY_MESSAGES,
                Collections.singletonList(userMessage));

        CloudflareModerationResponseDTO response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(CloudflareModerationResponseDTO.class)
                .block();

        if(ObjectUtil.isEmpty(response) || !Boolean.TRUE.equals(response.getSuccess())){
            throw new BusinessException(CommonResultCode.NETWORK_ERR);
        }

        String resultResponse = response.getResult().getResponse();
        if(StrUtil.isEmpty(resultResponse)){
            return false;
        }

        return !resultResponse.contains(HttpConstants.ResultParam.SIGNAL_UNSAFE);


    }

}
