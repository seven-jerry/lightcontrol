package jerry.arduino;


import java.util.function.Predicate;

public interface ThriPredicate<T,U,V> {
    boolean test(T t,U u,V v);
}
