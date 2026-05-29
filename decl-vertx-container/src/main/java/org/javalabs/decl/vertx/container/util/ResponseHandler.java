package org.javalabs.decl.vertx.container.util;

import org.javalabs.decl.vertx.container.handler.HttpErrorHandler;
import io.vertx.ext.web.RoutingContext;
import java.util.Iterator;
import java.util.Map;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.internal.ConfigStorage;
import org.javalabs.decl.vertx.config.model.ServerMessage;

/**
 * Utility class to send the response to client.
 *
 * @author Sudiptasish Chanda
 */
public class ResponseHandler {
    
    private static final String APPLICATION_JSON = "application/json";

    /**
     * Send the http response to the client.
     *
     * @param ctx           Vertx request context object.
     * @param statusCode    http status code to be sent
     * @param body          Response body, if any. Can be empty.
     */
    public static void send(RoutingContext ctx
        , int statusCode
        , Object body) {

        send(ctx.request().params().contains("env")
                , ctx
                , null
                , statusCode
                , body
                , Boolean.TRUE
                , APPLICATION_JSON);
    }

    /**
     * Send the http response to the client.
     *
     * @param ctx           Vertx request context object.
     * @param headers       Additional headers passed by caller.
     * @param statusCode    http status code to be sent
     * @param body          Response body, if any. Can be empty.
     */
    public static void send(RoutingContext ctx
            , Map<String, String> headers
            , int statusCode
            , Object body) {

        send(ctx.request().params().contains("env")
                , ctx
                , headers
                , statusCode
                , body
                , Boolean.TRUE
                , APPLICATION_JSON);
    }

    /**
     * Write the response message to the output stream.
     *
     * @param env           Indicates whether to include the environment details.
     * @param ctx           Vertx request context object.
     * @param headers       Additional headers passed by caller.
     * @param statusCode    http status code to be sent
     * @param body          Response body, if any. Can be empty.
     * @param isChunked     Indicate whether the response body will be chunked.
     *                      If set to false, then the content-length must be set
     *                      before writing the response body on the stream.
     * @param contentType   Standard Content type, json/html/xml, etc
     */
    public static void send(Boolean env
            , RoutingContext ctx
            , Map<String, String> headers
            , int statusCode
            , Object body
            , boolean isChunked
            , String contentType) {

        ctx.response().setChunked(isChunked);
        ctx.response().setStatusCode(statusCode);
        if (body != null) {
            // ctx.response().headers().add("Content-Type", ConfigHolder.getInstance().getRouting().getApi().getConsume());
            ctx.response().headers().add("Content-Type", contentType);
        }
        ctx.response().headers().add("Accept", ConfigStorage.get().routingConfig().getApi().getProduce());

        if (body != null) {
            if (env && body instanceof ServerMessage) {
                ((ServerMessage)body).setDc(HttpErrorHandler.dc());
                ((ServerMessage)body).setServer(HttpErrorHandler.host());
            }
            // Always inject the env/dc and server name.
            ctx.response().headers()
                    .add("X-Csu-DC", HttpErrorHandler.dc())
                    .add("X-Csu-Server", HttpErrorHandler.host());

            // Add the additional headers.
            if (headers != null && ! headers.isEmpty()) {
                for (Iterator<Map.Entry<String, String>> itr = headers.entrySet().iterator(); itr.hasNext(); ) {
                    Map.Entry<String, String> me = itr.next();
                    ctx.response().headers().add(me.getKey(), me.getValue());
                }
            }

            if (body instanceof java.nio.Buffer) {
                java.nio.Buffer buffer = (java.nio.Buffer)body;
                if (! istio()) {
                    ctx.response().headers().add("Content-Length", String.valueOf(2 * buffer.limit()));
                }
                ctx.response().write(new String((byte[])buffer.array()));
            }
            else if (body instanceof byte[]) {
                byte[] arr = (byte[])body;
                if (! istio()) {
                    ctx.response().headers().add("Content-Length", String.valueOf(2 * arr.length));
                }
                ctx.response().write(new String(arr));
            }
            else {
                String output = "";
                if (APPLICATION_JSON.equalsIgnoreCase(contentType)) {
                    if (body instanceof String) {
                        output = (String)body;
                    }
                    else {
                        try {
                            output = new String(MapperUtil.prettyWrite(body));
                        }
                        catch (RuntimeException e) {
                            // Do-Nothing
                        }
                    }
                }
                else {
                    output = body.toString();
                }
                if (! isChunked) {
                    if (! istio()) {
                        ctx.response().headers().add("Content-Length", String.valueOf(2 * output.length()));
                    }
                }
                // XXX (Sudip): Temporary Fix for Cookie
                //ctx.response().headers().add("access-control-allow-headers", "*");
                ctx.response().write(output);
            }
        }
        ctx.response().end();
    }
    
    private static Boolean istio() {
        return Boolean.valueOf(System.getProperty("istio.proxy", "true"));
    }
}
