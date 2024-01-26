package bg.sofia.uni.fmi.mjt.intelligenthome.storage;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class StorageTest {
    @Test
    void testStore() {
        MapDeviceStorage storage = new MapDeviceStorage();
        IoTDevice device = mock();

        assertNull(storage.store("deviceId", device));
        assertEquals(device, storage.store("deviceId", device));

        assertTrue(storage.exists("deviceId"));
    }

    @Test
    void testDelete() {
        MapDeviceStorage storage = new MapDeviceStorage();
        assertFalse(storage.delete("id"));

        IoTDevice device = mock();
        storage.store("deviceId", device);

        assertTrue(storage.delete("deviceId"));
        assertFalse(storage.exists("deviceId"));
    }

}
