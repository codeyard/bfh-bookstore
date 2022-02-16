package org.bookstore.security;

public final class Constants {

    private static final String ROLE_PREFIX = "ROLE_";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String CUSTOMER_AUTHORITY = ROLE_PREFIX + CUSTOMER;
    public static final String EMPLOYEE_AUTHORITY = ROLE_PREFIX + EMPLOYEE;

    public static String JWT_BEARER = "Bearer ";
    public static String AUTHORIZATION_HEADER = "Authorization";
    public static long EXPIRATION_TIME = 1000 * 60 * 60 * 10;
}
