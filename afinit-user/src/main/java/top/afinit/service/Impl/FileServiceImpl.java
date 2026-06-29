package top.afinit.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.afinit.common.constant.FileConstants;
import top.afinit.common.exception.BusinessException;
import top.afinit.common.result.CommonResultCode;
import top.afinit.config.properties.R2Properties;
import top.afinit.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AmazonS3 s3Client;

    private final R2Properties r2Properties;

    @Override
    public String uploadAvatar(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(StrUtil.DOT) ?
                originalFilename.substring(originalFilename.lastIndexOf(StrUtil.DOT)) : FileConstants.DEFAULT_IMAGE_EXTENSION;
        String objectKey = FileConstants.AVATAR_PATH_PREFIX + UUID.randomUUID() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(r2Properties.getBucketName(), objectKey, inputStream, metadata);
            putObjectRequest.getRequestClientOptions().setReadLimit(Long.valueOf(file.getSize()).intValue() + 1024);
            s3Client.putObject(putObjectRequest);


            return r2Properties.getPublicDomain() + StrUtil.SLASH + objectKey;

        } catch (IOException e) {
            throw new BusinessException(CommonResultCode.BUSINESS_ERR);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            return;
        }

        String prefix = r2Properties.getPublicDomain() + StrUtil.SLASH;

        if (fileUrl.startsWith(prefix)) {
            String objectKey = fileUrl.substring(prefix.length());

            if (StrUtil.isNotBlank(objectKey)) {
                s3Client.deleteObject(new DeleteObjectRequest(r2Properties.getBucketName(), objectKey));
            }
        }

    }

}
