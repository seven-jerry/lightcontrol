package jerry.util;


public interface ThriConsumer<T,U,V> {
    void accept(T t, U u, V v);
}
