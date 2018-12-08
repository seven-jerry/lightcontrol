package jerry.arduino;


import java.util.function.Supplier;


public enum SerialSources {

    ARDUINO(Arduino::new), MOCK(MockArduino::getSpringBean);


    private Supplier<ISerialSource> serialSourceSupplier;



    SerialSources(Supplier<ISerialSource> supplier) {
        this.serialSourceSupplier = supplier;
    }

    public ISerialSource getSource() {
        return serialSourceSupplier.get();
    }

}
