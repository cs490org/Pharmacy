package com.cs490.group4.constants;

public class TokenConstants {

    // 1000ms * 60 * 120 = 2 hours
    public static final int ADMIN_ACCESS_TOKEN_EXPIRATION=1000*60*120;
    // 1000ms * 60 * 120 = 2 hours
    public static final int ADMIN_REFRESH_TOKEN_EXPIRATION=1000*60*120;

    // 1000ms * 60 * 60 * 24 * 7 = 1 week
    public static final long USER_ACCESS_TOKEN_EXPIRATION=(1000L*60*60*24*7);

    // 1000ms * 60 * 60 * 24 * 7 = 1 week
    public static final long USER_REFRESH_TOKEN_EXPIRATION=(1000L*60*60*24*7);

}
