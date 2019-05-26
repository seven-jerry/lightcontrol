package test;

import jerry.interaction.ReadManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ReadManagerTest {


    @Autowired
    private ReadManager manager;


    @Test
    public void initTest(){
       // manager.handleMessage(new StateArray(1,0,0));
    }
}
