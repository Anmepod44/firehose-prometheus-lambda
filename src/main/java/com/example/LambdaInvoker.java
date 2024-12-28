package com.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.regions.Region;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class LambdaInvoker {

    private static final String FUNCTION_NAME = "YourLambdaFunctionName"; // Replace with your Lambda function name
    private static final String REGION = "us-west-2"; // Change to your region

    public static void main(String[] args) throws IOException {
        // Initialize Lambda client
        LambdaClient lambdaClient = LambdaClient.builder()
                .region(Region.of(REGION))
                .build();

        // Create an event payload (in this case, a simple JSON string)
        String jsonPayload = createJsonPayload();

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

    // Method to create a sample event payload (this should match the structure your Lambda expects)
    private static String createJsonPayload() {
        // Example event data that matches your Lambda's expected input format
        // This is just a basic example, update it as needed to match your actual event structure
        String eventJson = "{\n" +
                "  \"records\": [\n" +
                "    {\n" +
                "      \"recordId\": \"1234567890\",\n" +
                "      \"data\": \"eyJtZXRyaW5hbG...T0YXRlX3Jlbm9nZXMiXX0=\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        return eventJson;
    }
}
