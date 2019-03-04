package jerry.persist;

import jerry.arduino.ISerialSource;

import java.util.Optional;
import java.util.function.Consumer;

public interface IObjectPersistable<T> extends IPersistable<T> , IFolderChangeable{
    Optional<T> get();
    Optional<T> get(Class<? extends T> clazz);
    void set(T entry);
    void remove();
    void update(Consumer<T> update);
    void update(Class<? extends T> clazz,Consumer<T> update);

}
