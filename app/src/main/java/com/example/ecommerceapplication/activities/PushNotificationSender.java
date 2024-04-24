package com.example.ecommerceapplication.activities;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushNotificationSender {
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "Your_FCM_Server_Key"; // Keep this secret!

    public void sendPushNotification(String fcmToken, String orderId) {
        try {
            OkHttpClient client = new OkHttpClient();

            // Build the JSON body for the notification
            JSONObject notification = new JSONObject();
            notification.put("title", "New Order Received");
            notification.put("body", "You've received a new order with ID: " + orderId);

            JSONObject data = new JSONObject();
            data.put("orderId", orderId);

            JSONObject message = new JSONObject();
            message.put("to", fcmToken);
            message.put("notification", notification);
            message.put("data", data);

            // Build the request
            RequestBody body = RequestBody.create(message.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(FCM_URL)
                    .addHeader("Authorization", "key=" + SERVER_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            // Send the request
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to send notification: " + response.message());
            }
        } catch (Exception e) {
            // Handle errors
            e.printStackTrace(); // Log or handle the error as needed
        }
    }
}
