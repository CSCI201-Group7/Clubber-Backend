package com.group7.clubber_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.clubber_backend.Processors.CredentialProcessor;

@RestController
@RequestMapping("/utilities")
public class UtilitiesController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    /*
     * Returns the RSA public key in JWK format
     */
    @GetMapping("/public-key")
    public String publicKey() {
        return CredentialProcessor.getInstance().getPublicKey();
    }
}
