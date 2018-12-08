package jerry.arduino;

public interface ILIfeCycleExposable {
    void startLifecycle() throws RuntimeException;
    void stopLifeCycle();
}
