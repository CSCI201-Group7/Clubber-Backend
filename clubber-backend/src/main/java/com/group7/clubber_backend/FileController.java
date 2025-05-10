package com.group7.clubber_backend;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.group7.clubber_backend.Managers.FileManager;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Schemas.Files.UploadResponse;

@RestController
@RequestMapping("/files")
public class FileController {

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable String id) {
        FileId fileId = new FileId(id);
        InputStream fileStream = FileManager.getInstance().download(fileId);
        String filename = FileManager.getInstance().getFilename(fileId);
        
        // Create a resource from the input stream
        InputStreamResource resource = new InputStreamResource(fileStream);
        
        // Set up headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("filename") String filename) {
        if (file == null || filename == null) {
            return ResponseEntity.status(400).build();
        }
        try {
            FileId fileId = FileManager.getInstance().upload(filename, file.getInputStream());
            return ResponseEntity.ok(new UploadResponse(fileId));
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id) {
        FileId fileId = new FileId(id);
        try {
            FileManager.getInstance().delete(fileId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
