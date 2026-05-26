package org.javalabs.decl.api.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 *
 * @author schan280
 */
public class KeyStoreCommand implements Command {

    private final String name;
    private final Executor executor = Executors.newFixedThreadPool(1);

    private static final String  ALIAS = "RS256";
    private static final String  KEY_ALGO = "RSA";
    private static final Integer KEY_SIZE = 2048;
    private static final String  SIGN_ALGO = "SHA256withRSA";
    private static final String  D_NAME = "CN=REST Test, OU=EA, O=Javalabs.org, L=Kolkata, ST=WB, C=IN";
    
    private static final String GEN_KEY_TEMPLATE = "keytool -genkey -alias {0} -keyalg {1} -sigalg {2} -dname \"{3}\" -keystore {0} -keysize {4} -validity {5} -storepass {6}";

    public KeyStoreCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        Project project = (Project) ctx.get("project.work");

        try {
            File projectRoot = new File(project.dir(), project.name());
            String keyStoreFile = projectRoot.getAbsolutePath()
                    + File.separator
                    + project.srcResourceDir()
                    + File.separator
                    + project.keyStore();

            if (Boolean.TRUE) {
                // generateCmdLine(project, keyStoreFile);
                generateManually(project, keyStoreFile);
            }

            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Generated key store: " + ConsoleWriter.ANSI_GREEN + keyStoreFile + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private void generateCmdLine(Project project, String keyStoreFile) throws Exception {
        String cmd = MessageFormat.format(GEN_KEY_TEMPLATE
                , ALIAS
                , KEY_ALGO
                , SIGN_ALGO
                , D_NAME
                , KEY_SIZE
                , keyStoreFile
                , project.validityDays()
                , project.storePass());

        String[] commands = {
            "/bin/bash",
            "-c",
            cmd
        };

        ProcessBuilder pb = new ProcessBuilder()
                .directory(new File(System.getProperty("user.dir")))
                .command(commands)
                .redirectErrorStream(true);

        Process process = pb.start();   // Start the process

        StreamReader reader = new StreamReader(project, process.getInputStream());
        executor.execute(reader);

        Boolean exited = process.waitFor(1, TimeUnit.MINUTES);
        if (!exited) {
            throw new RuntimeException("Unable to generate key store file");
        }
    }

    private void generateManually(Project project, String keyStoreFile) throws Exception {
        String alias = ALIAS;

        // Generate RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGO);
        keyPairGenerator.initialize(KEY_SIZE);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Generate a Self-Signed Certificate
        X509Certificate certificate = generateSelfSignedCertificate(keyPair, project);

        // Create PKCS12 keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);

        keyStore.setKeyEntry(
                alias,
                keyPair.getPrivate(),
                project.storePass().toCharArray(),
                new java.security.cert.Certificate[] {certificate}
        );

        // Save the Keystore
        try (FileOutputStream fos = new FileOutputStream(keyStoreFile)) {
            keyStore.store(fos, project.storePass().toCharArray());
        }
    }

    private X509Certificate generateSelfSignedCertificate(KeyPair keyPair, Project project) throws Exception {
        long now = System.currentTimeMillis();
        Date from = new Date(now);
        Date to = new Date(now + (project.validityDays() * 24 * 60 * 60 * 1000));

        BigInteger serial = new BigInteger(64, new SecureRandom());
        AlgorithmId algorithmId = AlgorithmId.get(SIGN_ALGO);
        
        X500Name owner = new X500Name(D_NAME);
        
        X509CertImpl certificate = certGen(keyPair, owner, serial, algorithmId, from, to);
        return certificate;
    }
    
    private X509CertImpl certGen(KeyPair keyPair
            , X500Name owner
            , BigInteger serial
            , AlgorithmId algorithmId
            , Date from
            , Date to) throws ReflectiveOperationException, IOException {
        
        Runtime.Version version = Runtime.version();
        
        if (version.feature() >= 21) {
            return certGenWithJdk21(keyPair, owner, serial, algorithmId, from, to);
        }
        else {
            return certGenWithJdk17AndBelow(keyPair, owner, serial, algorithmId, from, to);
        }
    }
    
