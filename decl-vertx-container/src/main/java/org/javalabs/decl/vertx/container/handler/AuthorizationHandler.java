package org.javalabs.decl.vertx.container.handler;

import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.JWTAuthHandlerImpl;
import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public class AuthorizationHandler extends JWTAuthHandlerImpl {

    protected final String AUTH_HEADER = "Authorization";

    protected final List<String> includes;
    protected final List<String> excludes;

    public AuthorizationHandler(JWTAuth jwtAuth,
             List<String> includes,
             List<String> excludes,
             String realm) {

        super(jwtAuth, realm);

        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public void handle(RoutingContext ctx) {
        if (excludes != null && !excludes.isEmpty()) {
            boolean skip = false;

            for (String skipPattern : excludes) {
                if (ctx.normalizedPath().startsWith(skipPattern)
                        || ctx.normalizedPath().equals(skipPattern.substring(0, skipPattern.length() - 1))) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                ctx.next();
                return;
            }
        }
        if (includes != null && !includes.isEmpty()) {
            boolean skip = true;

            for (String skipPattern : includes) {
                if (ctx.normalizedPath().startsWith(skipPattern)) {
                    skip = false;
                    break;
                }
            }
            if (!skip) {
                ctx.next();
                return;
            }
        }
        forward(ctx);
    }

    protected void forward(RoutingContext ctx) {
        super.handle(ctx);
    }
}
