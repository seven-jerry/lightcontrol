package jerry.interaction;


import org.junit.Test;
import jerry.pojo.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class InputCommandTest {


    @Test
    public void switch_low_motion_high() {


        InputCommand c = InputCommand.MOTION_HIGH_MANUAL_LOW;

        Input manualSwitch = new Input(InputType.MANUAL, "switch", "0");
        Input motion = new Input(InputType.MANUAL, "motion", "1");


        List<Input> inputs = Arrays.asList(manualSwitch, motion);
        Optional<String> stateCommand = c.testCondition(StateArray.parseString("{i0010}"), InputControl.ISLAND_MODE, inputs);
        assertTrue(stateCommand.isPresent());
        assertEquals("sal", stateCommand.get());
       stateCommand = c.testCondition(StateArray.parseString("{i0110}"), InputControl.ISLAND_MODE, inputs);
        assertTrue(stateCommand.isPresent());
        assertEquals("smh", stateCommand.get());

        stateCommand = c.testCondition(StateArray.parseString("{i0011}"), InputControl.ISLAND_MODE, inputs);
        assertTrue(stateCommand.isPresent());
        assertEquals("smh", stateCommand.get());

        stateCommand = c.testCondition(StateArray.parseString("{i0010}"), InputControl.ISLAND_MODE, inputs);
        assertTrue(stateCommand.isPresent());
        assertEquals("sal", stateCommand.get());
    }
}
