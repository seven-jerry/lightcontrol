package jerry.interaction;

public interface IReadUpdateable {
    void handleMessage(String message);
    void handleError(String message);
}
