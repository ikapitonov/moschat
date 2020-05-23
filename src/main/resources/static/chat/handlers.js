let isWrite = false;
let usersWrite = new Map();
let interval = 400;
var userSub = null;
var adminSub = null;
let sendAddUser = false;
let Connected = false;
let TMPdata;

function onConnected() {
    Connected = true;
    userSub = stompClient.subscribe('/topic/' + sessionId + '/user', commonController);

    setTimeout(function () {
        if (adminAuth === true) {
            runAdminAuth(null);
            adminAuth = false;
        }
    }, 200);
    stompClient.subscribe('/topic/' + sessionId + '/common', commonController);
    // stompClient.send("/app/chat.addUser", {}, JSON.stringify({ type: "empty" }));
}

function socketReconnection(data) {
    if (data.role == "user") {
        saveSessionCookie(data.user);

        // веб сокет еще не успел подключиться
        sendAddUser = true;
        TMPdata = data;
        return ;
    }
    saveSessionCookie({
        admin: true,
        login: $("#admInputLogin").val(),
        password: $("#admInputPass").val()
    });
    if (userSub !== null) {
        userSub.unsubscribe();
        userSub = null;
    }
    adminSub = stompClient.subscribe('/topic/' + sessionId + '/' + data.token, adminController);

    $("#board").empty();
    stompClient.send("/app/chat.addUser", {}, JSON.stringify(data));
    loadingMessages();
}

function onError() {
    stompClient.disconnect();

    alert("Ошибка подключения");

    location.reload();
}

//удаление
function deleteComment(id) {
    if (confirm("Вы уверены?") === true) {
        stompClient.send("/app/chat.deleteItem", {}, JSON.stringify({ type: "comment", id: id }));
    }
}

function deleteMessage(id) {
    if (confirm("Вы уверены?") === true) {
        stompClient.send("/app/chat.deleteItem", {}, JSON.stringify({ type: "message", id: id }));
    }
}

function deleteItem(data) {
    if (data.item == "comment") {
        $("#comment_id_" + data.id).remove();
    }
    else if (data.item == "message") {
        $("#message" + data.id).remove();
    }
}

function adminController(payload) {
    commonController(payload);
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
        commentShow(data, data.clientMessage.id, getUserFields(data.fields));
    }
    else if (data.type == "WRITE") {
        usersWrite.set(data.session, [ data.username, interval ]);
    }
    else if (data.type == "DELETE") {
        deleteItem(data);
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
    if (sendAddUser === true && Connected === true) {
        sendAddUser = false;
        stompClient.send("/app/chat.addUser", {}, JSON.stringify(TMPdata));
    }

    if (userSub !== null && adminSub !== null) {
        userSub.unsubscribe();
        userSub = null;
    }
}, 400);
