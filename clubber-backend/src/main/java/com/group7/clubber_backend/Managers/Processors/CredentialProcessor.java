package com.group7.clubber_backend.processors;

import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class CredentialProcessor {
    private final Logger logger;
    private final String symmetricEncryptionAlgorithm = "A256GCM";
    private final String asymmetricEncryptionAlgorithm = "RSA-OAEP-256";
    private final String asymmetricSignatureAlgorithm = "RS256";
    private final String symmetricTokenAlgorithm = "HS256";

    @Value("${app.credentials.path:./credentials}")
    private String credentialsPath;

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private byte[] symmetricKey;

    public CredentialProcessor() {
        this.logger = new Logger("Processor/Credential");
        initializeKeys();
    }

    private void initializeKeys() {
        try{
            File credentialsDir = new File(credentialsPath);
            if (!credentialsDir.exists()) {
                credentialsDir.mkdirs();
            }

            File publicKeyFile = new File(credentialsDir, "public.pem");
            File privateKeyFile = new File(credentialsDir, "private.pem");
            File symmetricKeyFile = new File(credentialsDir, "symmetric.key");

            if(!publicKeyFile.exists() || !privateKeyFile.exists() || !symmetricKeyFile.exists()){
                //generate new keys
                RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
                this.publicKey = rsaJsonWebKey.getPublicKey();
                this.privateKey = rsaJsonWebKey.getPrivateKey();
                
                this.symmetricKey = new byte[32];
                java.security.SecureRandom.getInstanceStrong().nextBytes(symmetricKey);

                //save keys to files
                try(FileOutputStream fos = new FileOutputStream(publicKeyFile)){
                    fos.write(publicKey.getEncoded());
                }

                try(FileOutputStream fos = new FileOutputStream(privateKeyFile)){
                    fos.write(privateKey.getEncoded());
                }

                try (FileOutputStream fos = new FileOutputStream(symmetricKeyFile)){
                    fos.write(symmetricKey);
                }

                this.logger.log("New credentials generated and saved", LogLevel.INFO);
            } 
            else {
                //load existing keys
                this.publicKey = java.security.KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(
                        Files.readAllBytes(publicKeyFile.toPath())));
                
                this.privateKey = java.security.KeyFactory.getInstance("RSA")
                    .generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(
                        Files.readAllBytes(privateKeyFile.toPath())));
                
                this.symmetricKey = Files.readAllBytes(symmetricKeyFile.toPath());
                
                this.logger.log("Existing credentials loaded", LogLevel.INFO);
            }
        }
        catch(Exception e){
            this.logger.log("Failed to initialize keys: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to initialize keys", e);
        }
    }

    public String getPublicKey(){
        try{
            RsaJsonWebKey jwk = new RsaJsonWebKey(publicKey);
            return jwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
        }
        catch(JoseException e){
            this.logger.log("Failed to export public key: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to export public key", e);
        }
    }

    public String decrypt(String encryptedData){
        try{
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setKey(privateKey);
            jwe.setCompactSerialization(encryptedData);
            return jwe.getPlaintextString();
        }
        catch(JoseException e){
            this.logger.log("Decryption failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }

    public String encrypt(String data) {
        try{
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload(data);
            jwe.setAlgorithmHeaderValue(asymmetricEncryptionAlgorithm);
            jwe.setEncryptionMethodHeaderParameter(symmetricEncryptionAlgorithm);
            jwe.setKey(publicKey);
            return jwe.getCompactSerialization();
        }
        catch(JoseException e){
            this.logger.log("Encryption failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String createToken(String userId) {
        try{
            JwtClaims claims = new JwtClaims();
            claims.setSubject(userId);
            claims.setIssuedAtToNow();
            claims.setIssuer("clubber-server");
            claims.setAudience("clubber-client");
            claims.setExpirationTimeMinutesInTheFuture(60 * 24 * 7); // 1 week

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(privateKey);
            jws.setAlgorithmHeaderValue(asymmetricSignatureAlgorithm);

            return jws.getCompactSerialization();
        }
        catch(JoseException e){
            this.logger.log("Token creation failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Token creation failed", e);
        }
    }

    public JwtClaims verifyToken(String jwt) {
        try{
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireSubject()
                .setExpectedIssuer("clubber-server")
                .setExpectedAudience("clubber-client")
                .setVerificationKey(publicKey)
                .build();

            return jwtConsumer.processToClaims(jwt);
        }
        catch(InvalidJwtException e){
            this.logger.log("Token verification failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }
} 