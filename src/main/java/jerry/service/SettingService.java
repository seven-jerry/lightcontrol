package jerry.service;

import jerry.XmlModel;
import jerry.beans.Input;
import jerry.beans.Setting;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingService {
    public List<Setting> availableSettings(){
        return XmlModel.availableSettings();
    }
    public void addSetting(Setting setting){
        XmlModel.writeSetting(setting);
    }
    public void updateSetting(Setting setting){
        XmlModel.removeSetting(setting);
        XmlModel.writeSetting(setting);
    }

    public void removeSetting(Setting setting){
        XmlModel.removeInput(input->
            input.getSettingId().equals(setting.getId()));
        XmlModel.removeSetting(setting);
    }

    public Setting getSetting(String id){
        return XmlModel.get(id);
    }
    public void addInput(Input input){
        XmlModel.writeInput(input);
    }
    public void updateInput(Input input){
        XmlModel.removeInput(input);
        XmlModel.writeInput(input);

    }
    public void removeInput(Input input){
        XmlModel.removeInput(input);
        XmlModel.availableSettings();
    }
}
