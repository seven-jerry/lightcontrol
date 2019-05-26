namespace client{
    export class ClientStateWrapper implements IClientStateChangeConsumer{
        state:ClientStateModel;
        host:string;
        delegate:IMasterChangeConsumer;

        constructor(host:string,delegate:IMasterChangeConsumer){
            this.host = host;
            this.state = new ClientStateModel(this);
            this.delegate = delegate;
        }

        handleUpdate(state:string){
            this.state.handleStateUpdate(state);
        }
        handleCommandsChanged(commands:Command[]) {
            this.delegate.handleCommandsChanged(this.host,commands);
        }

        handleInputStateChange(inputMap: {}) {
            this.delegate.handleInputStateChange(this.host,inputMap);
        }

        handleOutputStateChange(state: string) {
            this.delegate.handleOutputStateChange(this.host,state);
        }

        handleOutsideStateChange(state: string) {
            this.delegate.handleOutsideStateChange(this.host,state);
        }

        handleSizeChange(columns: number, rows: number) {
        }

        handleUpdateError(message: string) {
            this.delegate.handleUpdateError(this.host,message);
        }

        handlePartialUpdate(update: string) {
            this.state.handleStateUpdate(update);
        }

        handleSettingsChange(setting: client.Setting) {
            this.delegate.handleSettingsChange(this.host,setting);
        }
    }
}