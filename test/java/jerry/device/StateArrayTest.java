package jerry.device;
import jerry.interaction.StateArray;
import org.junit.Test;

import static org.junit.Assert.*;

public class StateArrayTest {
        private final String ALL_HIGH  = "001"+"011"+"101"+"111";
    private final String SINGLE_ALL_HIGH  = "01"+"11";

  @Test
  public void outputStateString(){
      StateArray array = StateArray.empty(2,2,1,1);
      assertEquals("{o"+"000"+"010"+"100"+"110"+"}",array.outputStateString(true));
  }


    @Test
    public void inputStateString(){
        StateArray array = StateArray.empty(0,0,2,0);
        assertEquals("{i"+"00"+"10"+"}",array.inputStateString(true));
    }

    @Test
    public void outSideStateString(){
        StateArray array = StateArray.empty(0,0,0,2);
        assertEquals("{u"+"00"+"10"+"}",array.outSideStateString(true));
    }

    @Test
    public void toStingTest(){
        StateArray array = StateArray.empty(2,2,2,2);
        assertEquals("{o"+"000"+"010"+"100"+"110"+"i0010"+"u0010"+"}",array.toString());
        assertTrue(array.toString().contains(array.outputStateString(false)));
        assertTrue(array.toString().contains(array.inputStateString(false)));
        assertTrue(array.toString().contains(array.outSideStateString(false)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseStringDoubleCurlyBraceTest() {
        StateArray.parseString("a{aa{as");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseStringDoubleCurlyBraceEndTest() {
        StateArray.parseString("asd}content}");
    }

    @Test
    public void parseStringTest(){
      StateArray array = StateArray.parseString("{o000"+"010"+"100"+"110"+"i0010"+"u0010}");
      assertEquals("{o000"+"010"+"100"+"110"+"i0010"+"u0010}",array.toString());
    }


    @Test
    public void updateOutputStateTest(){
        StateArray array = StateArray.empty(2,2,2,2);
        array.updateOutputState(ALL_HIGH);
        assertEquals(StateArray.OUTPUT_CHAR+ALL_HIGH,array.outputStateString(false));
    }

    @Test
    public void updateInputStateTest(){
        StateArray array = StateArray.empty(0,0,2,0);
        array.updateInputState(SINGLE_ALL_HIGH);
        assertEquals(StateArray.INPUT_CHAR+SINGLE_ALL_HIGH,array.inputStateString(false));
    }

    @Test
    public void updateOutsideTest(){
        StateArray array = StateArray.empty(0,0,0,2);
        array.updateOutsideState(SINGLE_ALL_HIGH);
        assertEquals(StateArray.OUTSIDE_CHAR+SINGLE_ALL_HIGH,array.outSideStateString(false));
    }
}
