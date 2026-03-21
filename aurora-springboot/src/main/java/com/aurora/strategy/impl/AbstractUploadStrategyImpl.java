package com.aurora.strategy.impl;

import com.aurora.exception.BizException;
import com.aurora.strategy.UploadStrategy;
import com.aurora.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@Service
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {

    @Override
    public String uploadFile(MultipartFile file, String path) {
        try {
            String md5 = FileUtil.getMd5(file.getInputStream());
            String extName = FileUtil.getExtName(file.getOriginalFilename());
            String fileName = md5 + extName;
            if (!exists(path + fileName)) {
                upload(path, fileName, file.getInputStream());
            }
            return getFileAccessUrl(path + fileName);
        } catch (BizException e) {
            // 业务异常直接抛出，不记录堆栈
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BizException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String path) {
        try {
            upload(path, fileName, inputStream);
            return getFileAccessUrl(path + fileName);
        } catch (BizException e) {
            // 业务异常直接抛出，不记录堆栈
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BizException("文件上传失败: " + e.getMessage());
        }
    }

    public abstract Boolean exists(String filePath);

    public abstract void upload(String path, String fileName, InputStream inputStream) throws IOException;

    public abstract String getFileAccessUrl(String filePath);

}
