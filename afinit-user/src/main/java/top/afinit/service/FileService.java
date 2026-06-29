package top.afinit.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadAvatar(MultipartFile file);
    void deleteFile(String fileUrl);
}
