///<reference path="WebSocketClient.ts"/>
///<reference path="ClientStateModel.ts"/>
///<reference path="MessageConverter.ts"/>
///<reference path="AbstractController.ts"/>

namespace client {
    export class SingleLightController extends AbstractController {


        constructor() {
            super();
        }

        start(host: string) {
            super.start(host);
            SingleLightController.hideLoader();
        }


        /* implementation IClientStateChangeConsumer */


        handleSizeChange(columns: number, rows: number) {
            $("#grid").empty();
            for (var row = 0; row < rows; row++) {
                for (var column = 0; column < columns; column++) {
                    SingleLightController.addLight(column, row);
                }
            }
            this.changeButtonLayout();
        }

        handleCommandsChanged(commands: Command[]) {
            $(".command").remove();
            for (let command of this.model.clientState.commands) {
                SingleLightController.displayCommand(command);
                this.commands.set(command.command, command);
            }
        }

        public changeButtonLayout() {

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

        public handleOutputStateChange(state: string) {
            super.handleOutputStateChange(state);
        }

        public handleOutsideStateChange(state: string) {
            super.handleOutsideStateChange(state);

            let outside = this.groupedOutsideState();
            SingleLightController.updateOutside(outside["high"], outside["low"]);
        }


        protected changeOutputRow(x: number, y: number, s: number) {
            var key = "#light" + x + y + "";
            if (s == 0) {
                $(key).css("background-color", "rgba(108, 117, 125,0.7)");
                $(key).css("border", "2px solid rgb(108, 117, 125)");
                $(key).attr('onclick', 'light_clicked(' + x + ',' + y + ',' + 1 + ');');
            } else if (s == 1) {
                $(key).css("background-color", "rgba(255,193,7,0.7)");
                $(key).css("border", "2px solid rgb(108, 117, 125)");
                $(key).attr('onclick', 'light_clicked(' + x + ',' + y + ',' + 0 + ');');
            } else if (s == 7) {
                $(key).css("visibility", "hidden");
                $(key).attr('onclick', '');
            } else if (s == 8) {
                $(key).attr('onclick', '');
                $(key).css("background-color", "rgba(36, 39, 41, 0.93)");
                $(key).attr('onclick', 'light_clicked(' + x + ',' + y + ',' + 0 + ');');
            }
        }

        public lightClicked(row, column, value) {
            let obj = "" + row + "" + column + "" + value + "";
            let message = MessageConverter.changeMessage(obj);

            if (window.location.hash && window.location.hash.includes("disable")) {
                message = MessageConverter.disableMessage("" + row + "" + column + "");
            }
            let key = "#light" + row + column + "";
            $(key).css("background-color", "#ffffe6");
            this.websocket.send(message);
        }

        private static addLight(column, row) {
            let key = "light" + row + column + "";
            $("#grid").append('<button id ="' + key + '" class="btn btn-default lights">' + (column + 1) + " " + (row + 1) + "</button>");
        }

        private static displayCommand(command: Command) {
            let html = '<li class="command nav-item white">';
            html += '<a class="nav-link white" href="#" onclick="commandEntered(\'' + command.command + '\')">' + command.label + '</a>';
            html += '</li>';
            $("#lastEl").after(html);
        }


        private static showLoader() {
            $("#loader").css("display", "block");
        }

        private static hideLoader() {
            $("#loader").css("display", "none");
        }

        private static updateOutside(high: number, low: number) {
            $("#outside").text("Ein: " + high + " Aus: " + low);
        }
    }
}
