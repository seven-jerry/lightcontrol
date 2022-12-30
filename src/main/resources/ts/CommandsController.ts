///<reference path="AbstractController.ts"/>
///<reference path="MessageConverter.ts"/>

namespace client {
    export class CommandsController extends AbstractController {



        handleCommandsChanged(commands: Command[]) {
            $("#commandWrapper").empty();
            for (let command of this.model.clientState.commands) {
                CommandsController.displayCommand(command);
                this.commands.set(command.command, command);
            }
        }

        handleOutputStateChange(state: string) {
            super.handleOutputStateChange(state);
            $("#stateWrapper").empty();
            CommandsController.displayState(this.groupedOutputState());
        }

        handleOutsideStateChange(state: string) {
            super.handleOutsideStateChange(state);
            $("#outsideWrapper").empty();
            let outside = this.groupedOutsideState();
            CommandsController.displayOutside(outside["low"], outside["high"]);
        }

        handleInputStateChange(inputMap: {}) {
            $("#inputWrapper").empty();
            for (var key in inputMap) {
                if (inputMap.hasOwnProperty(key)) {
                    CommandsController.displayInput(key, inputMap[key]);
                }
            }
        }

        private static displayOutside(low, high) {
            var state = "Aus : " + low + " Ein : " + high + " ";
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += state;
            html += '</li>';
            $("#outsideWrapper").append(html);
        }

        private static displayState(groupedState) {
            var state = "Aus : " + groupedState["low"].length + " Ein : " + groupedState["high"].length + " ";
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += state;
            html += '</li>';
            $("#stateWrapper").append(html);
        }

        private static displayCommand(command: Command) {
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += '<a class="link" href="#" onclick="commandEntered(\'' + command.command + '\')">' + command.label + '</a>';
            html += '</li>';
            $("#commandWrapper").append(html);
        }

        private static displayInput(label, state) {
            let html = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
            html += label + " => " + state;
            html += '</li>';
            $("#inputWrapper").append(html);
        }
    }

}
