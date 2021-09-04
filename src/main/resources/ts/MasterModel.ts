namespace client {

    export interface IMasterChangeConsumer {
        handleSettingsChange(host: string, setting: Setting);

        handleUpdateError(host: string, message: string);

        handleCommandsChanged(host: string, commands: Command[]);

        handleOutputStateChange(host: string, state: string);

        handleInputStateChange(host: string, inputMap: {});

        handleOutsideStateChange(host: string, state: string);
    }

    export class MasterModel {
        map: {} = {};
        delegate: IMasterChangeConsumer;

        constructor(delegate: IMasterChangeConsumer) {
            this.delegate = delegate;
        }

        public handleStateUpdate(state: string) {
            try {
                let object = JSON.parse(state);
                if (this.ping(object)) {
                    return;
                }
                for (let key in object) {
                    if (!this.map.hasOwnProperty(key)) {
                        this.initModel(key, object[key]);
                        continue;
                    }
                    this.partialUpdate(key, object[key]);
                }
            } catch (e) {
                // this.consumer.handleUpdateError("could not process update: " + e);
            }
            //  console.log(update);
        }

        private ping(object:{}) :boolean{
            for (let key in object) {
                let message = object[key];
                if (!message.hasOwnProperty("type")) {
                    continue;
                }
                if (message["type"] == "ping") {
                    console.log("ping from "+key+" at "+message["time"]);
                    return true;
                }
            }
            return false;
        }

        public getSettings(host: string): Setting {
            if (!this.map.hasOwnProperty(host)) {
                throw new Error("host not found");
            }
            let wrapper: ClientStateWrapper = this.map[host];
            return wrapper.state.clientState.setting;
        }


        private initModel(key: string, value) {
            this.map[key] = new ClientStateWrapper(key, this.delegate);
            this.map[key].handleUpdate(value);
        }

        private partialUpdate(key, object) {
            let client: ClientStateWrapper = this.map[key];
            client.handlePartialUpdate(object);
        }


        private getClientState(host: string): ClientStateModel {
            if (this.map.hasOwnProperty(host)) {
                return this.map[host];
            }
            throw new Error("host not found in map");
        }


    }
}