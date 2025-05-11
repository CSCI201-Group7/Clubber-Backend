package com.group7.lib.types.Schemas.Files;

import com.group7.lib.types.Ids.FileId;

public record UploadResponse(FileId fileId) {

    public UploadResponse(FileId fileId) {
        this.fileId = fileId;
    }
}
