package jerry.arduino;

public interface IReadUpdateable {
    void handleMessage(StateArray message);
    void handleError(String message);
}
