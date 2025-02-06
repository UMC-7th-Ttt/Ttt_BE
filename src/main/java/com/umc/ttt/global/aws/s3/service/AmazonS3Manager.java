package com.umc.ttt.global.aws.s3.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umc.ttt.global.aws.s3.entity.Uuid;
import com.umc.ttt.global.aws.s3.repository.UuidRepository;
import com.umc.ttt.global.config.AmazonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {
    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try{
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));

        }catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", e.getStackTrace());
        }
        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public void deleteFile(String imageUrl) {
        try{
            int index = imageUrl.indexOf(".com/");
            if(index != -1 && index+5 < imageUrl.length()){
                imageUrl = imageUrl.substring(index+5);
            }
            amazonS3.deleteObject(amazonConfig.getBucket(), imageUrl);
        }catch(SdkClientException e){
            log.error("error at AmazonS3Manager deleteFile : {}", e.getStackTrace());
        }
    }

    public String generateProfileKeyName(Uuid uuid){
        return amazonConfig.getProfilePath() + '/' + uuid.getUuid();
    }
    public String generateBookLetterKeyName(Uuid uuid){
        return amazonConfig.getBookLetterPath() + '/' + uuid.getUuid();
    }
    public String generateBookClubKeyName(Uuid uuid){
        return amazonConfig.getBookClubPath() + '/' + uuid.getUuid();
    }

}