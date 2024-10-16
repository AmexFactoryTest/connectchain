package com.americanexpress.connectchain.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class TokenUtil {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String getToken(String url, String apiKey, String apiSecret) throws IOException {
        long timestamp = Instant.now().getEpochSecond();
        String signature = generateSignature(apiSecret, String.valueOf(timestamp));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", apiKey)
                .addHeader("X-SIGNATURE", signature)
                .addHeader("X-TIMESTAMP", String.valueOf(timestamp))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("token").asText();
        }
    }

    public static boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);
            long exp = jsonNode.get("exp").asLong();
            return Instant.now().getEpochSecond() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    private static String generateSignature(String secret, String message) throws IOException {
        try {
            Mac sha256Hmac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            sha256Hmac.init(secretKey);
            byte[] hmacBytes = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IOException("Error generating signature", e);
        }
    }
}