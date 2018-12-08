package jerry.persist;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class AbstractFilePersisted<T> {
    protected Class<T> clazz;

    File toBaseDirectoryLocatedFile(String path) {
        if (!path.endsWith("/")) path += "/";
        if (!path.startsWith("/")) path = "/" + path;
        File f = new File(System.getProperty("user.home") + "/jerryHome/v1.1" + path);
        f.toPath().toFile().mkdirs();
        return f;
    }

    File parentToBaseDirectoryLocatedFile(String path) {
        if (!path.endsWith("/")) path += "/";
        if (!path.startsWith("/")) path = "/" + path;
        File f = new File(System.getProperty("user.home") + "/jerryHome/v1.1" + path);
        f.toPath().getParent().toFile().mkdirs();
        return f;
    }

    void createFile(File f) {
        try {
            Files.createFile(f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void removeFile(File f) {
        try {
            Files.delete(f.toPath());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void write(File file, T entry) {
        JAXBContext context;
        try {
            if(!clazz.isAssignableFrom(entry.getClass())){
                throw new RuntimeException("not a instance of type");
            }
            context = JAXBContext.newInstance(entry.getClass());
            Marshaller m = context.createMarshaller();
            m.marshal(entry, file);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    protected T read(File file,Class<? extends T> clazz ) throws RuntimeException {
        JAXBContext context;
        try {
            if (!Files.isReadable(file.toPath())) {
                throw new RuntimeException("could not read file " + file);
            }
            context = JAXBContext.newInstance(clazz);
            Unmarshaller m = context.createUnmarshaller();
            return ((T) m.unmarshal(file));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    protected T read(File file ) throws RuntimeException {
      return read(file,clazz);
    }

}
