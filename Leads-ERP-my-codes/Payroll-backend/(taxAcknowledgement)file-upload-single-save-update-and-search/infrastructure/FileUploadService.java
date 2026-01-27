package com.leads.microcube.infrastructure;

import com.leads.microcube.infrastructure.client.FileUploadResponse;
import com.leads.microcube.infrastructure.client.ObjectStorageClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import static com.leads.microcube.helper.LogGeneratorHelper.*;

import java.util.Optional;

@Component
public class FileUploadService {
    private final ObjectStorageClient _objectStorageClient;

    public FileUploadService(ObjectStorageClient objectStorageClient) {
        _objectStorageClient = objectStorageClient;
    }

    /**
     * Helper method to upload a file if present.
     *
     * @param file Multipart file to upload
     * @return Optional of uploaded file's download URL
     */
    public Optional<String> uploadFile(MultipartFile file, String bucketName) {
        if (file != null && !file.isEmpty()) {
            try {
                FileUploadResponse response = _objectStorageClient.uploadObject(file, bucketName).block();
                if (response != null && response.downloadUrl() != null && !response.downloadUrl().isEmpty()) {
                    debug(this.getClass(),"Uploaded file successfully to bucket '{}'", bucketName);
                    return Optional.of(response.downloadUrl());
                } else {
                    warn(this.getClass(),"File upload response was null or contained empty URL");
                }
            } catch (Exception e) {
                error(this.getClass(),"File upload failed for bucket '{}'", bucketName, e);
            }
        } else {
            debug(this.getClass(),"No file uploaded: file was null or empty");
        }
        return Optional.empty();
    }
}
