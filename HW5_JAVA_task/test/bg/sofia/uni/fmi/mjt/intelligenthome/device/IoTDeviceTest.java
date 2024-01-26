package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IoTDeviceTest {

    @Test
    void testGetPowerConsumptionKWhZeroRgbBulb() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusDays(1);
        IoTDevice dev = new RgbBulb("test", 0, installationDateTime);

        long powerConsumptionKWh = dev.getPowerConsumptionKWh();

        assertEquals(0, powerConsumptionKWh);
    }

    @Test
    void testGetPowerConsumptionKWhRgbBulb() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusDays(1);
        IoTDevice dev = new RgbBulb("test", 75, installationDateTime);

        long powerConsumptionKWh = dev.getPowerConsumptionKWh();

        assertEquals(1800, powerConsumptionKWh);

    }

    @Test
    void testGetRegistrationDurationRgbBulb() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusHours(1);
        IoTDevice dev = new RgbBulb("test", 75, installationDateTime);
        dev.setRegistration(installationDateTime);
        long duration = dev.getRegistration();
        assertEquals(1, duration);
    }

    @Test
    void testGetPowerConsumptionKWhAmazonAlexa() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusHours(5);
        IoTDevice dev = new AmazonAlexa("test", 45, installationDateTime);

        long powerConsumptionKWh = dev.getPowerConsumptionKWh();

        assertEquals(225, powerConsumptionKWh);

    }

    @Test
    void testGetRegistrationDurationAmazonAlexa() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusHours(3);
        IoTDevice dev = new AmazonAlexa("test", 50, installationDateTime);
        dev.setRegistration(installationDateTime.plusHours(2));
        long duration = dev.getRegistration();
        assertEquals(1, duration);
    }

    @Test
    void testGetPowerConsumptionKWhThermostat() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusHours(3);
        IoTDevice dev = new WiFiThermostat("test", 20, installationDateTime);

        long powerConsumptionKWh = dev.getPowerConsumptionKWh();

        assertEquals(60, powerConsumptionKWh);

    }

    @Test
    void testGetRegistrationDurationThermostat() {
        LocalDateTime installationDateTime = LocalDateTime.now().minusDays(1);
        IoTDevice dev = new WiFiThermostat("test", 50, installationDateTime);
        dev.setRegistration(installationDateTime.plusHours(2));
        long duration = dev.getRegistration();
        assertEquals(22, duration);
    }

}

