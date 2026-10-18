package com.siva.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

class DynamoJobRepositoryTest {

    @Test
    void saveWritesItemToTable() {
        DynamoDbClient client = mock(DynamoDbClient.class);
        DynamoJobRepository repo = new DynamoJobRepository(client);

        JobApplication saved = repo.save(new JobApplication(null, "Acme", "DevOps Engineer", "APPLIED"));

        assertNotNull(saved.id());
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        assertEquals("JobApplications", captor.getValue().tableName());
        assertEquals("Acme", captor.getValue().item().get("company").s());
        assertEquals(saved.id(), captor.getValue().item().get("id").s());
    }

    @Test
    void saveHandlesMissingFields() {
        DynamoDbClient client = mock(DynamoDbClient.class);
        DynamoJobRepository repo = new DynamoJobRepository(client);

        JobApplication saved = repo.save(new JobApplication(null, "Acme", null, null));

        assertNotNull(saved.id());
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        assertEquals("", captor.getValue().item().get("role").s());
    }

    @Test
    void findAllMapsItemsAndHandlesMissingAttributes() {
        DynamoDbClient client = mock(DynamoDbClient.class);
        Map<String, AttributeValue> full = Map.of(
                "id", AttributeValue.fromS("1"),
                "company", AttributeValue.fromS("Acme"),
                "role", AttributeValue.fromS("DevOps Engineer"),
                "status", AttributeValue.fromS("APPLIED"));
        Map<String, AttributeValue> partial = Map.of("id", AttributeValue.fromS("2"));
        when(client.scan(any(ScanRequest.class)))
                .thenReturn(ScanResponse.builder().items(List.of(full, partial)).build());

        List<JobApplication> result = new DynamoJobRepository(client).findAll();

        assertEquals(2, result.size());
        assertEquals("Acme", result.get(0).company());
        assertEquals("", result.get(1).company());
    }

    @Test
    void findAllFollowsPagination() {
        DynamoDbClient client = mock(DynamoDbClient.class);
        Map<String, AttributeValue> key = Map.of("id", AttributeValue.fromS("1"));
        Map<String, AttributeValue> first = Map.of("id", AttributeValue.fromS("1"));
        Map<String, AttributeValue> second = Map.of("id", AttributeValue.fromS("2"));
        when(client.scan(any(ScanRequest.class)))
                .thenReturn(ScanResponse.builder().items(List.of(first)).lastEvaluatedKey(key).build())
                .thenReturn(ScanResponse.builder().items(List.of(second)).build());

        List<JobApplication> result = new DynamoJobRepository(client).findAll();

        assertEquals(2, result.size());
    }
}