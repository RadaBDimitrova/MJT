package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public class AmazonAlexa extends IoTDeviceBase {
    private final String alexaID;
    private final DeviceType type;

    public AmazonAlexa(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        type = DeviceType.SMART_SPEAKER;
        uniqueNumberDevice++;
        super.setDeviceID(uniqueNumberDevice);
        alexaID = type.getShortName() + '-' + name + '-' + uniqueNumberDevice;
    }

    @Override
    public DeviceType getType() {
        return type;
    }

    @Override
    public String getId() {
        return alexaID;
    }

}
