package jerry.interaction;


import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class NodeRedSensorState {
    public final Object state;
    public Map<String,Object> attributes = new HashMap<>();

    private transient String friendlyName;
    NodeRedSensorState(Object state,String unitOfMeasure,String friendlyName){
        this.state = state;
        if(unitOfMeasure == null){
            this.attributes.put("unit_of_measurement",unitOfMeasure);
        }
        this.attributes.put("friendly_name",friendlyName);
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName(){
        return friendlyName;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
