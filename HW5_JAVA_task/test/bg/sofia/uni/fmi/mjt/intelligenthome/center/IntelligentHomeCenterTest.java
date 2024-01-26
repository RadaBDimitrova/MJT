package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.MapDeviceStorage;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IntelligentHomeCenterTest {
    @Test
    void testRegisterNull() {
        DeviceStorage st = mock();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        IoTDevice device = null;
        assertThrows(IllegalArgumentException.class, () -> it.register(device));
    }

    @Test
    void testRegisterExists() {
        DeviceStorage st = new MapDeviceStorage();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        IoTDevice device = new RgbBulb("a", 60, LocalDateTime.now().minusHours(3));
        device.setRegistration(LocalDateTime.now());
        st.store(device.getId(), device);
        assertThrows(DeviceAlreadyRegisteredException.class, () -> it.register(device));
    }

    @Test
    void testUnregisterWhenEmpty() {
        DeviceStorage st = new MapDeviceStorage();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        IoTDevice device = new RgbBulb("a", 60, LocalDateTime.now().minusHours(3));
        assertThrows(DeviceNotFoundException.class, () -> it.unregister(device));
    }

    @Test
    void testUnregisterNull() {
        DeviceStorage st = mock();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        IoTDevice device = null;
        assertThrows(IllegalArgumentException.class, () -> it.unregister(device));
    }

    @Test
    void testGetDeviceByNullId() {
        DeviceStorage st = mock();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        assertThrows(IllegalArgumentException.class, () -> it.getDeviceById(null));
    }

    @Test
    void testGetDeviceWhenEmpty() {
        DeviceStorage st = new MapDeviceStorage();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        IoTDevice device = new RgbBulb("a", 60, LocalDateTime.now().minusHours(3));
        assertThrows(DeviceNotFoundException.class, () -> it.getDeviceById(device.getId()));
    }

    @Test
    void testGetDeviceQuantityByNullType() {
        DeviceStorage st = mock();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        assertThrows(IllegalArgumentException.class, () -> it.getDeviceQuantityPerType(null));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionZero() {
        DeviceStorage st = mock();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        assertThrows(IllegalArgumentException.class, () -> it.getTopNDevicesByPowerConsumption(0));
    }

    @Test
    void testGetFirstNDevicesByRegistrationZero() {
        DeviceStorage st = mock();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        assertThrows(IllegalArgumentException.class, () -> it.getFirstNDevicesByRegistration(0));
    }

    @Test
    void testRegister() {
        DeviceStorage st = new MapDeviceStorage();

        IoTDevice device = new RgbBulb("a", 60, LocalDateTime.now().minusHours(3));
        device.setRegistration(LocalDateTime.now());
        st.store(device.getId(), device);
        assertEquals(device, st.get(device.getId()));
    }

    @Test
    void testUnregister() {
        DeviceStorage st = new MapDeviceStorage();
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        IoTDevice device = new RgbBulb("a", 60, LocalDateTime.now().minusHours(3));
        device.setRegistration(LocalDateTime.now());
        st.store(device.getId(), device);
        st.delete(device.getId());
        assertThrows(DeviceNotFoundException.class, () -> it.getDeviceById(device.getId()));
    }

    @Test
    void testGetDeviceQuantityPerTypeRgbBulb() {
        DeviceStorage st = new MapDeviceStorage();
        for (int i = 0; i < 5; i++) {
            IoTDevice device = new RgbBulb("a", i, LocalDateTime.now().minusHours(3));
            device.setRegistration(LocalDateTime.now());
            st.store(device.getId(), device);
        }
        IntelligentHomeCenter it = new IntelligentHomeCenter(st);
        assertEquals(5, it.getDeviceQuantityPerType(DeviceType.BULB));
    }

    @Test
    void testGetTopNDevicesByPowerConsumption() {
        DeviceStorage st = new MapDeviceStorage();
        List<IoTDevice> devices = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            IoTDevice device = new AmazonAlexa("a" + i, i, LocalDateTime.now().minusHours(3));
            device.setRegistration(LocalDateTime.now());
            st.store(device.getId(), device);
            devices.add(device);
        }

        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        Collection<String> topDevices = it.getTopNDevicesByPowerConsumption(5);

        List<String> expectedTopDevices = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            expectedTopDevices.add(devices.get(i).getId());
        }
        assertEquals(expectedTopDevices, new ArrayList<>(topDevices));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionMore() {
        int n = 15;
        DeviceStorage st = new MapDeviceStorage();
        List<IoTDevice> devices = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            IoTDevice device = new AmazonAlexa("a" + i, i, LocalDateTime.now().minusHours(3));
            device.setRegistration(LocalDateTime.now());
            st.store(device.getId(), device);
            devices.add(device);
        }

        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        Collection<String> topDevices = it.getTopNDevicesByPowerConsumption(n);

        List<String> expectedTopDevices = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            expectedTopDevices.add(devices.get(i).getId());
        }
        assertEquals(expectedTopDevices, new ArrayList<>(topDevices));
    }

    @Test
    void testGetFirstNDevicesByRegistration() {
        DeviceStorage st = new MapDeviceStorage();
        List<IoTDevice> devices = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            IoTDevice device = new AmazonAlexa("a" + i, 5, LocalDateTime.now().minusHours(3));
            device.setRegistration(LocalDateTime.now().minusHours(i));
            st.store(device.getId(), device);
            devices.add(device);
        }

        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        Collection<IoTDevice> topDevices = it.getFirstNDevicesByRegistration(3);

        List<IoTDevice> expectedTopDevices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            expectedTopDevices.add(devices.get(i));
        }
        assertEquals(expectedTopDevices, new ArrayList<>(topDevices));
    }

    @Test
    void testGetFirstNDevicesByRegistrationMore() {
        int n = 10;
        DeviceStorage st = new MapDeviceStorage();
        List<IoTDevice> devices = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            IoTDevice device = new AmazonAlexa("a" + i, 5, LocalDateTime.now().minusHours(3));
            device.setRegistration(LocalDateTime.now().minusHours(i));
            st.store(device.getId(), device);
            devices.add(device);
        }

        IntelligentHomeCenter it = new IntelligentHomeCenter(st);

        Collection<IoTDevice> topDevices = it.getFirstNDevicesByRegistration(n);

        List<IoTDevice> expectedTopDevices = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            expectedTopDevices.add(devices.get(i));
        }
        assertEquals(expectedTopDevices, new ArrayList<>(topDevices));
    }

    @Test
    void testGetDeviceByIdWhenDeviceExists() throws DeviceNotFoundException {
        DeviceStorage storageMock = mock();

        IntelligentHomeCenter test = new IntelligentHomeCenter(storageMock);

        when(storageMock.exists("id")).thenReturn(true);

        IoTDevice expectedDevice = mock(IoTDevice.class);
        when(storageMock.get("id")).thenReturn(expectedDevice);

        IoTDevice result = test.getDeviceById("id");

        verify(storageMock).exists("id");
        verify(storageMock).get("id");
        assertEquals(expectedDevice, result);
    }

    @Test
    void testRegisterDeviceByIdWhenDeviceDoesntExist() throws DeviceNotFoundException, DeviceAlreadyRegisteredException {
        DeviceStorage storage = mock();
        IntelligentHomeCenter test = new IntelligentHomeCenter(storage);

        IoTDevice device = mock();
        test.register(device);

    }

    @Test
    void testUnregisterDevice() throws DeviceNotFoundException, DeviceAlreadyRegisteredException {
        DeviceStorage st = new MapDeviceStorage();
        IntelligentHomeCenter test = new IntelligentHomeCenter(st);
        IoTDevice device = new RgbBulb("a", 60, LocalDateTime.now().minusHours(3));

        test.register(device);
        test.unregister(device);

    }
}
