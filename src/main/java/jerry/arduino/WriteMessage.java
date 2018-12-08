package jerry.arduino;

public class WriteMessage {
    public String key = "";
    public String value = "";

    public WriteMessage(){

    }
    public WriteMessage(String key,String value){
    this.key = key;
    this.value = value;
    }


    public boolean isEmpty(){
        if(value == null) return false;
        return value.isEmpty();
    }
}
