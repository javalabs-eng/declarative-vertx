package org.javalabs.decl.vertx.container.handler;

import org.javalabs.decl.vertx.container.util.ResponseHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic error handler for vert.x http request.
 * 
 * <p>
 * This error handler is attached to the global router. If any unchecked exception
 * arises from the underlying the module/component, it will eventually be caught
 * here and generic error message will be sent.
 *
 * @author Sudiptasish Chanda
 */
public class HttpErrorHandler implements Handler<RoutingContext> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpErrorHandler.class);
    
    public static final String UNAUTHORIZED_MSG = "Access to this resource is restricted";
    
    public static final List<String> JWT_EXPIRED = Arrays.asList(
            "Invalid JWT token: token expired."
            , "Expired JWT token: exp <= now");
    
    private final Vertx vertx;

    public HttpErrorHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RoutingContext ctx) {
        ServerMessage errorMsg = new ServerMessage();
        Throwable error = ctx.failure();
        
        // Start exception handling ...
        if (error instanceof IllegalArgumentException
                || error instanceof NoSuchElementException
                || error instanceof IllegalAccessException
                || error instanceof HttpException) {
            
            LOGGER.error("Error Handler Invoked. Msg: " + (error != null ? error.getMessage() : "NONE"));
        }
        else {
            LOGGER.error("Error Handler Invoked.", error);
        }
        ServerMessage customErr = customError(error);
        if (customErr != null) {
            errorMsg = customErr;
        }
        else {
            if (error instanceof DecodeException) {
                errorMsg.setCode(HttpURLConnection.HTTP_BAD_REQUEST);
                errorMsg.setMessage("Invalid payload. Cannot parse input payload.");
            }
            else if (error instanceof HttpException) {
                int statusCode = ((HttpException)error).getStatusCode();
                String payload = ((HttpException)error).getPayload();

                if (payload == null && statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    if (error.getCause() != null) {
                        payload = error.getCause().getMessage();
                    }
                    else {
                        payload = UNAUTHORIZED_MSG;
                    }
                }
                else if ("Unauthorized".equals(error.getMessage())) {
                    payload = UNAUTHORIZED_MSG;
                }
                else if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    // If the authorization header has only "Bearer " string, then jwt auth
                    // handler will throw a Bad Request.
                    // But, application will flag it as UnAuthorized access.
                    String authorization = ctx.request().headers().get(HttpHeaders.AUTHORIZATION);
                    if (authorization.indexOf(' ') <= 0) {
                        statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
                        payload = UNAUTHORIZED_MSG;
                    }
                }
                // Switching to vert.x 4.1.0 has changed the expired token error message
                // from "Expired JWT token: exp <= now" to "Expired JWT token: exp <= now",
                // which is causing the application client to fail.
                // Although the sdk is upgraded, but to support backward compatbility
                // the new error message is changed to old message.
                if (JWT_EXPIRED.get(0).equals(payload)) {
                    payload = JWT_EXPIRED.get(1);
                }
                errorMsg.setCode(statusCode);
                errorMsg.setMessage(payload);
            }
            else if (error instanceof IOException) {
                Throwable th = rootCause(error);
                errorMsg.setCode(HttpURLConnection.HTTP_INTERNAL_ERROR);

                if (th instanceof UnknownHostException) {
                    errorMsg.setMessage("Remote server connection cannot be established");
                }
                if (th instanceof NoSuchFileException) {
                    errorMsg.setMessage("File not found - " + th.getMessage());
                }
                else {
                    errorMsg.setMessage("Connection to server server timed-out");
                }
            }
            else if (error instanceof NoSuchElementException) {
                errorMsg.setCode(HttpURLConnection.HTTP_NOT_FOUND);
                errorMsg.setMessage(error.getMessage());
            }
            else if (error instanceof IllegalAccessException) {
                errorMsg.setCode(HttpURLConnection.HTTP_UNAUTHORIZED);
                errorMsg.setMessage(error.getMessage());
            }
            else if (error instanceof IllegalArgumentException) {
                errorMsg.setCode(HttpURLConnection.HTTP_BAD_REQUEST);
                errorMsg.setMessage(error.getMessage());
            }
            else if (error instanceof RuntimeException) {
                errorMsg.setCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
                errorMsg.setMessage("Internal Error!! Please contact support group");
            }
            else {
                errorMsg.setCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
                errorMsg.setMessage("Internal Error!! Please contact support group");
            }
        }
        
        // Add the error context, only if debug flag is passed.
        if (ctx.queryParams().contains("debug")) {
            errorMsg.setException(error != null ? error.getClass().getName() : "Error");
            errorMsg.setStacktrace(stacktrace(error));
            errorMsg.setCause(error != null ? error.getMessage() : "Not Available");
            errorMsg.setServer(host());
            errorMsg.setDc(dc());
        }
        else {
            errorMsg.setHint("Pass debug flag to see the root cause wherever applicable.");
        }
        try {
            ResponseHandler.send(ctx, errorMsg.getCode(), errorMsg);
        }
        catch (Throwable th) {
            LOGGER.error("FATAL ! Error sending response back to client", th);
        }
    }
    
    /**
     * Allow application to analyze and pass custom error message.
     * @return ErrorMessage
     */
    protected ServerMessage customError(Throwable error) {
        return null;
    }
    
    public static Throwable rootCause(Throwable th) {
        Throwable root = th;
        while (th.getCause() != null) {
            th = th.getCause();
            root = th;
        }
        return root;
    }
    
    private static String stacktrace(Throwable error) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(512);
        PrintStream ps = new PrintStream(bOut);
        error.printStackTrace(ps);
        
        return new String(bOut.toByteArray());
    }
    
    /**
     * Return the host name / server name.
     * @return String   The host name.
     */
    public static String host() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (UnknownHostException e) {
            return "localhost";
        }
    }

    /**
     * Return the data center.
     * 
     * <p>
     * The dc name is retrieved by calling the environment variable <code>CLUSTER_ENV</code>
     * or <code>EPAAS_ENV</code>. If neither of them is set, then the method will return NONE.
     * 
     * @return String   Name of the data center where this server is running.
     */
    public static String dc() {
        String env = System.getenv("CLUSTER_ENV");
        if (env == null || env.trim().length() == 0) {
            env = System.getenv("EPAAS_ENV");
            if (env == null || env.trim().length() == 0) {
                env = "NONE";
            }
        }
        return env;
    }
}
