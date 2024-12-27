package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LambdaHandlerTest {

    @Test
    public void testCreateSamples() {
        LambdaHandler.Value values = new LambdaHandler.Value();
        values.setCount(1);
        values.setSum(2);
        values.setMax(3);
        values.setMin(4);
        long timestamp = 123;

        Sample countSample = createCountSample(values, timestamp);
        assertEquals(1, countSample.getValue());
        assertEquals(123, countSample.getTimestamp());

        Sample maxSample = createMaxSample(values, timestamp);
        assertEquals(3, maxSample.getValue());
        assertEquals(123, maxSample.getTimestamp());

        Sample minSample = createMinSample(values, timestamp);
        assertEquals(4, minSample.getValue());
        assertEquals(123, minSample.getTimestamp());

        Sample sumSample = createSumSample(values, timestamp);
        assertEquals(2, sumSample.getValue());
        assertEquals(123, sumSample.getTimestamp());
    }

    private Sample createCountSample(LambdaHandler.Value value, long timestamp) {
        return new Sample(value.getCount(), timestamp);
    }

    private Sample createMaxSample(LambdaHandler.Value value, long timestamp) {
        return new Sample(value.getMax(), timestamp);
    }

    private Sample createMinSample(LambdaHandler.Value value, long timestamp) {
        return new Sample(value.getMin(), timestamp);
    }

    private Sample createSumSample(LambdaHandler.Value value, long timestamp) {
        return new Sample(value.getSum(), timestamp);
    }

    // Sample class
    public static class Sample {
        private double value;
        private long timestamp;

        public Sample(double value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public double getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}