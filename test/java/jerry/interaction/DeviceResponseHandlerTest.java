package jerry.interaction;


import jerry.consumer.ClientState;
import jerry.service.ClientStateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DeviceResponseHandlerTest {

    @MockBean
    ClientStateRepository clientStateRepository;


    @Autowired
    DeviceResponseHandler deviceResponseHandler;

    @MockBean
    ReadManager readManager;


    @Test
    public void handleMessage() {
        when(clientStateRepository.getState()).thenReturn(ClientState.withSize(2,5,2,2));
        deviceResponseHandler.handleMessage("{o000010020030040100110120130147200210220230240u0010i0010a0:130,1:150}}");
        verify(clientStateRepository,times(1)).updateOutputState("o000010020030040100110120130147200210220230240");
    }

    @Test
    public void updateInputState() {
        ClientState state = ClientState.withSize(0,0,2,0);
        when(clientStateRepository.updateInputState(anyString())).thenReturn(true);

        when(clientStateRepository.getState()).thenReturn(state);
       // verify(readManager,times(3)).handleMessage(state.getState());

        //deviceResponseHandler.handleMessage("{i0110}");
       // deviceResponseHandler.handleMessage("{i0010}");
      //  deviceResponseHandler.handleMessage("{i0110}");

    }
}
