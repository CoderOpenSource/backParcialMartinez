package com.example.examen2.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map upload(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    }
    public Map uploadArchivos(InputStream inputStream, String contentType) throws IOException {
        return cloudinary.uploader().uploadLarge(inputStream, ObjectUtils.asMap(
                "resource_type", "auto",
                "file", inputStream,
                "content_type", contentType
        ));
    }
    public Map uploadArchivosExcel(byte[] fileData, String contentType, String fileName) {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "format", "xlsx",
                "public_id", fileName // Usa el nombre de archivo aquí
        );

        try {
            return cloudinary.uploader().upload(fileData, params);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
