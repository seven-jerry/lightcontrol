var websocket;
var settings;
var host ="http://localhost:8090";
var wsHost = "ws://localhost:8090";
var workingColumn;

var rows = 0;
var columns = 0;
function writePing(message) {
    $('#pingOutput').append(message + '\n');
}

function writeStatus(message) {
    $("#statusOutput").val($("#statusOutput").val() + message + '\n');
}

function writeMessage(message) {
    $('#messageOutput').append(message + '\n')
}

function prepareSettings(){
    settings.forEach(element => {
        if(element.outputRow > rows){
            rows = element.outputRow;
        }
        if(element.outputColumn > columns){
            columns = element.outputColumn;
        }
    });
}

function drawGrid(){
    $("#grid").empty();
    for (var row = 0; row < rows; row++) {
    for (var column = 0; column < columns; column++) {
            addLight(column,row);
        }
    }
}

window.onresize = function(){positionGrid();};
function positionGrid(){
    var marginLeft = 50 * (window.innerWidth/1300);
    var marginTop = 50 * (window.innerHeight/1000);

    var width = (window.innerWidth - marginLeft*(columns+1)) / columns;
    var height = (window.innerHeight - 58 - marginTop*(rows+1)) / rows;

    $(".lights").each(function(){
        $(this).css("margin-left",marginLeft+"px");
        $(this).css("margin-top",marginTop+"px");
        $(this).css("width",width+"px");
        $(this).css("height",height+"px");

    });
}

function addLight(column,row){
    if(workingColumn == undefined){
        workingColumn = row;
    } else if(workingColumn != row){
        workingColumn = row;
        $("#grid").append("<br/>");
    }
    var key = "light"+row+column+"";


 $("#grid").append('<button id ="'+key+'" class="btn btn-default lights">'+(column+1)+" "+(row+1)+"</button>");
}

function doConnect() {
    websocket = new WebSocket(wsHost+"/webSocket");

    websocket.onopen = function (evt) {
    };

    websocket.onclose = function (evt) {
    };

    websocket.onmessage = function (evt) {
        message(evt.data);
    };

    websocket.onerror = function (evt) {
        error(evt.data);
       // message('{"key" : "cu.usbmodemFA131","value" : "{001010020030040050060100110120130140150160200210221230240250260300310320330340350360400410420430440450460}"}');

    };
}

$(function() {
    $.getJSON( host+"/api/setting/list", function( data ) {
       settings = data;
       prepareSettings();
       drawGrid();
       positionGrid();
       doConnect();
      });
   
});


function message(msg){
    var object = JSON.parse(msg);
    if(object.value.length < (columns*rows)*3){
        return;
    }
    var value = object.value;
    while(value.length > 0){
        var x = parseInt(value.charAt(0));
        var y = parseInt(value.charAt(1));
        var s = parseInt(value.charAt(2));

        var key = "#light"+x+y+"";
        if(s == 0){
            $(key).css("background-color","#6c757d");
            $(key).attr('onclick','change('+x+','+y+','+1+');');
        }
        else if(s == 1){
            $(key).css("background-color","#ffc107");
            $(key).attr('onclick','change('+x+','+y+','+0+');');

        }
        value = value.substr(3);
    }
}


function change(row,column,value){
    var key = "";
    settings.forEach(element => {
        if(element.outputColumn > column && element.outputRow > row){
            key = element.serialport;
        }
    });

    var obj = {};
    obj["key"] = key;
    obj["value"] = ""+row+""+column+""+value+"";
    websocket.send(JSON.stringify(obj));
}

function turnAllOn(){
    var key = "";
    settings.forEach(element => {
        if(element.outputColumn > 0 && element.outputRow > 0){
            key = element.serialport;
        }
    });
    var obj = {};
    obj["key"] = key;

   var obj = {};
   obj["key"] = key;
   obj["value"] = "sah";
    websocket.send(JSON.stringify(obj));

}

function turnAllOff(){
    var key = "";
       settings.forEach(element => {
           if(element.outputColumn > 0 && element.outputRow > 0){
               key = element.serialport;
           }
       });
       var obj = {};
       obj["key"] = key;

      var obj = {};
      obj["key"] = key;
      obj["value"] = "sal";
       websocket.send(JSON.stringify(obj));
}

function turnHalfOn(){
  var key = "";
     settings.forEach(element => {
         if(element.outputColumn > 0 && element.outputRow > 0){
             key = element.serialport;
         }
     });
     var obj = {};
     obj["key"] = key;

    var obj = {};
    obj["key"] = key;
    obj["value"] = "shh";
     websocket.send(JSON.stringify(obj));
}



function error(err){
    alert(err);
}
