///<reference path="SingleLightController.ts"/>
///<reference path="CommandsController.ts"/>

declare var profile;
declare var wsHost;


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

controller.start(wsHost);

