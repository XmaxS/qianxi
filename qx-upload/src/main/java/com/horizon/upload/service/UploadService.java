package com.horizon.upload.service;

import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UploadService {

    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg","image/png","image/bmp");

    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType = file.getContentType();
            if (!ALLOW_TYPES.contains(contentType)){
                throw new QxException(ExceptionEnums.INVALID_FILE_TYPE);
            }
            //校验文件内容

            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null){
                throw new QxException(ExceptionEnums.INVALID_FILE_TYPE);
            }

            //准备目标路径
            File dest = new File("E:/JetBrains/IdeaProjects/qianxi/upload/",file.getOriginalFilename());
            //保存文件到本地
            file.transferTo(dest);
            //返回路径
            return "http://image.leyou.com/image/"+file.getOriginalFilename();

        } catch (IOException e) {
            //上传失败
            log.error("上传文件失败",e);
            throw new QxException(ExceptionEnums.UPLOAD_FILE_ERROR);
        }
    }
}
