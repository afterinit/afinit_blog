package top.afinit.service;

import top.afinit.model.request.ProjectRequest;

import java.io.File;

public interface GenerateService {
    File generate(ProjectRequest request)throws Exception;
}
