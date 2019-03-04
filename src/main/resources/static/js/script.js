var websocket;
var setting;
var host = "http://" + location.host;
var wsHost = "ws://" + location.host;
var workingColumn;
var expectedState = [];
var rows = 0;
var columns = 0;
var lastMessage;
var outSideHigh= 0;
var outSideLow= 0;
var queue="";
var sendQueue = false;
var sendTimeout;
var sendQueueDelay = 300;

var template = '<li class="nav-item white">';
template += '<a class="nav-link white" href="#" onclick="writeAction(\'{command}\')">{label}</a>';
template += '</li>';

hideLoader();

function log(msg){
    console.log(msg);
}


$(function () {
    $.getJSON(host + "/api/setting/list", function (data) {
        setting = data;
        prepareSetting();
        drawGrid();
        positionGrid();
        doConnect();
    });
});


function prepareSetting() {
    columns = setting.columns;
    rows = setting.rows;
    addActions(setting.commands);
}


function addActions(actions) {
    for (var i in actions) {
        var action = actions[i];
        var actionTemplate = template.replace("{label}", action.label);
        actionTemplate = actionTemplate.replace("{command}", action.command);

        $("#formEl").before(actionTemplate);
    }
}


function drawGrid() {
    $("#grid").empty();
    for (var row = 0; row < rows; row++) {
        for (var column = 0; column < columns; column++) {
            addLight(column, row);
        }
    }
}


function addLight(column, row) {
    if (workingColumn == undefined) {
        workingColumn = row;
    } else if (workingColumn != row) {
        workingColumn = row;
        $("#grid").append("<br/>");
    }
    var key = "light" + row + column + "";


    $("#grid").append('<button id ="' + key + '" class="btn btn-default lights">' + (column + 1) + " " + (row + 1) + "</button>");
}


window.onresize = function () {
    positionGrid();
};


function positionGrid() {
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


function doConnect() {
    websocket = new WebSocket(wsHost + "/webSocket");

    websocket.onopen = function (evt) {
        console.log(evt);
    };

    websocket.onclose = function (evt) {
        console.log(evt);
        error("closed connection");
    };

    websocket.onmessage = function (evt) {
        message(evt.data);
        message(queue,"#ffffe6");
        if(queue.length == 0){
          $("#updateMessage").text("");
        }
        if(queue.length > 0){
            $("#updateMessage").text("Elemente werden übertragen : "+(queue.length / 3));
            websocket.send(queue);
            queue = "";
            hideLoader();
        }

    };

    websocket.onerror = function (evt) {
        error("error");
    };
}


function writePing(message) {
    $('#pingOutput').append(message + '\n');
}

function writeStatus(message) {
    $("#statusOutput").val($("#statusOutput").val() + message + '\n');
}

function writeMessage(message) {
    $('#messageOutput').append(message + '\n')
}






function message(msg,onColor="#ffc107",offColor="#6c757d") {

    outSideHigh = 0;
    outSideLow = 0;

    lastMessage = msg;
    while (msg.length > 0) {
        var xChar = msg.charAt(0);
        if(xChar == 'o'){
            msg = proccessOutsideState(msg);
            continue;
        }
        var x = parseInt(msg.charAt(0));
        var y = parseInt(msg.charAt(1));
        var s = parseInt(msg.charAt(2));

        var key = "#light" + x + y + "";
        if (s == 0) {
            $(key).css("background-color", offColor);
            $(key).attr('onclick', 'change(' + x + ',' + y + ',' + 1 + ');');
        }
        else if (s == 1) {
            $(key).css("background-color", onColor);
            $(key).attr('onclick', 'change(' + x + ',' + y + ',' + 0 + ');');

        }
        else if (s == 7) {
            $(key).css("display", "none");
            $(key).attr('onclick', '');

        }
        msg = msg.substr(3);
    }
    updateOutside();
}

function proccessOutsideState(msg){
    var s = parseInt(msg.charAt(2));
    if(s == 1){
        outSideHigh++;
    }
    if(s == 0){
        outSideLow++;
    }
    return msg.substr(3);

}

function change(row, column, value) {
    var obj = "" + row + "" + column + "" + value + "";

    var key = "#light" + row + column + "";
     log("change :"+obj);

    $(key).css("background-color", "#ffffe6");
    showLoader();

     var counter = 0;
     var index = queue.indexOf(obj,counter);
     while(index != -1){
        if(index % 3 == 0){
        return;
        }
        var tmp = index;
        counter = index;
        index = queue.indexOf(obj,tmp);
     }

    queue += obj;
    $("#updateMessage").text("Puffer : "+(queue.length / 3));

}


function updateOutside(){
    $("#outside").text("Ein: "+outSideHigh+ " Aus: "+outSideLow);
}
function writeAction(action) {
    clearTimeout(sendTimeout);
     queue = "";
     hideLoader();
     sendQueue = false;
    websocket.send(action);

}


function error(err) {
    console.log(err);
    alert(err);
    setTimeout(
    function(){
    location.reload();
    },60);
}


function showLoader(){
    $("#loader").css("display","block");
}
function hideLoader(){
    $("#loader").css("display","none");
}