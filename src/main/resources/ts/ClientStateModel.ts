///<reference path="ClientState.ts"/>
namespace client {
    export interface IClientStateChangeConsumer {
        handleUpdateError(message: string);
        handleSizeChange(columns: number, rows: number);
        handleCommandsChanged();
        handleOutputStateChange(state:string);
        handleInputStateChange(inputMap:{});
        handleOutsideStateChange(state:string);
    }

    export class ClientStateModel {
        consumer: IClientStateChangeConsumer;
        clientState: ClientState;

        constructor(consumer: IClientStateChangeConsumer) {
            this.consumer = consumer;
            this.clientState = new ClientState();
        }

        handleStateUpdate(update: string) {
            try {
                let object = JSON.parse(update);
                for (let key in object) {
                    if (key == "error"  && object.hasOwnProperty(key)) {
                        this.consumer.handleUpdateError(object[key]);
                    }
                    if (key == "setting" && object.hasOwnProperty(key)) {
                        this.updateSettings(object[key]);
                    }
                    if (key == "commands" && object.hasOwnProperty(key)) {
                        this.updateCommands(object[key]);
                    }
                    if (key == "output_state" && object.hasOwnProperty(key)) {
                        this.updateOutputState(object[key]);
                    }
                    if (key == "input_state_map" && object.hasOwnProperty(key)) {
                        this.updateInputState(object[key]);
                    }

                    if (key == "outside_state" && object.hasOwnProperty(key)) {
                        this.updateOutsideState(object[key]);
                    }
                }
            } catch (e) {
                this.consumer.handleUpdateError("could not process update: " + e);
            }
            console.log(update);
        }

        private updateSettings(obj: any) {
            let setting = this.clientState.setting;
            let newSetting = Setting.fromObject(obj);
            this.clientState.setting = newSetting;

            if (setting.rows != newSetting.rows || setting.columns != newSetting.columns) {
                this.consumer.handleSizeChange(newSetting.columns, newSetting.rows);
            }
        }

        private updateCommands(obj: any) {
            let commands = this.clientState.commands;
            let newCommands = Command.arrayFromObject(obj);
            this.clientState.commands = newCommands;
            if (commands.length != newCommands.length) {
                this.consumer.handleCommandsChanged();
                return;
            }

            // always fire update for now
            this.consumer.handleCommandsChanged();
        }


        private updateOutputState(state: string) {
            this.clientState.ouputState = state;
            this.consumer.handleOutputStateChange(state);
        }

        private updateOutsideState(state: string) {
            this.clientState.ouputState = state;
            this.consumer.handleOutsideStateChange(state);
        }

        private updateInputState(state:{}) {
            this.clientState.inputState = state;
            this.consumer.handleInputStateChange(state);
        }



    }
}