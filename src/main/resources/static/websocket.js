//let domen = "https://secret-falls-85852.herokuapp.com";
let domen = "http://localhost:8080";
let stompClient;
let role;
let webSocket;

// после перезарузки страницы

function wsRunAdmin(login, password) {
    let httpGet = "?role=admin&login=" + login + "&password=" + password;

    role = "admin";
    wsConnection(httpGet);
}

function wsRunUser(name, phone, email) {
    let httpGet = "?role=user&name=" + name + "&phone=" + phone + "&email=" + email;

    role = "user";
    wsConnection(httpGet);
}

function wsConnection(httpGet) {
    let url = domen + "/ws" + httpGet;

    webSocket = new SockJS(url);

    stompClient = Stomp.over(webSocket);
    //нижепредставленной строчкой можно запретить стомпу писать в консоль
    //stompClient.debug = null;
    stompClient.connect({}, onConnected, onError);
}

function setSessionId() {
    let arr = webSocket._transport.url.split('/');

    for (let i = 0; i < arr.length; ++i) {
        if (arr[i].indexOf("websocket") == 0) {
            sessionId = arr[i - 1];
            return ;
        }
    }
}