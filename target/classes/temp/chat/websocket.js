//let domen = location.origin;
let domen = "http://localhost:8080";
let stompClient;
let role;
let webSocket;

function wsConnection() {
    let url = domen + "/init/ws?sessionId=" + sessionId;

    webSocket = new SockJS(url);

    stompClient = Stomp.over(webSocket);
    //нижепредставленной строчкой можно запретить стомпу писать в консоль
    stompClient.debug = null;
    stompClient.connect({}, onConnected, onError);
}
