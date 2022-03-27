package com.relay.test;

import java.util.ResourceBundle;

public class ApiRequest {
    private static ResourceBundle applicationContext;

    public static void setApplicationContext () {
        applicationContext = ResourceBundle.getBundle("config");
    }

    public static void setApplicationContext (ResourceBundle customResourceBundle) {
        applicationContext = customResourceBundle;
    }

    public static ResourceBundle getApplicationContext() {
        return applicationContext;
    }

    public static String getCassandraHost() {
        return applicationContext.getString("cassandra.db.host");
    }

    public static int getCassandraPort() {
        return Integer.parseInt(applicationContext.getString("cassandra.db.port"));
    }
}
