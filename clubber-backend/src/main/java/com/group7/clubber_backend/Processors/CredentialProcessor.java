package com.group7.clubber_backend.Processors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import com.group7.lib.types.Ids.UserId;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class CredentialProcessor {

    private static final CredentialProcessor instance = new CredentialProcessor();

    public static CredentialProcessor getInstance() {
        return instance;
    }

    private final Logger logger;
    private final String symmetricEncryptionAlgorithm = "A128GCM";
    private final String asymmetricEncryptionAlgorithm = "RSA-OAEP";
    private final String signingAlgorithm = "RS256";

    private String credentialsPath = "./config/credentials";

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    private CredentialProcessor() {
        this.logger = new Logger("Processors/Credential");
        File credentialsDir = new File(credentialsPath);
        if (!credentialsDir.exists()) {
            credentialsDir.mkdirs();
        }

        File publicKeyFile = new File(credentialsDir, "public.pem");
        File privateKeyFile = new File(credentialsDir, "private.pem");

        if (!publicKeyFile.exists() || !privateKeyFile.exists()) {
            //generate new keys
            try {
                RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
                this.publicKey = rsaJsonWebKey.getRsaPublicKey();
                this.privateKey = rsaJsonWebKey.getRsaPrivateKey();
            } catch (JoseException e) {
                this.logger.log("Failed to generate keys: " + e.getMessage(), LogLevel.ERROR);
                throw new RuntimeException("Failed to generate keys", e);
            }

            //save keys to files
            try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
                fos.write(publicKey.getEncoded());
            } catch (IOException e) {
                this.logger.log("Failed to save public key: " + e.getMessage(), LogLevel.ERROR);
                throw new RuntimeException("Failed to save public key", e);
            }

            try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
                fos.write(privateKey.getEncoded());
            } catch (IOException e) {
                this.logger.log("Failed to save private key: " + e.getMessage(), LogLevel.ERROR);
                throw new RuntimeException("Failed to save private key", e);
            }

            this.logger.log("New credentials generated and saved", LogLevel.INFO);
        } else {
            //load existing keys
            try {
                this.publicKey = (RSAPublicKey) java.security.KeyFactory.getInstance("RSA")
                        .generatePublic(new java.security.spec.X509EncodedKeySpec(
                                Files.readAllBytes(publicKeyFile.toPath())));

                this.privateKey = (RSAPrivateKey) java.security.KeyFactory.getInstance("RSA")
                        .generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(
                                Files.readAllBytes(privateKeyFile.toPath())));
            } catch (NoSuchAlgorithmException | IOException e) {
                this.logger.log("Failed to load keys: " + e.getMessage(), LogLevel.ERROR);
                throw new RuntimeException("Failed to load keys", e);
            } catch (InvalidKeySpecException e) {
                this.logger.log("Failed to load private key: " + e.getMessage(), LogLevel.ERROR);
                throw new RuntimeException("Failed to load private key", e);
            }

            this.logger.log("Existing credentials loaded", LogLevel.INFO);
        }
    }

    public String getPublicKey() {
        RsaJsonWebKey rsaJsonWebKey = new RsaJsonWebKey(publicKey);
        return rsaJsonWebKey.toJson();
    }

    public String decrypt(String encryptedData) {
        try {
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setKey(privateKey);
            jwe.setCompactSerialization(encryptedData);
            return jwe.getPlaintextString();
        } catch (JoseException e) {
            this.logger.log("Decryption failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }

    public String encrypt(String data) {
        try {
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload(data);
            jwe.setAlgorithmHeaderValue(asymmetricEncryptionAlgorithm);
            jwe.setEncryptionMethodHeaderParameter(symmetricEncryptionAlgorithm);
            jwe.setKey(publicKey);
            return jwe.getCompactSerialization();
        } catch (JoseException e) {
            this.logger.log("Encryption failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String createToken(String userId) {
        try {
            JwtClaims claims = new JwtClaims();
            claims.setSubject(userId);
            claims.setIssuedAtToNow();
            claims.setIssuer("clubber-server");
            claims.setAudience("clubber-client");
            claims.setExpirationTimeMinutesInTheFuture(60 * 24 * 7); // 1 week
            claims.setClaim("userId", userId);

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(privateKey);
            jws.setAlgorithmHeaderValue(signingAlgorithm);

            return jws.getCompactSerialization();
        } catch (JoseException e) {
            this.logger.log("Token creation failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Token creation failed", e);
        }
    }

    public UserId verifyToken(String jwt) {
        try {
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireSubject()
                    .setRequireExpirationTime()
                    .setExpectedIssuer("clubber-server")
                    .setExpectedAudience("clubber-client")
                    .setVerificationKey(publicKey)
                    .build();

            JwtClaims claims = jwtConsumer.processToClaims(jwt);
            return new UserId(claims.getClaimValue("userId", String.class));
        } catch (InvalidJwtException e) {
            this.logger.log("Token verification failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        } catch (MalformedClaimException e) {
            this.logger.log("Token verification failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }
}
