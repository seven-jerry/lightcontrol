package jerry.persist;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FilePersistedCollection<T extends IIdProvider&Comparable<T>> extends AbstractFilePersisted<T> implements ICollectionPersistable<T> {

    File folder;
    String fileNameTemplate;

    public FilePersistedCollection(String folder,String path,Class<T> tClass,String prefix,String potfix){
        super(folder);
        this.folder =toBaseDirectoryLocatedFile(path);
        fileNameTemplate = prefix + "{id}."+potfix;
        clazz = tClass;
   }

    @Override
    public List<T> getAvailabeEntries() {
        List<T> list = new ArrayList<>();
        if( folder.listFiles() == null) return list;

        for(File f : Objects.requireNonNull(folder.listFiles())){
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
        Integer id = this.generateNewId();
        entry.setId(id);
        String idTemplate = fileNameTemplate.replace("{id}",""+id);
        File f = Paths.get(folder.toString(),idTemplate).toFile();
        createFile(f);
        write(f,entry);
    }

    @Override
    public void removeEntries(List<T> entries) {
        entries.forEach(this::removeEntry);

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
        this.getAvailabeEntries().forEach(this::removeEntry);
    }


    private void removeEntry(T entry){
        String idTemplate = fileNameTemplate.replace("{id}",""+entry.getId());
        File f = Paths.get(folder.toString(),idTemplate).toFile();
        removeFile(f);
    }


    private Integer generateNewId(){
        Integer id = new Random().nextInt();

        for (T available : this.getAvailabeEntries()){
            if(available.getId().equals(id)){
                return this.generateNewId();
            }
        }
        return id;

    }
}
