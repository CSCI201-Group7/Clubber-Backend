package com.group7.lib.utilities.Crypto;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Crypto {
    public static String hash(String input){
        return BCrypt.hashpw(input, BCrypt.gensalt(12));
    }

    public static boolean verify(String input, String hashedInput){
        return BCrypt.checkpw(input, hashedInput);
    }
}
