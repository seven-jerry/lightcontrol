///<reference path="SerialSource.ts"/>
///<reference path="Command.ts"/>
///<reference path="Input.ts"/>
namespace client {

    export class Setting {
        inputSource: SerialSource;
        outside: number;
        columns: number;
        inputCommand: string;
        masterUrl: string;
        inputs: Input[] = [];
        control: string;
        rows: number;
        outputSource: SerialSource;
        name: string;

        public static withDefaults(){
            let setting = new Setting();
            setting.outside = 0;
            setting.columns = 0;
            setting.rows = 0;
            setting.masterUrl="";
            setting.control = "";
            setting.name = "Default Name";
            return setting;
        }
        public static fromObject(obj: any) {
            let setting = new Setting();
            setting.rows = obj.rows;
            setting.columns = obj.columns;
            setting.outside = obj.outside;
            setting.inputCommand = obj.inputCommand;
            setting.masterUrl = obj.masterUrl;
            setting.name = obj.name;

            setting.inputSource = SerialSource.fromObject(obj.inputSource);
            setting.outputSource = SerialSource.fromObject(obj.outputSource);

            for(let input of obj.inputs){
                setting.inputs.push(Input.fromObject(input));
            }
            return setting;

        }
    }
}