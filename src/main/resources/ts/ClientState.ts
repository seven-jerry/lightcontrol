///<reference path="entity/Setting.ts"/>
namespace client {
    export class ClientState {
        setting: Setting;
        commands: Command[] = [];
        ouputState: string;
        outsideState:string;
        inputState: {} = {};

        constructor() {
            this.setting = Setting.withDefaults();
        }

    }
}