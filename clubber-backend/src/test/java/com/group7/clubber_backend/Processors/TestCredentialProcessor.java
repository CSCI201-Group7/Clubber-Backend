package com.group7.clubber_backend.Processors;

import java.util.Objects;

import com.group7.lib.types.Ids.UserId;

/**
 * Simple command-line test program for CredentialProcessor.
 */
public class TestCredentialProcessor {

    public static void main(String[] args) {
        System.out.println("Starting CredentialProcessor Test...");

        // Manually instantiate CredentialProcessor
        // Note: This assumes the default credentials path './config/credentials' is okay
        // or that the directory already exists from a previous run.
        // For a real test environment, consider using Spring Boot test context
        // or providing a specific configuration for the path.
        CredentialProcessor instance = CredentialProcessor.getInstance();

        // --- Test Data ---
        String originalData = "This is a secret message!";
        String userId = "user123";

        System.out.println("\n--- Encryption/Decryption Test ---");
        System.out.println("Original Data: " + originalData);

        // --- Encryption ---
        String encryptedData = null;
        try {
            encryptedData = instance.encrypt(originalData);
            System.out.println("Encrypted Data: " + encryptedData);
            if (encryptedData == null || encryptedData.isEmpty()) {
                System.err.println("Encryption failed to produce output.");
                return; // Stop if encryption failed
            }
        } catch (RuntimeException e) {
            System.err.println("Encryption threw an exception: " + e.getMessage());
            e.printStackTrace();
            return; // Stop if encryption failed
        }

        // --- Decryption ---
        String decryptedData = instance.decrypt(encryptedData);
        System.out.println("Decrypted Data: " + decryptedData);

        // --- Verification ---
        if (Objects.equals(originalData, decryptedData)) {
            System.out.println("SUCCESS: Decrypted data matches original data.");
        } else {
            System.err.println("FAILURE: Decrypted data does NOT match original data.");
        }

        System.out.println("\n--- Token Creation/Verification Test ---");
        System.out.println("User ID: " + userId);

        // --- Token Creation ---
        String token = null;
        try {
            token = instance.createToken(userId);
            System.out.println("Generated Token: " + token);
            if (token == null || token.isEmpty()) {
                System.err.println("Token creation failed to produce output.");
                return; // Stop if token creation failed
            }
        } catch (RuntimeException e) {
            System.err.println("Token creation threw an exception: " + e.getMessage());
            e.printStackTrace();
            return; // Stop if token creation failed
        }

        // --- Token Verification ---
        UserId extractedUserId = instance.verifyToken(token);

        if (extractedUserId != null) {
            System.out.println("SUCCESS: Token verified successfully.");
            if (Objects.equals(extractedUserId.toString(), userId)) {
                System.out.println("SUCCESS: Token subject matches original user ID.");
            } else {
                System.err.println("FAILURE: Token subject (" + extractedUserId.toString() + ") does NOT match original user ID (" + userId + ").");
            }
        } else {
            System.err.println("FAILURE: Token verification failed.");
        }

        System.out.println("\nCredentialProcessor Test Finished.");
    }
}
