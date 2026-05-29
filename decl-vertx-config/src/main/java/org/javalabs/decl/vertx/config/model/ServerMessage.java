package org.javalabs.decl.vertx.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

/**
 * POJO that represents an error message.
 * 
 * <p>
 * This class encapsulates information about an error that has occurred. It typically includes a 
 * message describing the error, and may also include other details like the error type, stack 
 * trace, or cause.
 *
 * @author Sudiptasish Chanda
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerMessage implements Serializable {

    private int code = -1;
    private String message = "";
    
    private String dc;
    private String server;
    private String serverMsg;
    private String hint;
    private String cause;
    private String exception;
    private String stacktrace;
    private Object context;
    
    public ServerMessage() { }
    
    public ServerMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Return the http status code.
     * @return int  The http status code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Set the http status code.
     * @param code  The http code to be set.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Return the error message.
     * @return String   The error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the error message.
     * @param message   Message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Return the data center or env name.
     * @return String   The name of the dc/env.
     */
    public String getDc() {
        return dc;
    }

    /**
     * Set the data center/env name.
     * @param dc    The dc/env name to be set.
     */
    public void setDc(String dc) {
        this.dc = dc;
    }

    /**
     * Return the server name that encounters the error.
     * @return String   The server/host name
     */
    public String getServer() {
        return server;
    }

    /**
     * Set the server/host name.
     * @param server    Server name to be set.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Return the original server message
     * @return String   Server message
     */
    public String getServerMsg() {
        return serverMsg;
    }

    /**
     * Set the original server message
     * @param serverMsg     Message to be set
     */
    public void setServerMsg(String serverMsg) {
        this.serverMsg = serverMsg;
    }

    /**
     * Return the hint.
     * @return String hint
     */
    public String getHint() {
        return hint;
    }

    /**
     * Set the hint.
     * @param hint  Hint to be set.
     */
    public void setHint(String hint) {
        this.hint = hint;
    }

    /**
     * Return the original cause of failure.
     * @return String   The cause of failure
     */
    public String getCause() {
        return cause;
    }
    
    /**
     * Set the original error cause
     * @param cause     Error cause to be set.
     */
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * Return the exception stack trace.
     * @return String   Stack trace
     */
    public String getStacktrace() {
        return stacktrace;
    }

    /**
     * Set the exception stack trace.
     * @param stacktrace    Error stack trace
     */
    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    /**
     * Return the exception class name.
     * @return String   Exception class name
     */
    public String getException() {
        return exception;
    }

    /**
     * Set the exception class name.
     * @param exception Exception class name to be set.
     */
    public void setException(String exception) {
        this.exception = exception;
    }

    /**
     * Return the error context
     * @return Object
     */
    public Object getContext() {
        return context;
    }

    /**
     * Set the error context.
     * @param context 
     */
    public void setContext(Object context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return new StringBuilder(128)
                .append("{")
                .append("\n    ").append("\"code\":").append(code).append(",")
                .append("\n    ").append("\"message\":").append("\"").append(message).append("\"").append(",")
                .append("\n    ").append("\"dc\":").append("\"").append(dc).append("\"").append(",")
                .append("\n    ").append("\"server\":").append("\"").append(server).append("\"").append(",")
                .append("\n    ").append("\"serverMsg\":").append("\"").append(serverMsg != null ? serverMsg : "NONE").append("\"").append(",")
                .append("\n    ").append("\"stacktrace\":").append("\"").append(stacktrace).append("\"")
                .append("\n}")
                .toString();
    }
}
