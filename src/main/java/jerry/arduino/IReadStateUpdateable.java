package jerry.arduino;

public interface IReadStateUpdateable {
    void updateOutputWithString(String state);
    void updateInputWithString(String state);
    void handleError(String message);
}
