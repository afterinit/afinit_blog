package top.afinit.service;

import top.afinit.domain.dto.SendCodeDTO;

public interface MailService {
    void sendVerificationCode(SendCodeDTO sendCodeDTO, String textPre);

}
