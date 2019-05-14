package jerry.device;


import java.util.function.Supplier;


public enum SerialSources {

    ARDUINO(SerialDevice::new),
    MOCK(MockSerialDevice::new);

    private Supplier<ISerialSource> serialSourceSupplier;
    SerialSources(Supplier<ISerialSource> supplier) {
        this.serialSourceSupplier = supplier;
    }
    public ISerialSource getSource() {
        return serialSourceSupplier.get();
    }

}
