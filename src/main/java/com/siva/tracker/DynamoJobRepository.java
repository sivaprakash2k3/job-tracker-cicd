package com.siva.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@Repository
@Profile("aws")
public class DynamoJobRepository implements JobRepository {
    private static final String TABLE = "JobApplications";
    private final DynamoDbClient client;

    public DynamoJobRepository(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public JobApplication save(JobApplication job) {
        String id = UUID.randomUUID().toString();
        Map<String, AttributeValue> item = Map.of(
                "id", AttributeValue.fromS(id),
                "company", AttributeValue.fromS(nz(job.company())),
                "role", AttributeValue.fromS(nz(job.role())),
                "status", AttributeValue.fromS(nz(job.status())));
        client.putItem(PutItemRequest.builder().tableName(TABLE).item(item).build());
        return new JobApplication(id, job.company(), job.role(), job.status());
    }

    @Override
    public List<JobApplication> findAll() {
        List<JobApplication> result = new ArrayList<>();
        Map<String, AttributeValue> lastKey = null;
        do {
            ScanRequest.Builder request = ScanRequest.builder().tableName(TABLE);
            if (lastKey != null) {
                request.exclusiveStartKey(lastKey);
            }
            ScanResponse response = client.scan(request.build());
            for (Map<String, AttributeValue> item : response.items()) {
                result.add(new JobApplication(
                        str(item, "id"), str(item, "company"), str(item, "role"), str(item, "status")));
            }
            lastKey = response.hasLastEvaluatedKey() ? response.lastEvaluatedKey() : null;
        } while (lastKey != null);
        return result;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static String str(Map<String, AttributeValue> item, String key) {
        AttributeValue v = item.get(key);
        return v == null ? "" : v.s();
    }
}