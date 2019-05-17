///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="MessageConverter.ts"/>
///<reference path="AbstractController.ts"/>
///<reference path="MasterModel.ts"/>

namespace client {
    export class MasterController implements IWebSocketConsumer, IMasterChangeConsumer {
        websocket: WebSocketClient;
        model: MasterModel;

        constructor() {
            this.model = new MasterModel(this);
        }

        start(host: string) {
            this.websocket = new WebSocketClient(host, this);
        }


        handleWebSocketMessage(message: string) {
            this.model.handleStateUpdate(message);
        }

        handleWebSocketError(message: string) {
            this.displayError(message);
        }

        webSocketHasEnded(): void {
            this.displayError("socket has closed");
        }

        webSocketHasStarted(): void {

        }


        protected displayError(error: string) {
            console.error(error);
            setTimeout(
                function () {
                    location.reload();
                }, 30000);


        }

        handleCommandsChanged(host: string) {
            console.log(host);
        }

        handleInputStateChange(host: string, inputMap: {}) {
            console.log(host);
        }

        handleOutputStateChange(host: string, state: string) {
            console.log(host);
        }

        handleOutsideStateChange(host: string, state: string) {
            console.log(host);
        }

        handleUpdateError(host: string, message: string) {
            console.log(host);
        }


    }
}
