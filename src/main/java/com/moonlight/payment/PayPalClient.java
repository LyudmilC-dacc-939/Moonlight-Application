package com.moonlight.payment;

import com.paypal.base.rest.APIContext;

public class PayPalClient {

    // Set up for development, if app goes live, these 3 must be changed to real credentials
    private static final String CLIENT_ID = "AWZqDdKfTwZYmFjETWe7p1xxRTicRDMFop-8Ys7rDklk3BwcHKvL-n2zZNpqgJk-ovMqtS4Bi39fjH5G";
    private static final String CLIENT_SECRET = "EJc3dxKkp4uYCUu8TC50SyFRUenNE3FnxIjI6i9IROfv8kbxzwnLV3gXxKtw4J-gNVyUYilqCK0bA53a";
    private static final String MODE = "sandbox";

    public APIContext getApiContext() {
        return new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
    }
}
