package jerry.beans;

import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement

public class Setting {
    private String id;
    private String serialport;
    private String baundRate;
    private Integer outputRow;
    private Integer outputColumn;

    private transient ArrayList<Input> inputs =  new ArrayList<Input>();



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerialport() {
        return serialport;
    }

    public void setSerialport(String serialport) {
        this.serialport = serialport;
    }

    public String getBaundRate() {
        return baundRate;
    }

    public void setBaundRate(String baundRate) {
        this.baundRate = baundRate;
    }

    public Integer getOutputRow() {
        return outputRow;
    }

    public void setOutputRow(Integer outputRow) {
        this.outputRow = outputRow;
    }

    public Integer getOutputColumn() {
        return outputColumn;
    }

    public void setOutputColumn(Integer outputColumn) {
        this.outputColumn = outputColumn;
    }

    public ArrayList<Input> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<Input> inputs) {
        this.inputs = inputs;
    }
    public void addInput(Input in){
        this.inputs.add(in);
    }

    public Map<Integer,Input> getInputArray(){
        Map<Integer,Input> inputArray = new HashMap<>();

        for(Input input: inputs ){
            inputArray.put(input.getInputId(),input);
        }
        return inputArray;
    }


    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", serialport='" + serialport + '\'' +
                ", baundRate='" + baundRate + '\'' +
                ", outputRow=" + outputRow +
                ", outputColumn=" + outputColumn +
                ", input=" + inputs.toString() +
                '}';
    }
}
