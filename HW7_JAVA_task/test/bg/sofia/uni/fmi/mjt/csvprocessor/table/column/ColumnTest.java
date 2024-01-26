package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColumnTest {
    @Test
    void addData(){
        Column val = new BaseColumn();
        String s1 = null;
        String s2 = "";
        String s3 = "name";
        assertThrows(IllegalArgumentException.class, () -> val.addData(s1));
        assertThrows(IllegalArgumentException.class, () -> val.addData(s2));
        val.addData(s3);
        assertTrue(val.getData().contains(s3));
    }

}
