package jerry.util;

import java.util.function.Predicate;

public class ErrrorHandling {

    public static<T> void throwIf(T object, Predicate<T> test){
        if(test.test(object)){
            throw  new IllegalArgumentException("ErrorHandling.throwIf. dod not fit expected criteria");
        }
    }
}
