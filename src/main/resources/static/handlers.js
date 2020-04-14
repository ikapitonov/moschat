let isWrite = false;
let usersWrite = new Map();
let interval = 2000;
let userSub;

function onConnected() {
    userSub = stompClient.subscribe('/topic/user', commonController);
    stompClient.subscribe('/topic/' + "common", commonController);
   // stompClient.send("/app/chat.addUser", {}, JSON.stringify({ type: "empty" }));
}

function socketReconnection(data) {
    if (data.role == "user") {
        deleteCookie("name");
        deleteCookie("phone");
        deleteCookie("email");
        setCookie("name", data.name, 7);
        setCookie("phone", data.phone, 7);
        setCookie("email", data.email, 7);

        stompClient.send("/app/chat.addUser", {}, JSON.stringify(data));
        return ;
    }
    userSub.unsubscribe();
    stompClient.subscribe('/topic/' + data.token, commonController);

    $("#board").empty();
    stompClient.send("/app/chat.addUser", {}, JSON.stringify(data));
    loadingMessages();
}

function onError() {
    stompClient.disconnect();

    alert("Ошибка подключения");

    location.reload();
}

function commonController(payload) {
    let data = JSON.parse(payload.body);

    console.log(data);
    if (data.type == "ADD") {
        addUser(data);
    }
    else if (data.type == "REMOVE") {
        removeUser(data);
    }
    else if (data.type == "MESSAGE") {
        massageShow(data, "START");
    }
    else if (data.type == "COMMENT") {
        commentShow(data, data.clientMessage.id);
    }
    else if (data.type == "WRITE") {
        usersWrite.set(data.session, [ data.username, interval ]);
    }
}

function sendMessage() {
    if (!isAuth) return ;
    let text = $("#textarea").val().trim();

    if (text.length === 0) return ;

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({content: text}));

    $("#textarea").val('');
    autosize.update($("#textarea"));
    //resizeTextarea(document.getElementById('textarea'));//меняет размер текстого поля
}

function sendComment(id) {
    if (!isAuth) return ;

    let text = $("#message" + id + " textarea").val();

    if (text.length === 0)
        return ;

    stompClient.send("/app/chat.sendComment", {}, JSON.stringify({content: text, id: id}));


    $("#message" + id + " textarea").val('');
    autosize.update($("#message" + id + " textarea"));
}

$("#textarea").keyup(userWrite);

function userWrite() {
    if (!isAuth) return ;
    isWrite = true;
}

setInterval(function () {
    usersWrite.forEach(function(value,key){
        // key - уникальный ключ
        // value[0] - имя
        // value[1] - задержка

        if (value[1] < 0) {
            usersWrite.delete(key);
            return ;
        }
        usersWrite.set(key, [ value[0], value[1] - interval ])
    });
    let len = usersWrite.size;

    if (len == 0) {
        $("#writes").css("color", "#030027");
    }
    else if (len == 1) {
        $("#writes").css("color", "#FFFFFF");
        $("#writes").text("Сейчас набирает сообщение 1 пользователь...");
    }
    else {
        $("#writes").css("color", "#FFFFFF");
        $("#writes").text("Сейчас набирают сообщение " + len + " " + declOfNum(len, [ 'пользователь', 'пользователя', 'пользоваталей' ]) + "...");
    }
},interval);

function declOfNum(number, titles) {
    cases = [2, 0, 1, 1, 1, 2];
    return titles[ (number%100>4 && number%100<20)? 2 : cases[(number%10<5)?number%10:5] ];
}

setInterval(function () {
    if (isWrite === true) {
        stompClient.send("/app/chat.userWrite", {}, JSON.stringify({type: "empty"}));

        isWrite = false;
    }
}, 400);
