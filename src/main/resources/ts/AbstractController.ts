///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="StateAggregation.ts"/>

namespace client {

    export abstract class AbstractController implements IWebSocketConsumer, IClientStateChangeConsumer {
        websocket: WebSocketClient;
        model: ClientStateModel;

        aggregation:StateAggregation;
        outSideAggregation:OutputAggregation;

        constructor() {
            this.aggregation = new StateAggregation();
            this.outSideAggregation = new OutputAggregation();
            this.model = new ClientStateModel(this);
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

        public groupedOutputState(){
            return this.aggregation.groupedOutputState;
        }


        handleCommandsChanged(commands:Command[]) {
        }

        handleOutputStateChange(state: string) {
            var method_char = state.charAt(0);
            let saveSate = state;
            if(method_char != 'o'){
                this.displayError("got wrong output method char");
                return;
            }
            state = state.substr(1);
            while (state.length > 0) {
                var xChar = state.charAt(0);
                var x = parseInt(state.charAt(0));
                var y = parseInt(state.charAt(1));
                var s = parseInt(state.charAt(2));

                this.changeOutputRow(x, y, s);
                state = state.substr(3);
            }

            this.generateStateAggregation(saveSate);
        }

        protected generateStateAggregation(state: string) {
            this.aggregation.withNewState(state);
        }

        protected changeOutputRow(x: number, y: number, s: number) {
        }

        handleSizeChange(columns: number, rows: number) {
        }

        handleUpdateError(message: string) {
            this.displayError(message);
        }


        protected displayError(error: string) {
            console.error(error);
            setTimeout(
                function () {
                   location.reload();
                }, 30000);


        }

        handleInputStateChange(inputMap: {}) {

        }

        handleOutsideStateChange(state: string) {
           this.outSideAggregation.withNewState(state);
        }

        public groupedOutsideState(){
            return this.outSideAggregation.groupedOutsideState;
        }

        handleSettingsChange(setting: client.Setting) {
        }







    }
}