    private X509CertImpl certGenWithJdk17AndBelow(KeyPair keyPair
            , X500Name owner
            , BigInteger serial
            , AlgorithmId algorithmId
            , Date from
            , Date to) throws ReflectiveOperationException, IOException {
        
        X509CertInfo info = new X509CertInfo();
        
        Method method = info.getClass().getDeclaredMethod("set", String.class, Object.class);
        
        // info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        method.invoke(info, X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        
        // info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serial));
        method.invoke(info, X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serial));
        
        // info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algorithmId));
        method.invoke(info, X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algorithmId));
        
        // info.set(X509CertInfo.SUBJECT, owner);
        method.invoke(info, X509CertInfo.SUBJECT, owner);
        
        // info.set(X509CertInfo.ISSUER, owner);
        method.invoke(info, X509CertInfo.ISSUER, owner);
        
        // info.set(X509CertInfo.KEY, owner);
        method.invoke(info, X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        
        // info.set(X509CertInfo.KEY, owner);
        method.invoke(info, X509CertInfo.VALIDITY, new CertificateValidity(from, to));
        
        X509CertImpl certificate = ObjectCreator.create(X509CertImpl.class.getName(), new Class[] {X509CertInfo.class}, new Object[] {info});
        method = certificate.getClass().getDeclaredMethod("sign", PrivateKey.class, String.class);
        method.invoke(certificate, keyPair.getPrivate(), SIGN_ALGO);
        
        return certificate;
    }
    
    private X509CertImpl certGenWithJdk21(KeyPair keyPair
            , X500Name owner
            , BigInteger serial
            , AlgorithmId algorithmId
            , Date from
            , Date to) throws ReflectiveOperationException, IOException {
        
        X509CertInfo info = new X509CertInfo();
        
        // info.setVersion(new CertificateVersion(CertificateVersion.V3));
        Method method = info.getClass().getDeclaredMethod("setVersion", CertificateVersion.class);
        method.invoke(info, new CertificateVersion(CertificateVersion.V3));
        
        // info.setSerialNumber(new CertificateSerialNumber(serial));
        method = info.getClass().getDeclaredMethod("setSerialNumber", CertificateSerialNumber.class);
        method.invoke(info, new CertificateSerialNumber(serial));
        
        // info.setAlgorithmId(new CertificateAlgorithmId(algorithmId));
        method = info.getClass().getDeclaredMethod("setAlgorithmId", CertificateAlgorithmId.class);
        method.invoke(info, new CertificateAlgorithmId(algorithmId));
        
        // info.setSubject(owner);
        method = info.getClass().getDeclaredMethod("setSubject", X500Name.class);
        method.invoke(info, owner);
        
        // info.setIssuer(owner);
        method = info.getClass().getDeclaredMethod("setIssuer", X500Name.class);
        method.invoke(info, owner);
        
        // info.setKey(new CertificateX509Key(keyPair.getPublic()));
        method = info.getClass().getDeclaredMethod("setKey", CertificateX509Key.class);
        method.invoke(info, new CertificateX509Key(keyPair.getPublic()));
        
        // info.setValidity(new CertificateValidity(from, to));
        method = info.getClass().getDeclaredMethod("setValidity", CertificateValidity.class);
        method.invoke(info, new CertificateValidity(from, to));

        // Create and Sign this Certificate
        // X509CertImpl certificate = X509CertImpl.newSigned(info, keyPair.getPrivate(), SIGN_ALGO);
        method = X509CertImpl.class.getDeclaredMethod("newSigned", X509CertInfo.class, PrivateKey.class, String.class);
        X509CertImpl certificate = (X509CertImpl)method.invoke(null, info, keyPair.getPrivate(), SIGN_ALGO);
        
        return certificate;
    }

    private class StreamReader implements Runnable {

        private final Project project;
        private final InputStream in;
        private final List<String> lines = new ArrayList<>();

        StreamReader(Project project, InputStream in) {
            this.project = project;
            this.in = in;
        }

        @Override
        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(in));

                for (String line = br.readLine(); line != null
                        && !Thread.currentThread().isInterrupted();
                        line = br.readLine()) {

                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stop();
            }
            if (project.verbose() <= 1) {
                ConsoleWriter.println("Sub-Process log:\n" + String.join("\n", lines.toArray(new String[lines.size()])));
            }
        }

        void stop() {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
