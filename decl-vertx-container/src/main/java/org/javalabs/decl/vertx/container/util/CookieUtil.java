package org.javalabs.decl.vertx.container.util;

import io.vertx.core.http.Cookie;

/**
 *
 * @author Sudiptasish Chanda
 */
public class CookieUtil {
    
    public static final String AUTH_COOKIE = "app_token";
    
    /**
     * Create a cookie to store the jwt token.
     * 
     * @param jwt
     * @return Cookie
     */
    public static Cookie create(String jwt) {
        Cookie cookie = Cookie.cookie(AUTH_COOKIE, jwt);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        
        return cookie;
    }
}
