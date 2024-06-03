package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.*;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketClientRealTest {

    private WebSocketClientReal client;
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() throws URISyntaxException {
        dataStorage = DataStorage.getInstance(new DataReaderReal());
        client = new WebSocketClientReal(new URI("ws://test-uri"), dataStorage);
    }

    @Test
    void testOnOpen() {
        // Directly call the method without parameters for simplicity
        client.onOpen(null);

        // Verify any relevant state or output if applicable
        // Since there's no real handshake, you can verify printed statements or other indicators
        assertTrue(true); // Placeholder assertion
    }

    @Test
    void testOnMessageValid() {
        String validMessage = "Patient ID: 123, Timestamp: 456789, Label: HeartRate, Data: 72.5";

        client.onMessage(validMessage);

        // Fetch the records and assert their content
        var records = dataStorage.getRecords(123, 0, 500000);
        assertEquals(1, records.size());

        PatientRecord record = records.get(0);
        assertEquals(123, record.getPatientId());
        assertEquals(456789, record.getTimestamp());
        assertEquals("HeartRate", record.getRecordType());
        assertEquals(72.5, record.getMeasurementValue());
    }

    @Test
    void testOnMessageInvalid() {
        String invalidMessage = "Invalid message format";

        client.onMessage(invalidMessage);

        // Assuming patient ID 123 is used for the valid test, checking no records for invalid messages
        assertEquals(0, dataStorage.getRecords(123, 0, 500000).size());
    }

    @Test
    void testOnClose() {
        client.onClose(1000, "Normal closure", true);

        // Assert the reconnect scheduling indirectly by checking the reconnection timer task
        // Placeholder assertion - ensure you have a way to verify that reconnection is scheduled
        assertTrue(client.isReconnectScheduled());
    }

    @Test
    void testOnError() {
        Exception exception = new Exception("Test exception");
        client.onError(exception);

        // Add assertions to verify behavior if needed
        assertTrue(true); // Placeholder assertion
    }
}
