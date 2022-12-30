package jerry.interaction;

import jerry.device.ISerialSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WriteThreadTest {

    @Test
    void givenStringCommand__whenProcessed__sourceReceivesCorrectCommand() {
        ISerialSource mock = mock(ISerialSource.class);
        WriteThread DUT = new WriteThread(mock);
        DUT.sendToSource("sal");
        verify(mock).write("{sal}");
    }

    @Test
    void givenSingleCommands__whenProcessed__sourceReceivesCorrectCommand() {
        ISerialSource mock = mock(ISerialSource.class);
        WriteThread DUT = new WriteThread(mock);
        DUT.sendToSource("001220330440550660770880990110220330440");
        verify(mock).write("{001220330440550660770880990110220330440}");
    }

    @Test
    void givenLongSingleCommands__whenProcessed__sourceReceivesCorrectCommandInChunkedMode() {
        ISerialSource mock = mock(ISerialSource.class);
        WriteThread DUT = new WriteThread(mock);
        DUT.sendToSource("123456789123456789123456789123456789123456789123456789");
        verify(mock).write("{123456789123456789123456789123456789123}");
        verify(mock).write("{456789123456789}");
    }


    @Test
    void givenFaultyCommands__whenProcessed__sourceReceivesCorrectCommandInChunkedMode() {
        ISerialSource mock = mock(ISerialSource.class);
        WriteThread DUT = new WriteThread(mock);
        DUT.sendToSource("12");
        DUT.sendToSource("1244");
        DUT.sendToSource("sa");
        verifyNoMoreInteractions(mock);
    }
}
