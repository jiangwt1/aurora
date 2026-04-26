package com.aurora.config;

import com.aurora.service.SystemConfigProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLConnection;

@Controller
public class LocalFileConfig {

    @Autowired
    private SystemConfigProviderService configProvider;

    @GetMapping("/images/**")
    public ResponseEntity<Resource> getImage(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String filePath = uri.substring("/images/".length());

        String storagePath = getStoragePath();
        File file = new File(storagePath, filePath);

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=2592000")
                .body(resource);
    }

    private String getStoragePath() {
        String customPath = configProvider.getConfig("upload.local.path", "");
        if (customPath == null || customPath.trim().isEmpty()) {
            return System.getProperty("user.dir") + File.separator + "images";
        }
        return customPath;
    }
}
