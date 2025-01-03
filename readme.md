
# AWS Lambda Handler for Kinesis Firehose Events

This repository contains an AWS Lambda function written in Java that processes Kinesis Firehose events. The function extracts metrics from the event records and pushes them to a Prometheus PushGateway.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Setup and Deployment](#setup-and-deployment)
- [Configuration](#configuration)
- [Usage](#usage)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Overview

This Lambda function processes Kinesis Firehose events containing metric data. For each record, it extracts metrics, creates Prometheus gauge metrics, and pushes these metrics to a Prometheus PushGateway.

## Architecture

The main components of the Lambda function are:

- **LambdaHandler**: The main handler class implementing `RequestHandler<KinesisFirehoseEvent, KinesisFirehoseResponse>`.
- **MetricStreamData**: A class representing the metric data extracted from the Kinesis Firehose event records.
- **Value**: A class representing the metric values.
- **KinesisFirehoseResponse**: A class representing the response to be sent back to Kinesis Firehose.

## Setup and Deployment

### Prerequisites

- Java 8 or higher
- AWS CLI configured with appropriate permissions
- AWS Lambda execution role with access to Kinesis Firehose and CloudWatch Logs
- Prometheus PushGateway URL

### Building the Project

1. Clone the repository:

   ```sh
   git clone https://github.com/your-repo/aws-lambda-handler.git
   cd aws-lambda-handler
   ```
2. Build the project using Maven:

   ```sh
   mvn clean package
   ```
3. The output JAR file will be located in the `target` directory.

### Deploying the Lambda Function

1. Create a new Lambda function in the AWS Management Console.
2. Upload the JAR file from the `target` directory.
3. Set the handler to `com.example.LambdaHandler::handleRequest`.
4. Configure the Lambda function with the necessary environment variables (e.g., Prometheus PushGateway URL).
5. Set up a Kinesis Firehose delivery stream to trigger the Lambda function.

## Configuration

### Environment Variables

- `PROMETHEUS_PUSHGATEWAY_URL`: The URL of the Prometheus PushGateway.

### IAM Permissions

Ensure the Lambda execution role has the necessary permissions to access Kinesis Firehose and CloudWatch Logs.

## Usage

The Lambda function will automatically process incoming Kinesis Firehose events, extract metrics, and push them to the Prometheus PushGateway. The metrics include:

- Count
- Sum
- Max
- Min

### Metric Naming Convention

Metrics are named using the format `<metric_name>_<metric_type>`, where `metric_type` can be `count`, `sum`, `max`, or `min`.

## Testing

### Unit Tests

Unit tests can be run using Maven:

```sh
mvn test
```
