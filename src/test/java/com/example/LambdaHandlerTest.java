package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import java.nio.ByteBuffer;
import java.util.List;

public class LambdaHandlerTest {

    @Mock
    private Context context;

    private AWSLambda lambdaClient;  // AWS Lambda client

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getLogger()).thenReturn(mock(LambdaLogger.class));
        
        // Initialize the AWS Lambda client
        lambdaClient = com.amazonaws.services.lambda.AWSLambdaClient.builder().build();
    }

    @Test
    public void testHandleRequestWithSingleDynamoDBMetric() throws Exception {
        LambdaHandler handler = new LambdaHandler();
        KinesisFirehoseEvent event = new KinesisFirehoseEvent();
        KinesisFirehoseEvent.Record record = new KinesisFirehoseEvent.Record();
        record.setData(ByteBuffer.wrap(("{\"metricName\":\"ConditionalCheckFailedRequests\",\"value\":{\"count\":1}}").getBytes()));
        event.setRecords(List.of(record));

        // Before invoking Lambda, create the payload for invocation
        String payload = "{\"records\":[{\"data\":\"{\\\"metricName\\\":\\\"ConditionalCheckFailedRequests\\\",\\\"value\\\":{\\\"count\\\":1}}\"}]}";

        // Invoke the Lambda function directly via AWS SDK
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("your-lambda-function-name") // Replace with your Lambda function name
                .withPayload(payload);
        
        InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);

        // Convert the result payload to String and assert it
        String result = new String(invokeResult.getPayload().array());
        System.out.println("Lambda Invoke Result: " + result);
        
        // Assuming LambdaResponse is the expected output from the Lambda function
        assertEquals("ExpectedResult", result);  // Adjust based on your expected result
    }

    @Test
    public void testHandleRequestWithMultipleDynamoDBMetrics() throws Exception {
        LambdaHandler handler = new LambdaHandler();
        KinesisFirehoseEvent event = new KinesisFirehoseEvent();
        KinesisFirehoseEvent.Record record = new KinesisFirehoseEvent.Record();
        String data = "{\"metricName\":\"ConsumedReadCapacityUnits\",\"value\":{\"count\":1}}\n" +
                      "{\"metricName\":\"ConsumedWriteCapacityUnits\",\"value\":{\"count\":2}}\n" +
                      "{\"metricName\":\"ReadThrottleEvents\",\"value\":{\"count\":3}}\n" +
                      "{\"metricName\":\"ReturnedBytes\",\"value\":{\"count\":4}}\n" +
                      "{\"metricName\":\"ReturnedItemCount\",\"value\":{\"count\":5}}\n" +
                      "{\"metricName\":\"ReturnedRecordsCount\",\"value\":{\"count\":6}}\n" +
                      "{\"metricName\":\"SuccessfulRequestLatency\",\"value\":{\"count\":7}}\n" +
                      "{\"metricName\":\"SystemErrors\",\"value\":{\"count\":8}}\n" +
                      "{\"metricName\":\"TimeToLiveDeletedItemCount\",\"value\":{\"count\":9}}\n" +
                      "{\"metricName\":\"ThrottledRequests\",\"value\":{\"count\":10}}\n" +
                      "{\"metricName\":\"UserErrors\",\"value\":{\"count\":11}}\n" +
                      "{\"metricName\":\"WriteThrottleEvents\",\"value\":{\"count\":12}}\n" +
                      "{\"metricName\":\"OnDemandMaxReadRequestUnits\",\"value\":{\"count\":13}}\n" +
                      "{\"metricName\":\"OnDemandMaxWriteRequestUnits\",\"value\":{\"count\":14}}\n" +
                      "{\"metricName\":\"AccountMaxReads\",\"value\":{\"count\":15}}\n" +
                      "{\"metricName\":\"AccountMaxTableLevelReads\",\"value\":{\"count\":16}}\n" +
                      "{\"metricName\":\"AccountMaxTableLevelWrites\",\"value\":{\"count\":17}}\n" +
                      "{\"metricName\":\"AccountMaxWrites\",\"value\":{\"count\":18}}\n" +
                      "{\"metricName\":\"ThrottledPutRecordCount\",\"value\":{\"count\":19}}";
        record.setData(ByteBuffer.wrap(data.getBytes()));
        event.setRecords(List.of(record));

        // Prepare payload for invocation
        String payload = "{\"records\":[{\"data\":\"" + data + "\"}]}";

        // Invoke the Lambda function directly via AWS SDK
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("your-lambda-function-name") // Replace with your Lambda function name
                .withPayload(payload);

        InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);

        // Convert the result payload to String and assert it
        String result = new String(invokeResult.getPayload().array());
        System.out.println("Lambda Invoke Result: " + result);

        // Assuming LambdaResponse is the expected output from the Lambda function
        assertEquals("ExpectedResult", result);  // Adjust based on your expected result
    }
}
