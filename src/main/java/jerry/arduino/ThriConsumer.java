package jerry.arduino;


import java.util.function.Consumer;

public interface ThriConsumer<T,U,V> {
    void accept(T t, U u, V v);
}
