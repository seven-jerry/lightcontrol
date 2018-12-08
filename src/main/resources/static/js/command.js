var host = "http://" + location.host;
var low = 0;
var high = 0;
var disabled = 0;
var outsideHigh = 0;
var outsideLow = 0;


var template = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
template += '<a class="link" href="#"  onclick="writeAction(\'{command}\')">{label}</a>';
template += '</li>';

var stateTemplate = '<li class="list-group-item sized" style="margin-top:20px; {style}">';
stateTemplate += '{label}';
stateTemplate += '</li>';

$(function () {
    setInterval( function () {
        $.getJSON(host + "/setting/command/list", function (data) {
            setting = data;
            updateState(data.state);
            updateCommands(data.commands);
            updateInputs(data.inputs);
        });
    },1000);
});


function updateState(msg) {
    low = 0;
    high = 0;
    disabled = 0;
    outsideLow = 0;
    outsideHigh = 0;

    $("#stateWrapper").empty();

    if(msg == undefined || msg == null) return;
    while (msg.length > 0) {
        var s = parseInt(msg.charAt(2));
        if(msg.charAt(0) == 'o'){
            if(s == 0){
                outsideLow++;
            } else if(s == 1){
                outsideHigh++;
            }
            msg = msg.substr(3);
            continue;
        }
        if(s == 0){
            low++;
        } else if(s == 1){
            high++;
        }
        else {
            disabled++;
        }
        msg = msg.substr(3);
    }
    var state = "Aus : "+low+" Ein : "+high+" | Ausen Ein : "+outsideHigh+" Ausen Aus : "+outsideLow;
    var st = stateTemplate.replace("{label}",state);
    st.replace("{style}","");
        $("#stateWrapper").append(st);
}

function updateCommands(commands) {
    $("#commandWrapper").empty();
    for(var i in commands){
        var action = commands[i];
        var actionTemplate = template.replace("{label}", action.label);
        actionTemplate = actionTemplate.replace("{command}", action.command);
        actionTemplate = actionTemplate.replace("{style}", "");

        $("#commandWrapper").append(actionTemplate);
    }
}

function updateInputs(msg) {
    $("#inputWrapper").empty();
    var inputString = " Inputs : ";
    if(msg == undefined || msg == null) return;
    while (msg.length > 0) {
        var x = parseInt(msg.charAt(0));
        var y = parseInt(msg.charAt(1));
        var s = parseInt(msg.charAt(2));
       inputString += " "+ y + " : " + s + " | ";
        msg = msg.substr(3);
    }

    var st = stateTemplate.replace("{label}",inputString);
    st.replace("{style}","");

    $("#inputWrapper").append(st);
}

function writeAction(action) {
    $.getJSON("/setting/command/execute?command="+action, function (data) {
        console.log(data);
    });
}