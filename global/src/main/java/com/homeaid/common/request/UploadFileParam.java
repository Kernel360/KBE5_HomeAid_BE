package com.homeaid.common.request;

import com.homeaid.common.enumerate.DocumentType;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileParam(
    DocumentType documentType,
    String packageName,
    MultipartFile file
) {

}
