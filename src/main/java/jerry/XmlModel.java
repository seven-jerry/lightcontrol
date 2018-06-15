package jerry;

import io.swagger.models.auth.In;
import jerry.beans.Input;
import jerry.beans.Setting;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class XmlModel {

    private static File settingfolder=new File(System.getProperty("user.home")+"/jerryHome/setting/");
    private static int settingcount;
    private static List<Setting> avaiableSettings;

    private static File inputfolder=new File(System.getProperty("use.home")+"/jerryHome/input/");
    private static int inputcount;
    private static List<Input> avaiableInputs;

    static {
        settingfolder.mkdirs();
        inputfolder.mkdirs();
    }

    public static List<Setting> availableSettings(){
        settingcount = 0;
        avaiableSettings = new ArrayList<>();
        avaiableInputs();

        for(File file :settingfolder.listFiles()){
            Setting setting = readSetting(file);
            if(setting != null){
                settingcount++;

                avaiableSettings.add(setting);
            }
        }

        for(Setting s : avaiableSettings){
            for(Input i:avaiableInputs){
                if(i.getSettingId().equals(s.getId())){
                    s.addInput(i);
                }
            }
        }
        return avaiableSettings;
    }

    private static List<Input> avaiableInputs(){
        inputcount = 0;
        avaiableInputs = new ArrayList<>();

        for(File file :inputfolder.listFiles()){
            Input input = readInput(file);
            if(input != null){
                inputcount++;
                avaiableInputs.add(input);
            }
        }
        return avaiableInputs;
    }

    public static Setting readSetting(File file){
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Setting.class);
            Unmarshaller m = context.createUnmarshaller();
            Setting set =(Setting)m.unmarshal(file);

            return set;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeSetting(Setting setting){

        availableSettings();
        if(setting.getId() == null || setting.getId().isEmpty()){
            setting.setId(""+settingcount);
        }

        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Setting.class);
            Marshaller m = context.createMarshaller();
            m.marshal(setting,new File(settingfolder.getPath()+"/setting-"+setting.getId()+".xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        availableSettings();
    }

    public static Setting get(String id){
        availableSettings();
        for(Setting i:avaiableSettings){
            if(i.getId().equals(id)){
                return i;
            }
        }
        return null;
    }




    private static Input readInput(File file){
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Input.class);
            Unmarshaller m = context.createUnmarshaller();
            Input set =(Input)m.unmarshal(file);

            return set;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeInput(Input input){
        availableSettings();
        if(input.getId() == null || input.getId().isEmpty()){
            input.setId(""+inputcount);
        }
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Input.class);
            Marshaller m = context.createMarshaller();
            m.marshal(input,new File(inputfolder.getPath()+"/input-"+input.getId()+".xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        availableSettings();
    }

    public static void removeInput(Input source) {
        removeInput(input -> input.getId().equals(source.getId()));
    }

    public static void removeInput(Predicate<Input> predicate) {
        for(File file :inputfolder.listFiles()){
            Input input = readInput(file);
            if(input != null && predicate.test(input)){
                file.delete();
            }
        }
        availableSettings();
    }

    public static void removeSetting(Setting source) {
        for(File file :settingfolder.listFiles()){
            Setting setting = readSetting(file);
            if(setting != null && setting.getId().equals(source.getId())){
                file.delete();
                availableSettings();
                return;
            }
        }

    }
}
