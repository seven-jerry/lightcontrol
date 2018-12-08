package jerry.persist;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePersistedCollection<T extends IIdProvider&Comparable<T>> extends AbstractFilePersisted<T> implements ICollectionPersistable<T> {

    File folder;
    String fileNameTemplate;

    public FilePersistedCollection(String path,Class<T> tClass,String prefix,String potfix){
        folder =toBaseDirectoryLocatedFile(path);
        fileNameTemplate = prefix + "{id}."+potfix;
        clazz = tClass;
   }

    @Override
    public List<T> getAvailabeEntries() {
        List<T> list = new ArrayList<>();
        for(File f : folder.listFiles()){
            if (f.isHidden())continue;
            if(!Files.isRegularFile(f.toPath())) continue;
            list.add(read(f));
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public void addEntries(List<T> entries) {
        entries.forEach(this::addEntry);
    }

    @Override
    public void addEntry(T entry) {
        Integer id = getAvailabeEntries().size();
        entry.setId(id);
        String idTemplate = fileNameTemplate.replace("{id}",""+id);
        File f = Paths.get(folder.toString(),idTemplate).toFile();
        createFile(f);
        write(f,entry);
    }

    @Override
    public void removeEntries(List<T> entries) {

    }

    @Override
    public void removeEntryById(Integer id) {
            for(T entry : getAvailabeEntries()  ){
                if (entry.getId().equals(id)) {
                    removeEntry(entry);
                }
            }
    }


    @Override
    public void removeAllEntries() {

    }

    @Override
    public void updateEntries(List<T> entries) {

    }

    private void removeEntry(T entry){
        String idTemplate = fileNameTemplate.replace("{id}",""+entry.getId());
        File f = Paths.get(folder.toString(),idTemplate).toFile();
        removeFile(f);
    }
}
