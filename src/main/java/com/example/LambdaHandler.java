package com.example;

/**
 * This class implements an AWS Lambda function that processes Kinesis Firehose events.
 * It extracts metrics from the event records and pushes them to a Prometheus PushGateway.
 * 
 * The main components of this Lambda function are:
 * 
 * - MetricStreamData: A class representing the metric data extracted from the Kinesis Firehose event records.
 * - Value: A class representing the metric values.
 * - KinesisFirehoseResponse: A class representing the response to be sent back to Kinesis Firehose.
 * 
 * The function processes each record in the Kinesis Firehose event, extracts metrics, creates Prometheus gauge metrics,
 * and pushes these metrics to a Prometheus PushGateway.
 * 
 * The metrics include:
 * - Count
 * - Sum
 * - Max
 * - Min
 * 
 * The metrics are named using the format <metric_name>_<metric_type>, where metric_type can be count, sum, max, or min.
 * 
 * The function requires the following environment variables:
 * - PROMETHEUS_PUSHGATEWAY_URL: The URL of the Prometheus PushGateway.
 * 
 * The Lambda execution role must have the necessary permissions to access Kinesis Firehose and CloudWatch Logs.
 */

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.Gauge;
import io.prometheus.client.Collector;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class LambdaHandler implements RequestHandler<KinesisFirehoseEvent, LambdaHandler.KinesisFirehoseResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public KinesisFirehoseResponse handleRequest(KinesisFirehoseEvent firehoseEvent, Context context) {
        KinesisFirehoseResponse response = new KinesisFirehoseResponse();
        List<KinesisFirehoseResponse.Record> responseRecords = new ArrayList<>();

        for (KinesisFirehoseEvent.Record record : firehoseEvent.getRecords()) {
            String data = new String(record.getData().array());
            String[] splitRecord = data.split("\n");

            for (String x : splitRecord) {
                if (x.isEmpty()) {
                    continue;
                }

                try {
                    MetricStreamData metricStreamData = objectMapper.readValue(x, MetricStreamData.class);
                    List<Gauge> gauges = createGauges(metricStreamData);

                    // Push the metrics to Prometheus
                    pushMetricsToPrometheus(gauges);

                    // Add the record to the response
                    KinesisFirehoseResponse.Record responseRecord = new KinesisFirehoseResponse.Record();
                    responseRecord.setRecordId(record.getRecordId());
                    responseRecord.setResult(KinesisFirehoseResponse.Result.Ok);
                    responseRecords.add(responseRecord);
                } catch (Exception e) {
                    context.getLogger().log("Error processing record: " + e.getMessage());
                }
            }
        }

        response.setRecords(responseRecords);
        return response;
    }

    private List<Gauge> createGauges(MetricStreamData metricStreamData) {
        List<Gauge> gauges = new ArrayList<>();
        String metricName = sanitize(metricStreamData.getMetricName());

        Gauge countGauge = Gauge.build()
                .name(metricName + "_count")
                .help("Count metric")
                .labelNames("namespace", "account_id", "region", "dimensions")
                .register();
        countGauge.labels(metricStreamData.getNamespace(), metricStreamData.getAccountID(), metricStreamData.getRegion(), metricStreamData.getDimensions().toString())
                .set(metricStreamData.getValue().getCount());
        gauges.add(countGauge);

        // Similarly create gauges for Sum, Max, Min
        // ...

        return gauges;
    }

    private void pushMetricsToPrometheus(List<Gauge> gauges) throws Exception {
        PushGateway pg = new PushGateway("your-prometheus-pushgateway-url");
        for (Gauge gauge : gauges) {
            StringWriter writer = new StringWriter();
            Enumeration<Collector.MetricFamilySamples> mfs = Collections.enumeration(gauge.collect());
            TextFormat.write004(writer, mfs);
            pg.pushAdd(gauge, "job_name");
        }
    }

    private String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    // MetricStreamData class
    public static class MetricStreamData {
        private String metricStreamName;
        private String accountID;
        private String region;
        private String namespace;
        private String metricName;
        private Map<String, Object> dimensions;
        private long timestamp;
        private Value value;
        private String unit;

        // Getters and setters
        public String getMetricStreamName() { return metricStreamName; }
        public void setMetricStreamName(String metricStreamName) { this.metricStreamName = metricStreamName; }

        public String getAccountID() { return accountID; }
        public void setAccountID(String accountID) { this.accountID = accountID; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getNamespace() { return namespace; }
        public void setNamespace(String namespace) { this.namespace = namespace; }

        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }

        public Map<String, Object> getDimensions() { return dimensions; }
        public void setDimensions(Map<String, Object> dimensions) { this.dimensions = dimensions; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public Value getValue() { return value; }
        public void setValue(Value value) { this.value = value; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }

    // Value class
    public static class Value {
        private double count;
        private double sum;
        private double max;
        private double min;

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public double getSum() {
            return sum;
        }

        public void setSum(double sum) {
            this.sum = sum;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }
    }

    // KinesisFirehoseResponse class
    public static class KinesisFirehoseResponse {
        private List<Record> records;

        public List<Record> getRecords() {
            return records;
        }

        public void setRecords(List<Record> records) {
            this.records = records;
        }

        public static class Record {
            private String recordId;
            private Result result;

            public String getRecordId() {
                return recordId;
            }

            public void setRecordId(String recordId) {
                this.recordId = recordId;
            }

            public Result getResult() {
                return result;
            }

            public void setResult(Result result) {
                this.result = result;
            }
        }

        public enum Result {
            Ok, Dropped, ProcessingFailed
        }
    }
}