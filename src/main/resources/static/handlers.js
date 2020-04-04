let limit = 50;
let offsetClassHtml = "message_item";

function onConnected() {
    $("#forms").css("display","none");
    $("#window").css("display","block");

    stompClient.subscribe('/topic/' + role, commonController);
    setSessionId();
    stompClient.subscribe('/topic/' + role + "/" + sessionId, myController);
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({ type: "empty" }));
    stompClient.send("/app/chat.listMessages", {}, JSON.stringify({ offset: getOffset(), limit: limit }));
}

function onError() {
    $("#forms").css("display","block");
    $("#window").css("display","none");

    stompClient.disconnect();

    alert("Ошибка подключения, проверьте данные");
}

function commonController(payload) {
    let data = JSON.parse(payload.body);

    if (data.type == "ADD") {
        add(data);
    }
    else if (data.type == "REMOVE") {
        remove(data);
    }
    else if (data.type == "MESSAGE") {
        message(data, "START");
    }
}

function myController(payload) {
    let data = JSON.parse(payload.body);

    console.log(data);

    if (data.messages == null || data.messages.length == 0) {
        $("#loading").css("display","none");
        return ;
    }
    if (data.messages.length < limit)
        $("#loading").css("display","none");

    for (let i = 0; i < data.messages.length; ++i) {
        message(data.messages[i], "APPEND");
    }
}

function loadingMessages() {
    stompClient.send("/app/chat.listMessages", {}, JSON.stringify({ offset: getOffset(), limit: limit}));
}

function sendMessage(text) {
    let message = {
        content: text
    };

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
}

// html class для подсчета общего числа (чтобы сместить offset)
function getOffset() {
    let tmp = document.querySelectorAll("." + offsetClassHtml);

    return tmp == null ? 0 : tmp.length;
}

// html
function add(data) {
    let str = "<div class=\"card mt-3\"><div class=\"card-header\">" + data.name + "</div>";

    str += "<div class=\"card-body\">";
    str += "<div>Роль: " + data.role +  "</div>";
    str += "<div>Время: " + data.date +  "</div>";
    str += "<div>Событие: подключен к чату</div>";
    str += "</div></div>";

    $("#board").prepend(str);
}

function remove(data) {
    let str = "<div class=\"card mt-3\"><div class=\"card-header\">" + data.name + "</div>";

    str += "<div class=\"card-body\">";
    str += "<div>Роль: " + data.role +  "</div>";
    str += "<div>Время: " + data.date +  "</div>";
    str += "<div>Событие: отключился от чата</div>";
    str += "</div></div>";

    $("#board").prepend(str);
}

function message(data, where) {
    let str = "<div class=\"card message_item mt-3\"><div class=\"card-header\">" + data.name + "</div>";

    str += "<div class=\"card-body\">";
    str += "<div>id: " + data.id +  "</div>";
    str += "<div>Роль: " + data.role +  "</div>";
    str += "<div>Время: " + data.date +  "</div>";

    // телефон и почту видят только админы. Юзеру всегда приходит null или 0 ("email":null,"phone":0), можно поставить if на глоб. переменную role
    str += "<div>Почта: " + data.email +  "</div>";
    str += "<div>Телефон: " + data.phone +  "</div>";
    str += "<div>Сообщение: <pre>" + data.content + "</pre></div>";
    str += "</div></div>";

    if (where == "START") {
        $("#board").prepend(str);
        return ;
    }
    $("#board").append(str);
}