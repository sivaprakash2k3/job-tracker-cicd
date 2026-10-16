package com.siva.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

@Repository
@Profile("aws")
public class DynamoJobRepository implements JobRepository {
    private static final String TABLE = "JobApplications";
    private final DynamoDbClient client =
            DynamoDbClient.builder().region(Region.AP_SOUTH_1).build();

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
        client.scanPaginator(ScanRequest.builder().tableName(TABLE).build())
                .items()
                .forEach(i -> result.add(new JobApplication(
                        str(i, "id"), str(i, "company"), str(i, "role"), str(i, "status"))));
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