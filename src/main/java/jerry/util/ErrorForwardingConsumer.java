package jerry.util;


@FunctionalInterface
public interface ErrorForwardingConsumer<T>{
    void accept(T obj) throws Exception;
}
