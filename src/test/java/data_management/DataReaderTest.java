package data_management;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataReader;
import com.data_management.DataReaderReal;
import com.data_management.DataStorage;

public class DataReaderTest {
    @Test
    public void testReadData() {
        // TODO Implement the test for the readData method in DataReaderReal
        DataReader reader = new DataReaderReal();
        DataStorage storage = DataStorage.getInstance(reader);
        storage.readData();

        //this is a very simple test, but it should be enough to see if the data is read correctly
        assertNotEquals(0, storage.getAllPatients().size());
        
        //now we can check if a specific patient has records, this also tests the getRecords method
        assertNotEquals(0, storage.getAllPatients().get(0).getRecords(Long.MIN_VALUE, Long.MAX_VALUE).size());

    }
}
