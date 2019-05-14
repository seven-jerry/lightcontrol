package jerry.interaction;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MasterInteractionManager extends AbstractInteractionManager {


    public MasterInteractionManager() {
        System.out.println("master");
    }


    @Override
    protected void initLifeCycle() {

    }

    @Override
    protected void onTryAutoStart() {

    }

    @Override
    protected void onStart() throws Exception {
        System.out.println("master");
    }
}
