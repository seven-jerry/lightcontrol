if (typeof masterhost === "undefined") {
    var host = "http://" + location.host;
} else {
    var host = masterhost;

}
var low = 0;
var high = 0;
var disabled = 0;
var outsideHigh = 0;
var outsideLow = 0;
var commands = [];
var displayedCommands = {};
var template = '<li class="list-group-item sized" style="margin-top:20px;{style}">';
template += '<a class="link" href="#"  onclick="writeAction(\'{command}\')">{label} : {count}</a>';
template += '</li>';

var stateTemplate = '<li class="list-group-item sized" style="margin-top:20px; {style}">';
stateTemplate += '{label}';
stateTemplate += '</li>';
var clientTemplate = '<li style="display: inline"><input type="checkbox" class="sendToClient" name="{id}" checked/><span style="margin-left:5px;">{label}</span></li>';


var errorTemplate = '<li class="list-group-item sized" style="margin-top:20px; background-color: #D8000C">';
errorTemplate += '{label}';
errorTemplate += '</li>';


var lineBreak = "<br/>";
var clients = [];
$(function () {
    setInterval(function () {
        $.getJSON(host + "/master/list", function (data) {
            $("#stateWrapper").empty();
            $("#inputWrapper").empty();
            $("#errors").empty();

            commands = [];
            states = JSON.parse(data.states);
            errors = JSON.parse(data.errors);
            last_updated = data.last_updated;
            $("#date").text(last_updated);
            for (var key in states) {
                if (states.hasOwnProperty(key)) {
                    var endpoint = states[key];
                    updateState(key, endpoint.id, endpoint.state);
                    updateInputs(key, endpoint.labeledInputs);
                    updateCommands(endpoint.commands);
                    updateClientList(key);
                }
            }
            for (var key in errors) {
                if (errors.hasOwnProperty(key)) {
                    var error = errors[key];
                    var error = errorTemplate.replace("{label}",error);
                    $("#errors").append(error);

                }
            }

            for (var i in commands) {
                var command = commands[i];
                if(displayedCommands.hasOwnProperty(command.command)){
                    continue;
                }
                displayedCommands[command.command] = command;
                var actionTemplate = template.replace("{label}", command.label);
                actionTemplate = actionTemplate.replace("{command}", command.command);
                actionTemplate = actionTemplate.replace("{count}", command.count);
                $("#commandWrapper").append(actionTemplate);
            }


        });
    }, 1000);
});


function updateState(label, id, msg) {
    low = 0;
    high = 0;
    disabled = 0;
    outsideLow = 0;
    outsideHigh = 0;


    if (msg == undefined || msg == null) return;
    while (msg.length > 0) {
        var s = parseInt(msg.charAt(2));
        if (msg.charAt(0) == 'o') {
            if (s == 0) {
                outsideLow++;
            } else if (s == 1) {
                outsideHigh++;
            }
            msg = msg.substr(3);
            continue;
        }
        if (s == 0) {
            low++;
        } else if (s == 1) {
            high++;
        }
        else {
            disabled++;
        }
        msg = msg.substr(3);
    }
    var state = "<span style='font-weight: bold;'>" + label + "</span><hr/>" + " Aus : " + low + " Ein : " + high + lineBreak + " Ausen Ein : " + outsideHigh + " Ausen Aus : " + outsideLow;
    var st = stateTemplate.replace("{label}", state);
    if (high / low > 1.2) {
        st = st.replace("{style}", "background-color:#ffb3b3;");
    }
    else if (high > 0 || outsideHigh > 0) {
        st = st.replace("{style}", "background-color:#fff2e6;");
    }
    else {
        st = st.replace("{style}", "background-color:#e6ffee;");
    }
    st.replace("{id}", id);
    $("#stateWrapper").append(st);
}

function updateCommands(l_commands) {
    for (var i in l_commands) {
        var l_command = l_commands[i];
        var hasFound = false;
        for (var j in commands) {
            var command = commands[j];
            if (command.command == l_command.command) {
                command["count"]++;
                hasFound = true;
            }
        }
        if (hasFound == false) {
            l_command["count"] = 1;
            commands.push(l_command);
        }
    }
}

function updateInputs(label, obj) {

    var inputString = "<span style='font-weight: bold;'>" + label + "</span><hr/>";
    if (obj == undefined || obj == null) return;
    for (key in obj) {
        if(!obj.hasOwnProperty(key)){
            continue;
        }
        var value = obj[key];

        inputString += " " + key + " => " + value + " " + lineBreak;
    }

    var st = stateTemplate.replace("{label}", inputString);
    st = st.replace("{style}", "");
    $("#inputWrapper").append(st);
}

function writeAction(action) {
    var additional = additionalWriteParameters();

    $.getJSON(host+"/master/execute?command=" + action + "&ids=" + additional, function (data) {
        console.log(data);
    });
}

function updateClientList(key) {
    if (!clients.includes(key)) {
        clients.push(key);
        var t = clientTemplate.replace("{label}",key);
        t = t.replace("{id}",key);
        $("#clients").append(t);
    }
}


function updateErrors(key) {
        var t = clientTemplate.replace("{label}",key);
        t = t.replace("{id}",key);
        $("#clients").append(t);

}

function additionalWriteParameters() {
    var result = "";

    $(".sendToClient").each(function (id,obj) {
        if ($(obj).prop('checked')) {
            result += $(obj).attr("name") + ",";
        }
    })
    return result;
}