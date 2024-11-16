package at.technikumwien.dmsbackend.service;

import java.io.InputStream;

public interface MinioService {
    void uploadFile(String fileName, InputStream inputStream, long size, String contentType) throws Exception;
    InputStream getFile(String fileName) throws Exception;
}
