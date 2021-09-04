var client;
(function (client) {
    class WebSocketClient {
        constructor(host, consumer) {
            this.websocket = new WebSocket(host + "/webSocket");
            var that = this;
            this.websocket.onopen = (evt) => {
                that.onSocketOpen(evt);
            };
            this.websocket.onclose = (evt) => {
                that.onSocketClose(evt);
            };
            this.websocket.onmessage = (evt) => {
                that.onSocketMessage(evt);
            };
            this.websocket.onerror = (evt) => {
                that.onSocketError(evt);
            };
            this.consumer = consumer;
        }
        onSocketOpen(evt) {
            console.log(evt);
            this.started = true;
            this.consumer.webSocketHasStarted();
        }
        onSocketClose(evt) {
            this.started = false;
            this.consumer.webSocketHasEnded();
        }
        ;
        onSocketMessage(evt) {
            this.consumer.handleWebSocketMessage(evt.data);
        }
        onSocketError(evt) {
            this.consumer.handleWebSocketError(evt.data);
        }
        ;
        send(message) {
            this.websocket.send(message);
        }
    }
    client.WebSocketClient = WebSocketClient;
})(client || (client = {}));
var client;
(function (client) {
    class SerialSource {
        static fromObject(obj) {
            let source = new SerialSource();
            source.baundRate = obj.baundRate;
            source.port = obj.port;
            source.serialSources = obj.serialSources;
            return source;
        }
    }
    client.SerialSource = SerialSource;
})(client || (client = {}));
var client;
(function (client) {
    class Command {
        static fromObject(obj) {
            let command = new Command();
            command.label = obj.label;
            command.id = obj.id;
            command.command = obj.command;
            command.order = obj.order;
            return command;
        }
        static arrayFromObject(obj) {
            let build = [];
            for (let commandObj of obj) {
                let command = Command.fromObject(commandObj);
                build.push(command);
            }
            return build;
        }
    }
    client.Command = Command;
})(client || (client = {}));
var client;
(function (client) {
    class Input {
        static fromObject(obj) {
            let input = new Input();
            input.type = obj.type;
            input.sourceId = obj.sourceId;
            input.id = obj.id;
            input.name = obj.name;
            input.order = obj.order;
            input.state = obj.state;
            return input;
        }
    }
    client.Input = Input;
})(client || (client = {}));
///<reference path="SerialSource.ts"/>
///<reference path="Command.ts"/>
///<reference path="Input.ts"/>
var client;
(function (client) {
    class Setting {
        constructor() {
            this.inputs = [];
        }
        static withDefaults() {
            let setting = new Setting();
            setting.outside = 0;
            setting.columns = 0;
            setting.rows = 0;
            setting.masterUrl = "";
            setting.control = "";
            setting.name = "Default Name";
            return setting;
        }
        static fromObject(obj) {
            let setting = new Setting();
            setting.rows = obj.rows;
            setting.columns = obj.columns;
            setting.outside = obj.outside;
            setting.inputCommand = obj.inputCommand;
            setting.masterUrl = obj.masterUrl;
            setting.name = obj.name;
            setting.inputSource = client.SerialSource.fromObject(obj.inputSource);
            setting.outputSource = client.SerialSource.fromObject(obj.outputSource);
            for (let input of obj.inputs) {
                setting.inputs.push(client.Input.fromObject(input));
            }
            return setting;
        }
    }
    client.Setting = Setting;
})(client || (client = {}));
///<reference path="entity/Setting.ts"/>
var client;
(function (client) {
    class ClientState {
        constructor() {
            this.commands = [];
            this.inputState = {};
            this.setting = client.Setting.withDefaults();
        }
    }
    client.ClientState = ClientState;
})(client || (client = {}));
///<reference path="ClientState.ts"/>
var client;
(function (client) {
    class ClientStateModel {
        constructor(consumer) {
            this.consumer = consumer;
            this.clientState = new client.ClientState();
        }
        handleStateUpdate(update) {
            try {
                let object = JSON.parse(update);
                if (object.hasOwnProperty("setting")) {
                    this.updateSettings(object["setting"]);
                }
                for (let key in object) {
                    if (key == "error" && object.hasOwnProperty(key)) {
                        this.consumer.handleUpdateError(object[key]);
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
            }
            catch (e) {
                this.consumer.handleUpdateError("could not process update: " + e);
            }
            console.log(update);
        }
        updateSettings(obj) {
            let setting = this.clientState.setting;
            let newSetting = client.Setting.fromObject(obj);
            this.clientState.setting = newSetting;
            if (setting.rows != newSetting.rows || setting.columns != newSetting.columns) {
                this.consumer.handleSizeChange(newSetting.columns, newSetting.rows);
            }
            this.consumer.handleSettingsChange(newSetting);
        }
        updateCommands(obj) {
            let commands = this.clientState.commands;
            let newCommands = client.Command.arrayFromObject(obj);
            this.clientState.commands = newCommands;
            if (commands.length != newCommands.length) {
                this.consumer.handleCommandsChanged(newCommands);
                return;
            }
            // always fire update for now
            this.consumer.handleCommandsChanged(newCommands);
        }
        updateOutputState(state) {
            this.clientState.ouputState = state;
            this.consumer.handleOutputStateChange(state);
        }
        updateOutsideState(state) {
            this.clientState.ouputState = state;
            this.consumer.handleOutsideStateChange(state);
        }
        updateInputState(state) {
            this.clientState.inputState = state;
            this.consumer.handleInputStateChange(state);
        }
    }
    client.ClientStateModel = ClientStateModel;
})(client || (client = {}));
var client;
(function (client) {
    class StateAggregation {
        constructor() {
            this.groupedOutputState = {};
        }
        initGroupedState() {
            this.groupedOutputState = {};
            this.groupedOutputState["high"] = [];
            this.groupedOutputState["low"] = [];
            this.groupedOutputState["disabled"] = [];
            this.groupedOutputState["outside_low"] = [];
            this.groupedOutputState["outside_high"] = [];
        }
        withNewState(state) {
            this.initGroupedState();
            var method_char = state.charAt(0);
            if (method_char != 'o') {
                console.error("got wrong output method char");
            }
            else {
                state = state.substr(1);
            }
            while (state.length > 0) {
                var x = parseInt(state.charAt(0));
                var y = parseInt(state.charAt(1));
                var s = parseInt(state.charAt(2));
                if (s == 0) {
                    this.groupedOutputState["low"].push(x + "" + y);
                }
                else if (s == 1) {
                    this.groupedOutputState["high"].push(x + "" + y);
                }
                else if (s == 7) {
                    this.groupedOutputState["disabled"].push(x + "" + y);
                }
                state = state.substr(3);
            }
            return this.groupedOutputState;
        }
    }
    client.StateAggregation = StateAggregation;
    class OutputAggregation {
        constructor() {
            this.groupedOutsideState = {};
        }
        init() {
            this.groupedOutsideState["high"] = 0;
            this.groupedOutsideState["low"] = 0;
        }
        withNewState(state) {
            this.init();
            var method_char = state.charAt(0);
            if (method_char != 'u') {
                console.error("got wrong output method char");
            }
            state = state.substr(1);
            while (state.length > 0) {
                var s = parseInt(state.charAt(1));
                if (s == 1) {
                    this.groupedOutsideState["high"]++;
                }
                if (s == 0) {
                    this.groupedOutsideState["low"]++;
                }
                state = state.substr(2);
            }
            return this.groupedOutsideState;
        }
    }
    client.OutputAggregation = OutputAggregation;
})(client || (client = {}));
///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="StateAggregation.ts"/>
var client;
(function (client) {
    class AbstractController {
        constructor() {
            this.aggregation = new client.StateAggregation();
            this.outSideAggregation = new client.OutputAggregation();
            this.model = new client.ClientStateModel(this);
        }
        start(host) {
            this.websocket = new client.WebSocketClient(host, this);
        }
        handleWebSocketMessage(message) {
            this.model.handleStateUpdate(message);
        }
        handleWebSocketError(message) {
            this.displayError(message);
        }
        webSocketHasEnded() {
            this.displayError("socket has closed");
        }
        webSocketHasStarted() {
        }
        groupedOutputState() {
            return this.aggregation.groupedOutputState;
        }
        handleCommandsChanged(commands) {
        }
        handleOutputStateChange(state) {
            var method_char = state.charAt(0);
            let saveSate = state;
            if (method_char != 'o') {
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
        generateStateAggregation(state) {
            this.aggregation.withNewState(state);
        }
        changeOutputRow(x, y, s) {
        }
        handleSizeChange(columns, rows) {
        }
        handleUpdateError(message) {
            this.displayError(message);
        }
        displayError(error) {
            console.error(error);
            setTimeout(function () {
                location.reload();
            }, 30000);
        }
        handleInputStateChange(inputMap) {
        }
        handleOutsideStateChange(state) {
            this.outSideAggregation.withNewState(state);
        }
        groupedOutsideState() {
            return this.outSideAggregation.groupedOutsideState;
        }
        handleSettingsChange(setting) {
        }
    }
    client.AbstractController = AbstractController;
})(client || (client = {}));
var client;
(function (client) {
    class ClientStateWrapper {
        constructor(host, delegate) {
            this.host = host;
            this.state = new client.ClientStateModel(this);
            this.delegate = delegate;
        }
        handleUpdate(state) {
            this.state.handleStateUpdate(state);
        }
        handleCommandsChanged(commands) {
            this.delegate.handleCommandsChanged(this.host, commands);
        }
        handleInputStateChange(inputMap) {
            this.delegate.handleInputStateChange(this.host, inputMap);
        }
        handleOutputStateChange(state) {
            this.delegate.handleOutputStateChange(this.host, state);
        }
        handleOutsideStateChange(state) {
            this.delegate.handleOutsideStateChange(this.host, state);
        }
        handleSizeChange(columns, rows) {
        }
        handleUpdateError(message) {
            this.delegate.handleUpdateError(this.host, message);
        }
        handlePartialUpdate(update) {
            this.state.handleStateUpdate(update);
        }
        handleSettingsChange(setting) {
            this.delegate.handleSettingsChange(this.host, setting);
        }
    }
    client.ClientStateWrapper = ClientStateWrapper;
})(client || (client = {}));
var client;
(function (client) {
    class MessageConverter {
        static changeMessage(message) {
            var obj = {};
            obj["type"] = "CHANGE";
            obj["argument"] = message;
            return JSON.stringify(obj);
        }
        static masterChangeMessage(hosts, command) {
            var obj = {};
            obj["type"] = "CHANGE";
            var hostMap = {};
            for (let host of hosts) {
                hostMap[host] = command;
            }
            obj["argument"] = hostMap;
            return JSON.stringify(obj);
        }
        static fetchMessage(message) {
            var obj = {};
            obj["type"] = "FETCH";
            obj["argument"] = message;
            return JSON.stringify(obj);
        }
    }
    client.MessageConverter = MessageConverter;
})(client || (client = {}));
///<reference path="AbstractController.ts"/>
///<reference path="MessageConverter.ts"/>
var client;
(function (client) {
    class CommandsController extends client.AbstractController {
        commandEntered(command) {
            let message = client.MessageConverter.changeMessage(command);
            this.websocket.send(message);
        }
        handleCommandsChanged(commands) {
            $("#commandWrapper").empty();
            for (let command of this.model.clientState.commands) {
                CommandsController.displayCommand(command);
            }
        }
        handleOutputStateChange(state) {
            super.handleOutputStateChange(state);
            $("#stateWrapper").empty();
            CommandsController.displayState(this.groupedOutputState());
        }
        handleOutsideStateChange(state) {
            super.handleOutsideStateChange(state);
            $("#outsideWrapper").empty();
            let outside = this.groupedOutsideState();
            CommandsController.displayOutside(outside["low"], outside["high"]);
        }
        handleInputStateChange(inputMap) {
            $("#inputWrapper").empty();
            for (var key in inputMap) {
                if (inputMap.hasOwnProperty(key)) {
                    CommandsController.displayInput(key, inputMap[key]);
                }
            }
        }
        static displayOutside(low, high) {
            var state = "Aus : " + low + " Ein : " + high + " ";
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += state;
            html += '</li>';
            $("#outsideWrapper").append(html);
        }
        static displayState(groupedState) {
            var state = "Aus : " + groupedState["low"].length + " Ein : " + groupedState["high"].length + " ";
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += state;
            html += '</li>';
            $("#stateWrapper").append(html);
        }
        static displayCommand(command) {
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += '<a class="link" href="#" onclick="commandEntered(\'' + command.command + '\')">' + command.label + '</a>';
            html += '</li>';
            $("#commandWrapper").append(html);
        }
        static displayInput(label, state) {
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += label + " => " + state;
            html += '</li>';
            $("#inputWrapper").append(html);
        }
    }
    client.CommandsController = CommandsController;
})(client || (client = {}));
///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="MessageConverter.ts"/>
///<reference path="AbstractController.ts"/>
var client;
(function (client) {
    class SingleLightController extends client.AbstractController {
        constructor() {
            super();
        }
        start(host) {
            super.start(host);
            SingleLightController.hideLoader();
        }
        /* implementation IClientStateChangeConsumer */
        handleSizeChange(columns, rows) {
            $("#grid").empty();
            for (var row = 0; row < rows; row++) {
                for (var column = 0; column < columns; column++) {
                    SingleLightController.addLight(column, row);
                }
            }
            this.changeButtonLayout();
        }
        handleCommandsChanged(commands) {
            $(".command").remove();
            for (let command of this.model.clientState.commands) {
                SingleLightController.displayCommand(command);
            }
        }
        changeButtonLayout() {
            var columns = this.model.clientState.setting.columns;
            var rows = this.model.clientState.setting.rows;
            var marginLeft = 50 * (window.innerWidth / 1300);
            var marginTop = 50 * (window.innerHeight / 1000);
            var width = (window.innerWidth - marginLeft * (columns + 1)) / columns;
            var height = (window.innerHeight - 58 - marginTop * (rows + 1)) / rows;
            $(".lights").each(function () {
                $(this).css("margin-left", marginLeft + "px");
                $(this).css("margin-top", marginTop + "px");
                $(this).css("width", width + "px");
                $(this).css("height", height + "px");
            });
        }
        handleOutputStateChange(state) {
            super.handleOutputStateChange(state);
        }
        handleOutsideStateChange(state) {
            super.handleOutsideStateChange(state);
            let outside = this.groupedOutsideState();
            SingleLightController.updateOutside(outside["high"], outside["low"]);
        }
        commandEntered(command) {
            let message = client.MessageConverter.changeMessage(command);
            this.websocket.send(message);
        }
        changeOutputRow(x, y, s) {
            var key = "#light" + x + y + "";
            if (s == 0) {
                $(key).css("background-color", "#6c757d");
                $(key).attr('onclick', 'light_clicked(' + x + ',' + y + ',' + 1 + ');');
            }
            else if (s == 1) {
                $(key).css("background-color", "#ffc107");
                $(key).attr('onclick', 'light_clicked(' + x + ',' + y + ',' + 0 + ');');
            }
            else if (s == 7) {
                $(key).css("visibility", "hidden");
                $(key).attr('onclick', '');
            }
        }
        lightClicked(row, column, value) {
            let obj = "" + row + "" + column + "" + value + "";
            let message = client.MessageConverter.changeMessage(obj);
            let key = "#light" + row + column + "";
            $(key).css("background-color", "#ffffe6");
            this.websocket.send(message);
        }
        static addLight(column, row) {
            let key = "light" + row + column + "";
            $("#grid").append('<button id ="' + key + '" class="btn btn-default lights">' + (column + 1) + " " + (row + 1) + "</button>");
        }
        static displayCommand(command) {
            let html = '<li class="command nav-item white">';
            html += '<a class="nav-link white" href="#" onclick="commandEntered(\'' + command.command + '\')">' + command.label + '</a>';
            html += '</li>';
            $("#lastEl").after(html);
        }
        static showLoader() {
            $("#loader").css("display", "block");
        }
        static hideLoader() {
            $("#loader").css("display", "none");
        }
        static updateOutside(high, low) {
            $("#outside").text("Ein: " + high + " Aus: " + low);
        }
    }
    client.SingleLightController = SingleLightController;
})(client || (client = {}));
var client;
(function (client_1) {
    class MasterModel {
        constructor(delegate) {
            this.map = {};
            this.delegate = delegate;
        }
        handleStateUpdate(state) {
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
            }
            catch (e) {
                // this.consumer.handleUpdateError("could not process update: " + e);
            }
            //  console.log(update);
        }
        ping(object) {
            for (let key in object) {
                let message = object[key];
                if (!message.hasOwnProperty("type")) {
                    continue;
                }
                if (message["type"] == "ping") {
                    console.log("ping from " + key + " at " + message["time"]);
                    return true;
                }
            }
            return false;
        }
        getSettings(host) {
            if (!this.map.hasOwnProperty(host)) {
                throw new Error("host not found");
            }
            let wrapper = this.map[host];
            return wrapper.state.clientState.setting;
        }
        initModel(key, value) {
            this.map[key] = new client_1.ClientStateWrapper(key, this.delegate);
            this.map[key].handleUpdate(value);
        }
        partialUpdate(key, object) {
            let client = this.map[key];
            client.handlePartialUpdate(object);
        }
        getClientState(host) {
            if (this.map.hasOwnProperty(host)) {
                return this.map[host];
            }
            throw new Error("host not found in map");
        }
    }
    client_1.MasterModel = MasterModel;
})(client || (client = {}));
///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="MessageConverter.ts"/>
///<reference path="AbstractController.ts"/>
///<reference path="MasterModel.ts"/>
///<reference path="StateAggregation.ts"/>
var client;
(function (client) {
    class MasterController {
        constructor() {
            this.commandMap = {};
            this.commandHostMap = {};
            this.aggregation = new client.StateAggregation();
            this.outSideAggregation = new client.OutputAggregation();
            this.model = new client.MasterModel(this);
        }
        start(host) {
            this.websocket = new client.WebSocketClient(host, this);
        }
        commandEntered(command) {
            var hosts = [];
            $(".formCommand").each(function () {
                var el = this;
                if (el.checked) {
                    hosts.push($(this).val());
                }
            });
            var message = client.MessageConverter.masterChangeMessage(hosts, command);
            this.websocket.send(message);
        }
        handleWebSocketMessage(message) {
            this.model.handleStateUpdate(message);
        }
        handleWebSocketError(message) {
            this.displayError(message);
        }
        webSocketHasEnded() {
            this.displayError("socket has closed");
        }
        webSocketHasStarted() {
        }
        displayError(error) {
            console.error(error);
            setTimeout(function () {
                location.reload();
            }, 30000);
        }
        handleCommandsChanged(host, commands) {
            this.commandHostMap[host] = commands;
            this.addCommands(commands);
            $("#commandForm").empty();
            $("#commandWrapper").empty();
            for (let key in this.commandHostMap) {
                if (!this.commandHostMap.hasOwnProperty(key)) {
                    continue;
                }
                let name = this.model.getSettings(key).name;
                let commandView = MasterController.commandFormView(key, name);
                $("#commandForm").append(commandView);
            }
            for (let key in this.commandMap) {
                if (!this.commandMap.hasOwnProperty(key)) {
                    continue;
                }
                let view = MasterController.commandView(this.commandMap[key]);
                $("#commandWrapper").append(view);
            }
        }
        addCommands(commands) {
            for (let command of commands) {
                this.commandMap[command.command] = command;
            }
        }
        handleInputStateChange(host, inputMap) {
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
        handleOutputStateChange(host, state) {
            let viewObj = this.getStateElement(host, undefined);
            let groupedState = this.aggregation.withNewState(state);
            var state = "Aus : " + groupedState["low"].length + " Ein : " + groupedState["high"].length + " ";
            $(viewObj).find(".outputValue").text(state);
        }
        handleOutsideStateChange(host, state) {
            let viewObj = this.getStateElement(host, undefined);
            let groupedState = this.outSideAggregation.withNewState(state);
            var state = "Aus : " + groupedState["low"] + " Ein : " + groupedState["high"] + " ";
            $(viewObj).find(".outsideValue").text(state);
        }
        handleUpdateError(host, message) {
            this.displayError("Error from " + host + " : " + message);
        }
        getStateElement(host, name) {
            let el = $("#" + MasterController.toStateId(host));
            if (el.length > 0) {
                return el;
            }
            let view = MasterController.stateView(host, name);
            $("#stateWrapper").append(view);
            return $("#" + MasterController.toStateId(host));
        }
        static stateView(host, name) {
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
        static commandFormView(host, name) {
            let hostStr = host.replace(/\./g, "");
            let id = hostStr + "commandForm";
            return "" +
                "<div class=\"form-group\" style=\"margin: 10px;\">\n" +
                "  <input class=\"form-check-input formCommand\" type=\"checkbox\" name=\"" + id + "\" value=\"" + host + "\" id=\"" + id + "\" checked>\n" +
                "  <label class=\"form-check-label\" for=\"" + id + "\">\n" +
                "     <h4>" + name + "</h4>\n" +
                "  </label>\n" +
                "</div>";
        }
        static commandView(command) {
            return "<button class=\"btn btn-light\" onclick=\"commandEntered('" + command.command + "')\" style=\"width: 100%;margin-top:20px;\">" + command.label + "<span class=\"value\">-</span></button>";
        }
        static toStateId(host) {
            let hostStr = host.replace(/\./g, "");
            return "state" + hostStr;
        }
        handleSettingsChange(host, setting) {
            this.getStateElement(host, setting.name);
        }
    }
    client.MasterController = MasterController;
})(client || (client = {}));
///<reference path="SingleLightController.ts"/>
///<reference path="CommandsController.ts"/>
///<reference path="MasterController.ts"/>
var controller;
function commandEntered(command) {
    controller.commandEntered(command);
}
if (profile == "command") {
    controller = new client.CommandsController();
}
else if (profile == "single_light") {
    controller = new client.SingleLightController();
    window.onresize = function () {
        controller.changeButtonLayout();
    };
    function light_clicked(row, column, value) {
        controller.lightClicked(row, column, value);
    }
}
else if (profile == "master") {
    controller = new client.MasterController();
}
controller.start(wsHost);
