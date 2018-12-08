package jerry.persist;

import jerry.arduino.ISerialSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Consumer;

public class FilePersistedObject<T> extends AbstractFilePersisted<T> implements IObjectPersistable<T> {
    protected File file;
    public FilePersistedObject(String filePath,Class<T> tClass) {
        file = parentToBaseDirectoryLocatedFile(filePath);
        this.clazz = tClass;
    }

    public FilePersistedObject() {
    }

    @Override
    public Optional<T> get() {
        try {
            T readObject = read(file);
            return Optional.ofNullable(readObject);
        }catch (RuntimeException e){
            System.out.println("FielPerssistedObject Exception : "+e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> get(Class<? extends T> clazz) {
        try {
            T readObject = read(file,clazz);
            return Optional.ofNullable(readObject);
        }catch (RuntimeException e){
            System.out.println("FielPerssistedObject Exception : "+e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void set(T entry) {
            write(file,entry);
    }

    @Override
    public void remove() {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Consumer<T> update) {
        T entry = this.get().orElse(null);
        if (entry == null) return;
        update.accept(entry);
        set(entry);
    }

    @Override
    public void update(Class<? extends T> clazz, Consumer<T> update) {
        T entry = this.get(clazz).orElse(null);
        if (entry == null) return;
        update.accept(entry);
        set(entry);
    }


}
