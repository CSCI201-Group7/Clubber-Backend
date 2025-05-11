package com.group7.clubber_backend.Managers;

import java.io.InputStream;

import com.group7.lib.types.Ids.FileId;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class FileManager {

    private static final FileManager instance = new FileManager();
    private final Database database;
    private final Logger logger;

    private FileManager() {
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/FileManager");
        logger.log("FileManager initialized", LogLevel.INFO);
    }

    public static FileManager getInstance() {
        return instance;
    }

    public FileId upload(String filename, InputStream stream, String contentType) {
        return new FileId(this.database.upload(filename, stream, contentType));
    }

    public InputStream download(FileId fileId) {
        return this.database.download(fileId.toString());
    }

    public String getFilename(FileId fileId) {
        return this.database.getFilename(fileId.toString());
    }

    public String getContentType(FileId fileId) {
        return this.database.getContentType(fileId.toString());
    }

    public void delete(FileId fileId) {
        this.database.deleteFile(fileId.toString());
    }
}
