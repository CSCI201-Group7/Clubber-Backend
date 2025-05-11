package com.group7.lib.utilities.Database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

public class Database {

    private static final Database instance = new Database();

    private final Logger logger = new Logger("Database");

    private MongoClient mongoClient;
    private MongoDatabase database;
    private GridFSBucket files;

    private Database() {
        this.logger.log("Initializing database", LogLevel.INFO);

        this.logger.log("Connecting to database", LogLevel.INFO);
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .retryWrites(true)
                .retryReads(true)
                .applyConnectionString(new ConnectionString("mongodb+srv://backend_user:Tp2cJM6pSuesjmRF@clubber.tq7l5az.mongodb.net/?retryWrites=true&w=majority&appName=Clubber"))
                .serverApi(serverApi)
                .build();

        try {
            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase(DatabaseConfig.DATABASE_NAME);
            this.files = GridFSBuckets.create(this.database, "files");
            this.database.runCommand(new Document("ping", 1));
            this.logger.log("Database connected", LogLevel.INFO);
        } catch (MongoException e) {
            this.logger.log("Error connecting to database: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to connect to database", e);
        }
        this.logger.log("Database initialized", LogLevel.INFO);
    }

    public static Database getInstance() {
        return instance;
    }

    public String insert(DatabaseCollection collection, Document document) {
        var result = this.database.getCollection(collection.getCollectionName()).insertOne(document).getInsertedId();
        if (result == null) {
            return null;
        }
        return result.asObjectId().getValue().toHexString();
    }

    public Boolean delete(DatabaseCollection collection, String id) {
        var result = this.database.getCollection(collection.getCollectionName()).deleteOne(new Document("_id", new ObjectId(id)));
        return result.getDeletedCount() > 0;
    }

    public Boolean update(DatabaseCollection collection, String id, Document document) {
        var result = this.database.getCollection(collection.getCollectionName()).updateOne(new Document("_id", new ObjectId(id)), new Document("$set", document));
        return result.getModifiedCount() > 0;
    }

    public Document get(DatabaseCollection collection, String id) {
        return this.database.getCollection(collection.getCollectionName()).find(new Document("_id", new ObjectId(id))).first();
    }

    public List<Document> list(DatabaseCollection collection, Document query) {
        return this.database.getCollection(collection.getCollectionName()).find(query).into(new ArrayList<>());
    }

    public String upload(String filename, InputStream stream, String contentType) {
        try {
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("contentType", contentType));
            ObjectId fileId = files.uploadFromStream(filename, stream, options);
            return fileId.toHexString();
        } catch (MongoException e) {
            throw new RuntimeException("Failed to upload file to GridFS", e);
        }
    }

    public InputStream download(String fileId) {
        try {
            return files.openDownloadStream(new ObjectId(fileId));
        } catch (MongoException e) {
            throw new RuntimeException("Failed to download file from GridFS", e);
        }
    }

    public String getFilename(String fileId) {
        try {
            GridFSFile fileInfo = files.find(new Document("_id", new ObjectId(fileId))).first();
            if (fileInfo != null) {
                return fileInfo.getFilename();
            }
            return null;
        } catch (MongoException e) {
            throw new RuntimeException("Failed to get filename from GridFS", e);
        }
    }

    public String getContentType(String fileId) {
        try {
            GridFSFile fileInfo = files.find(new Document("_id", new ObjectId(fileId))).first();
            if (fileInfo != null && fileInfo.getMetadata() != null) {
                return fileInfo.getMetadata().getString("contentType");
            }
            return null;
        } catch (MongoException e) {
            throw new RuntimeException("Failed to get contentType from GridFS", e);
        }
    }

    public void deleteFile(String fileId) {
        files.delete(new ObjectId(fileId));
    }
}
