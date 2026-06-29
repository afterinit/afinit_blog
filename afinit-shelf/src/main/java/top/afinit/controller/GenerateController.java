package top.afinit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.afinit.model.request.ProjectRequest;
import top.afinit.service.GenerateService;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
@Validated
public class GenerateController {

    private final GenerateService generateService;

    @PostMapping
    public ResponseEntity<byte[]> generate(
            @RequestBody ProjectRequest request
    ) throws Exception {

        File zipFile = generateService.generate(request);

        byte[] bytes = Files.readAllBytes(zipFile.toPath());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + zipFile.getName()
                )
                .header(
                        HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        HttpHeaders.CONTENT_DISPOSITION
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

}
