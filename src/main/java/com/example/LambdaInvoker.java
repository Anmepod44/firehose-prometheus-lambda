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
        LambdaClient lambdaClient = LambdaClient.builder()
                .region(Region.of(REGION))
                .build();

        String eventType = "ConditionalCheckFailedRequests";  // Event to test
        String jsonPayload = createJsonPayload(eventType);

        SdkBytes payloadBytes = SdkBytes.fromByteArray(jsonPayload.getBytes(StandardCharsets.UTF_8));

        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(FUNCTION_NAME)
                .payload(payloadBytes)
                .build();

        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
        String responsePayload = invokeResponse.payload().asString(StandardCharsets.UTF_8);
        System.out.println("Lambda Response: " + responsePayload);

        lambdaClient.close();
    }

    // Method to create an event payload based on the event type
    private static String createJsonPayload(String eventType) {
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
        String eventData = "{\n" +
                "  \"eventName\": \"" + eventName + "\",\n" +
                "  \"value\": " + value + "\n" +
                "}";

        String encodedData = Base64.getEncoder().encodeToString(eventData.getBytes(StandardCharsets.UTF_8));

        return "{\n" +
                "  \"records\": [\n" +
                "    {\n" +
                "      \"recordId\": \"1234567890\",\n" +
                "      \"data\": \"" + encodedData + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
