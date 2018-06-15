package jerry.arduino;

import jerry.beans.KeyValuePair;

public interface ReadCallback {
    void contentFromArduino(KeyValuePair content);
}
