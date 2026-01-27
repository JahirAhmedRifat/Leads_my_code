package com.leads.microcube.infrastructure.client;

import java.time.LocalDateTime;

public record FileUploadResponse(String bucketName, String fileName, String downloadUrl, LocalDateTime timeStamp) {}