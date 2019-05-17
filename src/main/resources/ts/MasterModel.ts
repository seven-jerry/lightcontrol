namespace client {

    export interface IMasterChangeConsumer {
        handleUpdateError(host: string, message: string);

        handleCommandsChanged(host: string);

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
                for (let key in object) {
                    if (!this.map.hasOwnProperty(key)) {
                        this.initModel(key, object[key]);
                        return;
                    }
                    this.partialUpdate(key, object);
                }
            } catch (e) {
                // this.consumer.handleUpdateError("could not process update: " + e);
            }
            //  console.log(update);
        }


        private initModel(key: string, value) {
            this.map[key] = new ClientStateWrapper(key, value, this.delegate);
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