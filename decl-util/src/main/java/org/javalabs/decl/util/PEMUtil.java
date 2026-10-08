/*
 * Copyright 2026 schan280.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javalabs.decl.util;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author schan280
 */
public class PEMUtil {
    
    public static final String KEY_BLOCK = "PRIVATE KEY";
    public static final String CERT_BLOCK = "CERTIFICATE";

    /**
     * Extract the private key or a certificate block.
     * 
     * label is "PRIVATE KEY" or "CERTIFICATE". Pattern.DOTALL makes "." match newlines too, which is what JavaScript's 
     * [\s\S] accomplishes - without it, "." stops at line boundaries and would never span a multi-line PEM body. 
     * 
     * The "(?:RSA )?" group optionally matches the older PKCS#1 header
     * 
     * ("-----BEGIN RSA PRIVATE KEY-----") in addition to PKCS#8's
     * ("-----BEGIN PRIVATE KEY-----"), exactly like the JS version.
     * 
     * @param bundle
     * @param label
     * @return String
     */
    public static String extractPemBlock(String bundle, String label) {
        String optionalRsa = KEY_BLOCK.equals(label) ? "(?:RSA )?" : "";
        Pattern pattern = Pattern.compile(
                "-----BEGIN " + optionalRsa + label + "-----.+?-----END " + optionalRsa + label + "-----",
                Pattern.DOTALL);
        
        Matcher matcher = pattern.matcher(bundle);

        if (! matcher.find()) {
            throw new IllegalStateException("PEM bundle is missing a " + label + " block");
        }
        return matcher.group();
    }
    
    public static PrivateKey generateKey(String raw) throws GeneralSecurityException {
        String cleanKey = raw
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);
        
        return privateKey;
    }
    
    public static Certificate generateCert(String raw) throws GeneralSecurityException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(raw.getBytes()));
        
        return cert;
    }
}
