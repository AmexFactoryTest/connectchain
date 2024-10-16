package com.americanexpress.connectchain.utils;

import com.americanexpress.connectchain.Config;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);
    private static final String ALGORITHM = "AES";
    private static final String CONFIG_KEY = "token_encryption_key";

    public static String getToken(String tokenName) {
        try {
            Config config = Config.getInstance();
            String encryptedToken = (String) config.getConfig(tokenName);
            if (encryptedToken == null) {
                logger.error("Token not found: {}", tokenName);
                return null;
            }
            String encryptionKey = (String) config.getConfig(CONFIG_KEY);
            return decrypt(encryptedToken, encryptionKey);
        } catch (Exception e) {
            logger.error("Error retrieving token: {}", tokenName, e);
            return null;
        }
    }

    public static String encrypt(String value, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedValue, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static SecretKeySpec generateKey(String key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key256 = digest.digest();
        return new SecretKeySpec(key256, ALGORITHM);
    }

    public static String createAuthHeader(String apiKey) {
        JSONObject header = new JSONObject();
        header.put("Authorization", "Bearer " + apiKey);
        return header.toString();
    }

    public static void storeEncryptedToken(String tokenName, String tokenValue, String encryptionKey) {
        try {
            String encryptedToken = encrypt(tokenValue, encryptionKey);
            Config config = Config.getInstance();
            config.setConfig(tokenName, encryptedToken);
            logger.info("Token stored successfully: {}", tokenName);
        } catch (Exception e) {
            logger.error("Error storing token: {}", tokenName, e);
        }
    }
}
```

This Java implementation of TokenUtil provides the following functionality:

1. `getToken(String tokenName)`: Retrieves and decrypts a token from the configuration.
2. `encrypt(String value, String key)`: Encrypts a value using AES encryption.
3. `decrypt(String encryptedValue, String key)`: Decrypts an encrypted value.
4. `generateKey(String key)`: Generates a SecretKeySpec from a given key.
5. `createAuthHeader(String apiKey)`: Creates an authorization header for API requests.
6. `storeEncryptedToken(String tokenName, String tokenValue, String encryptionKey)`: Encrypts and stores a token in the configuration.

This implementation ensures that tokens are securely stored and retrieved, maintaining compatibility with the authentication flow used in the Python version. It uses Java's security libraries for cryptographic operations and interacts with the Config class for storing and retrieving configuration values.

Note: Make sure to add the org.json dependency to your pom.xml file:

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20230227</version>
</dependency>