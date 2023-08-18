package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.ImageNameDTO;
import com.connecter.digitalguiljabiback.service.AWSS3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "S3Controller", description = "S3 관련 컨트롤러입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/s3")
public class AWSS3Controller {

    private final AWSS3Service s3Service;

    @PostMapping("/presigned")
    public ResponseEntity getS3PresignedKey(@RequestBody ImageNameDTO imageNameDTO) {
        String preSignedUrl = s3Service.getPreSignedUrl(imageNameDTO.getImage_name());

        Map<String, String> map = new HashMap<>();
        map.put("presigned_url", preSignedUrl);

        return ResponseEntity.ok(map);
    }

}

