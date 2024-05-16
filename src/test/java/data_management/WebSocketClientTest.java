package data_management;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WebSocketClientRealTest {
    
    private WebSocketClientReal client;
    private MockDataStorage mockDataStorage;

    @BeforeEach
    void setUp() throws URISyntaxException {
        mockDataStorage = new MockDataStorage(null);
        client = new WebSocketClientReal(new URI("ws://test-uri"), mockDataStorage);
    }

    @Test
    void testOnOpen(ServerHandshake handshake) {
        client.onOpen(handshake);

    }

    @Test
    void testOnMessageValid() {
        String validMessage = "Patient ID: 123, Timestamp: 456789, Label: HeartRate, Data: 72.5";

        client.onMessage(validMessage);

        assertEquals(1, mockDataStorage.receivedMessages.size());
        assertEquals(validMessage, mockDataStorage.receivedMessages.get(0));
    }

    @Test
    void testOnMessageInvalid() {
        String invalidMessage = "Invalid message format";

        client.onMessage(invalidMessage);

        assertEquals(0, mockDataStorage.receivedMessages.size());
    }

    @Test
    void testOnClose() {
        client.onClose(1000, "Normal closure", true);
        
        // To verify that reconnect is scheduled, check if reconnect was attempted
        assertTrue(client.isClosing());
    }

    @Test
    void testOnError() {
        Exception exception = new Exception("Test exception");
        client.onError(exception);

        // Add assertions to verify behavior if needed
    }

    private static class MockDataStorage extends DataStorage {
        public MockDataStorage(DataReader reader) {
            super(reader);
        }
        List<String> receivedMessages = new ArrayList<>();
    }

    class MockDataReader implements DataReader {
        @Override
        public void readData(DataStorage dataStorage) {
            // No-op
        }

        @Override
        public void readLine(String line, DataStorage dataStorage) {
            ((MockDataStorage) dataStorage).receivedMessages.add(line);
        }
    }
}
