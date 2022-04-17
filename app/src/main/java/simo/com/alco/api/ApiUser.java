package simo.com.alco.api;

public class ApiUser {
    /**
     * User name
     */
    public String username;

    int userId;
    /**
     * Auth token, received after login
     * Token is static to make sure one application instance uses
     * one login at a time.
     */
    public static String TOKEN;

    /**
     * User's password
     */
    String password;

    /**
     * Indicates that user approved subscription to
     * our news feed
     */
    public Boolean optIn;

    /**
     * Indicates that user has administrative rights.
     * In this case app will give better prices :) and
     * decline shipping cost
     */
    public static Boolean isAdmin = false;

    public ApiUser() {
    }


    public ApiUser(String theName, String thePassword) {
        this.username = theName;
        this.password = thePassword;
    }

    public ApiUser(String theName, String thePassword, boolean optin)  {
        this(theName, thePassword);
        this.optIn = optin;
        this.isAdmin = false;
    }

    public ApiUser(String theName, String thePassword, boolean optin, int userId)  {
        this(theName, thePassword, optin);
        this.userId = userId;
    }

    public ApiUser(String theName, String thePassword, boolean optin, int userId, boolean isAdmin)  {
        this(theName, thePassword, optin, userId);
        this.isAdmin = isAdmin;
    }

}
