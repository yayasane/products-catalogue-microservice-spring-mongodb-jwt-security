package org.sid.catalogueservice.security;

public interface SecurityParams {
    public static final String JWT_HEADER_NAME = "Authorization";
    public static final String SECRET = "yabadji2010@gmail.com";
    public static final String HEADER_PREFIX = "Bearer ";
    public static final long EXPIRATION = 10 * 24 * 3600;
}
