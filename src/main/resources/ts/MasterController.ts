///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="MessageConverter.ts"/>
///<reference path="AbstractController.ts"/>
///<reference path="MasterModel.ts"/>
///<reference path="StateAggregation.ts"/>

namespace client {
    export class MasterController implements IWebSocketConsumer, IMasterChangeConsumer {
        websocket: WebSocketClient;
        model: MasterModel;
        aggregation: StateAggregation;
        outSideAggregation: OutputAggregation;
        commandMap:{} = {};
        commandHostMap:{} = {};

        constructor() {
            this.aggregation = new StateAggregation();
            this.outSideAggregation = new OutputAggregation();
            this.model = new MasterModel(this);
        }

        start(host: string) {
            this.websocket = new WebSocketClient(host, this);
        }

        commandEntered(command:string){
            var hosts = [];
            $(".formCommand").each(function(){
                var el:HTMLInputElement =<HTMLInputElement>this;
                if(el.checked) {
                    hosts.push($(this).val());
                }
            });

            var message = MessageConverter.masterChangeMessage(hosts,command);
            this.websocket.send(message);

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

        handleCommandsChanged(host: string, commands: Command[]) {
            this.commandHostMap[host] = commands;
            this.addCommands(commands);
            $("#commandForm").empty();
            $("#commandWrapper").empty();
            for(let key in this.commandHostMap){
                if(!this.commandHostMap.hasOwnProperty(key)){
                    continue;
                }
                let name = this.model.getSettings(key).name;
                let commandView = MasterController.commandFormView(key,name);
                $("#commandForm").append(commandView);
            }
            for(let key in this.commandMap){
                if(!this.commandMap.hasOwnProperty(key)){
                    continue;
                }
                let view = MasterController.commandView(this.commandMap[key]);
                $("#commandWrapper").append(view);

            }
        }

        private addCommands(commands:Command[]){
            for(let command of commands){
                this.commandMap[command.command] = command;
            }
        }

        handleInputStateChange(host: string, inputMap: {}) {
            let viewObj = this.getStateElement(host, undefined);

            var state = "";
            for (let key in inputMap) {
                if (!inputMap.hasOwnProperty(key)) {
                    continue;
                }
                state += " " + key + " => " + inputMap[key] + " ;";
            }
            $(viewObj).find(".inputValue").text(state);
        }

        handleOutputStateChange(host: string, state: string) {
            let viewObj = this.getStateElement(host, undefined);
            let groupedState = this.aggregation.withNewState(state);
            var state = "Aus : " + groupedState["low"].length + " Ein : " + groupedState["high"].length + " ";
            $(viewObj).find(".outputValue").text(state);
        }

        handleOutsideStateChange(host: string, state: string) {
            let viewObj = this.getStateElement(host, undefined);
            let groupedState = this.outSideAggregation.withNewState(state);
            var state = "Aus : " + groupedState["low"] + " Ein : " + groupedState["high"] + " ";
            $(viewObj).find(".outsideValue").text(state);
        }

        handleUpdateError(host: string, message: string) {
            this.displayError("Error from " + host + " : " + message);
        }

        private getStateElement(host: string, name: string) {
            let el = $("#" + MasterController.toStateId(host));

            if (el.length > 0) {
                return el;
            }
            let view = MasterController.stateView(host, name);
            $("#stateWrapper").append(view);
            return $("#" + MasterController.toStateId(host));
        }

        public static stateView(host: string, name: string) {

            let id = MasterController.toStateId(host);

            return "" +
                "<div id=\"" + id + "\">\n" +
                "  <div class=\"left\"><h3 class=\"name\">" + name + "</h3></div>\n" +
                "    <div class=\"list-group-item sized row\">\n" +
                "      <div class=\"slice\">\n" +
                "         <span>Eingänge</span><span class=\"value inputValue block\"></span>\n" +
                "      </div>\n" +
                "      <div class=\"slice\">\n" +
                "         <span>Lichter</span><span class=\"value outputValue block\"></span>\n" +
                "      </div>\n" +
                "      <div class=\"slice\">\n" +
                "         <span>Ausen</span><span class=\"value outsideValue block\"></span>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "</div>";

        }

        private static commandFormView(host: string, name: string) {
            let hostStr = host.replace(/\./g, "");

            let id = hostStr + "commandForm";
            return "" +
                "<div class=\"form-group\" style=\"margin: 10px;\">\n" +
                "  <input class=\"form-check-input formCommand\" type=\"checkbox\" name=\""+id+"\" value=\""+host+"\" id=\"" + id + "\" checked>\n" +
                "  <label class=\"form-check-label\" for=\"" + id + "\">\n" +
                "     <h4>" + name + "</h4>\n" +
                "  </label>\n" +
                "</div>";
        }

        private static commandView(command:Command){
            return "<button class=\"btn btn-light\" onclick=\"commandEntered('"+command.command+"')\" style=\"width: 100%;margin-top:20px;\">"+command.label+ "<span class=\"value\">-</span></button>"
        }

        private static toStateId(host: string) {
            let hostStr = host.replace(/\./g, "");
            return "state" + hostStr;
        }

        handleSettingsChange(host: string, setting: client.Setting) {
            this.getStateElement(host, setting.name);
        }

    }
}
