package com.group7.clubber_backend;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.lib.types.Schemas.Ping;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;

@RestController
public class PingController {

    @GetMapping("/ping")
    public Ping ping() {
        return new Ping(Database.getInstance().insert(DatabaseCollection.TEST, new Document("testfield", "testvalue")));
    }
}
