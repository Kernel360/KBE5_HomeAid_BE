package com.homeaid.service;

import com.homeaid.common.request.UploadFileParam;
import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerDocument;
import java.util.List;

public interface ManagerDocumentService {

  List<ManagerDocument> upload(Manager manager, List<UploadFileParam> files);

  List<ManagerDocument> update(Manager manager, List<UploadFileParam> files);
}

