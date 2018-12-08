package jerry.persist;

import com.sun.org.apache.bcel.internal.generic.InstructionComparator;

import java.util.List;

public interface ICollectionPersistable<T extends Comparable<T>> extends IPersistable<T>{
    List<T> getAvailabeEntries();
    void addEntries(List<T> entries);
    void addEntry(T entry);
    void removeEntries(List<T> entries);
    void removeEntryById(Integer id);
    void removeAllEntries();
    void updateEntries(List<T> entries);
}
