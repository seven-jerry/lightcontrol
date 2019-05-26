package jerry.persist;


import java.util.List;

public interface ICollectionPersistable<T extends Comparable<T>> extends IPersistable<T>,IFolderChangeable{
    List<T> getAvailabeEntries();
    void addEntries(List<T> entries);
    void addEntry(T entry);
    void removeEntries(List<T> entries);
    void removeEntryById(Integer id);
    void removeAllEntries();
}
