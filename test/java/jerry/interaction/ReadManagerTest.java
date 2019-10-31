package jerry.interaction;


import jerry.consumer.ClientState;
import jerry.service.ClientStateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReadManagerTest {

    @Autowired
    ReadManager readManager;

    @MockBean
    ClientStateRepository clientStateRepository;



    @Test
    public void handleMessage() {
        readManager.handleMessage(StateArray.parseString("i00"));
       //verify(clientStateRepository,times(1)).updateOutputState("o000010100110");
    }

    @Test
    public void updateInputState() {
        //deviceResponseHandler.handleMessage("{i0010}");

    }
}