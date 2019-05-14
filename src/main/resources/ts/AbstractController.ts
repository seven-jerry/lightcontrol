///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
namespace client {

    export abstract class AbstractController implements IWebSocketConsumer, IClientStateChangeConsumer {
        websocket: WebSocketClient;
        model: ClientStateModel;


        protected groupedOutputState: {} = {};
        protected outSideHigh:number = 0;
        protected outSideLow:number = 0;


        constructor() {
            this.model = new ClientStateModel(this);
            this.initGroupedState();
        }

        initGroupedState() {
            this.groupedOutputState = {};
            this.groupedOutputState["high"] = [];
            this.groupedOutputState["low"] = [];
            this.groupedOutputState["disabled"] = [];
            this.groupedOutputState["outside_low"] = [];
            this.groupedOutputState["outside_high"] = [];
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


        handleCommandsChanged() {
        }

        handleOutputStateChange(state: string) {
            var method_char = state.charAt(0);
            if(method_char != 'o'){
                this.displayError("got wrong output method char");
                return;
            }
            state = state.substr(1);
            let saveSate = state;
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
            this.initGroupedState();

            while (state.length > 0) {
                var x = parseInt(state.charAt(0));
                var y = parseInt(state.charAt(1));
                var s = parseInt(state.charAt(2));
                if (s == 0) {
                    this.groupedOutputState["low"].push(x + "" + y);
                } else if (s == 1) {
                    this.groupedOutputState["high"].push(x + "" + y);

                } else if (s == 7) {
                    this.groupedOutputState["disabled"].push(x + "" + y);
                }
                state = state.substr(3);
            }

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
            this.outSideLow = 0;
            this.outSideHigh = 0;
            var method_char = state.charAt(0);
            if(method_char != 'u'){
                this.displayError("got wrong output method char");
                return;
            }
            state = state.substr(1);

            while (state.length > 0) {
                var s = parseInt(state.charAt(1));
                if (s == 1) {
                    this.outSideHigh++;
                }
                if (s == 0) {
                    this.outSideLow++;
                }
                state = state.substr(2);
                continue;
            }
        }


    }
}