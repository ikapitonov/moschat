let isWrite = false;
let usersWrite = new Map();
let interval = 2000;

function onConnected(e) {
    $("#forms").css("display","none");
    $("#window").css("display","block");

    // у стомп клиента какая-то проблема с генерацией id, поэтому приходится ставить таймаунты
    stompClient.subscribe('/topic/' + role, commonController);
    setSessionId();
    stompClient.subscribe('/topic/' + "common", commonController);
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({ type: "empty" }));
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
    else if (data.type == "COMMENT") {
        comment(data, data.clientMessage.id);
    }
    else if (data.type == "WRITE") {
        usersWrite.set(data.session, [ data.username, interval ]);
    }
}

function sendMessage(text) {
    let message = {
        content: text
    };

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
}

function sendComment(id) {
    let text = $("#message" + id + " textarea").val();

    if (text.length === 0)
        return ;

    stompClient.send("/app/chat.sendComment", {}, JSON.stringify({content: text, id: id}));
}

function userWrite() {
    isWrite = true;
}

setInterval(function () {
    usersWrite.forEach(function(value,key){
        // key - уникальный ключ
        // value[0] - имя
        // value[1] - задержка

        if (/*key == sessionId ||*/ value[1] < 0) {
            usersWrite.delete(key);
            $("#userwrite_" + key).remove();
            return ;
        }
        if ($("#userwrite_" + key).length === 0) {
            $("#usersWrite").append("<div id=\"userwrite_" + key + "\">" + value[0] + " Набирает сообщение</div>");
        }
        usersWrite.set(key, [ value[0], value[1] - interval ])
    });

},interval);

setInterval(function () {
    if (isWrite === true) {
        stompClient.send("/app/chat.userWrite", {}, JSON.stringify({type: "empty"}));

        isWrite = false;
    }
}, 500);

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
    let str = "<div class=\"card message_item mt-3\"  id=\"message" + data.id + "\"><div class=\"card-header\">" + data.name + "</div>";

    str += "<div class=\"card-body\">";
    str += "<div>id: " + data.id +  "</div>";
    str += "<div>Роль: " + data.role +  "</div>";
    str += "<div>Время: " + data.date +  "</div>";

    // телефон и почту видят только админы. Юзеру всегда приходит null или 0 ("email":null,"phone":0), можно поставить if на глоб. переменную role
    str += "<div>Почта: " + data.email +  "</div>";
    str += "<div>Телефон: " + data.phone +  "</div>";
    str += "<div>Сообщение: <pre>" + data.content + "</pre></div>";

    // комментарий
    str += "<div class=\"card mt-3\" style=\"border: none;\"><h5>Оставить комментарий</h5><textarea class=\"form-control\"></textarea>";
    str += "<button class=\"btn btn-primary send_comment mt-3\" onclick=\"sendComment(" + data.id + ")\">Отправить</button>";
    str += "<div class=\"comment\"></div></div>";

    if (where == "START") {
        $("#board").prepend(str);
        return ;
    }
    $("#board").append(str);

    for (let i = 0; data.comments != null && data.comments.length !== 0 && i < data.comments.length; i++) {
        comment(data.comments[i], data.id);
    }
}

function comment(data, messageId) {
    let str = "<div class=\"card mt-3\"><div class=\"card-header\">" + data.name + "</div>";

    str += "<div class=\"card-body\">";
    str += "<div>id: " + data.id +  "</div>";
    str += "<div>Роль: " + data.role +  "</div>";
    str += "<div>Время: " + data.date +  "</div>";

    // телефон и почту видят только админы. Юзеру всегда приходит null или 0 ("email":null,"phone":0), можно поставить if на глоб. переменную role
    str += "<div>Почта: " + data.email +  "</div>";
    str += "<div>Телефон: " + data.phone +  "</div>";
    str += "<div>Сообщение: <pre>" + data.content + "</pre></div>";

    $("#message" + messageId + " .comment").append(str);
}