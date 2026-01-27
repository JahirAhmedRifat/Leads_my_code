package com.leads.microcube.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class ObjectStorageClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageClient.class);
    private final WebClient client;
    private final ServiceDiscoveryClient serviceDiscoveryClient;

    public ObjectStorageClient(WebClient.Builder builder, ServiceDiscoveryClient serviceDiscoveryClient) {
        this.client = builder.build();
        this.serviceDiscoveryClient = serviceDiscoveryClient;
    }


    public Mono<FileUploadResponse> uploadObject(MultipartFile file, String bucketName) {
        String url = serviceDiscoveryClient.getObjectStorageServiceUrl() + "/candidateInfo";
        return this.client
                .post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA) // Set the content type for multipart upload
                .body(BodyInserters.fromMultipartData("file", file.getResource())) // Attach file with proper name key
                .retrieve()
                .bodyToMono(FileUploadResponse.class)  // Expecting a single response
                .onErrorResume(e -> {
                    // Log the error for debugging
                    LOGGER.error("Error uploading file: ", e);
                    // Return a fallback response if there's an error during the upload
                    return Mono.just(new FileUploadResponse("unknown", "unknown", "", LocalDateTime.now()));
                });
    }
}
