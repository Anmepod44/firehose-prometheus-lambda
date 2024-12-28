package com.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.regions.Region;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Base64;

public class LambdaInvoker {

    private static final String FUNCTION_NAME = "YourLambdaFunctionName"; // Replace with your Lambda function name
    private static final String REGION = "us-west-2"; // Change to your region

    public static void main(String[] args) throws IOException {
        // Initialize Lambda client
        LambdaClient lambdaClient = LambdaClient.builder()
                .region(Region.of(REGION))
                .build();

        // Define event type (for demonstration, we'll use a specific event, but you can loop through these)
        String eventType = "ConditionalCheckFailedRequests";  // Replace with the event you want to test

        // Create the event payload based on the event type
        String jsonPayload = createJsonPayload(eventType);

        // Convert payload to SDKBytes (necessary for Lambda invocation)
        SdkBytes payloadBytes = SdkBytes.fromByteArray(jsonPayload.getBytes(StandardCharsets.UTF_8));

        // Create InvokeRequest
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(FUNCTION_NAME)
                .payload(payloadBytes)
                .build();

        // Invoke the Lambda function
        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);

        // Get the response
        String responsePayload = invokeResponse.payload().asString(StandardCharsets.UTF_8);
        System.out.println("Lambda Response: " + responsePayload);

        // Close the Lambda client
        lambdaClient.close();
    }

    // Method to create an event payload based on the event type
    private static String createJsonPayload(String eventType) {
        // Construct the JSON payload for each event type
        String eventJson = "";

        switch (eventType) {
            case "ConditionalCheckFailedRequests":
                eventJson = generateEventPayload("ConditionalCheckFailedRequests", 100);
                break;
            case "ConsumedReadCapacityUnits":
                eventJson = generateEventPayload("ConsumedReadCapacityUnits", 200);
                break;
            case "ConsumedWriteCapacityUnits":
                eventJson = generateEventPayload("ConsumedWriteCapacityUnits", 300);
                break;
            case "ReadThrottleEvents":
                eventJson = generateEventPayload("ReadThrottleEvents", 10);
                break;
            case "ReturnedBytes":
                eventJson = generateEventPayload("ReturnedBytes", 1500);
                break;
            case "ReturnedItemCount":
                eventJson = generateEventPayload("ReturnedItemCount", 120);
                break;
            case "ReturnedRecordsCount":
                eventJson = generateEventPayload("ReturnedRecordsCount", 50);
                break;
            case "SuccessfulRequestLatency":
                eventJson = generateEventPayload("SuccessfulRequestLatency", 35);
                break;
            case "SystemErrors":
                eventJson = generateEventPayload("SystemErrors", 5);
                break;
            case "TimeToLiveDeletedItemCount":
                eventJson = generateEventPayload("TimeToLiveDeletedItemCount", 3);
                break;
            case "ThrottledRequests":
                eventJson = generateEventPayload("ThrottledRequests", 8);
                break;
            case "UserErrors":
                eventJson = generateEventPayload("UserErrors", 2);
                break;
            case "WriteThrottleEvents":
                eventJson = generateEventPayload("WriteThrottleEvents", 15);
                break;
            case "OnDemandMaxReadRequestUnits":
                eventJson = generateEventPayload("OnDemandMaxReadRequestUnits", 1200);
                break;
            case "OnDemandMaxWriteRequestUnits":
                eventJson = generateEventPayload("OnDemandMaxWriteRequestUnits", 1100);
                break;
            case "AccountMaxReads":
                eventJson = generateEventPayload("AccountMaxReads", 500);
                break;
            case "AccountMaxTableLevelReads":
                eventJson = generateEventPayload("AccountMaxTableLevelReads", 700);
                break;
            case "AccountMaxTableLevelWrites":
                eventJson = generateEventPayload("AccountMaxTableLevelWrites", 400);
                break;
            case "AccountMaxWrites":
                eventJson = generateEventPayload("AccountMaxWrites", 600);
                break;
            case "ThrottledPutRecordCount":
                eventJson = generateEventPayload("ThrottledPutRecordCount", 10);
                break;
            default:
                System.out.println("Unknown event type: " + eventType);
                break;
        }

        return eventJson;
    }

    // Helper method to generate a base64-encoded payload for an event
    private static String generateEventPayload(String eventName, int value) {
        // Construct the JSON data
        String eventData = "{\n" +
                "  \"eventName\": \"" + eventName + "\",\n" +
                "  \"value\": " + value + "\n" +
                "}";

        // Base64 encode the event data (as your Lambda expects base64-encoded data)
        String encodedData = Base64.getEncoder().encodeToString(eventData.getBytes(StandardCharsets.UTF_8));

        // Construct the final JSON structure with the base64-encoded data
        String jsonPayload = "{\n" +
                "  \"records\": [\n" +
                "    {\n" +
                "      \"recordId\": \"1234567890\",\n" +
                "      \"data\": \"" + encodedData + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return jsonPayload;
    }
}
