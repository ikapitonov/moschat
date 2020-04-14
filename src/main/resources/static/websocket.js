//let domen = location.origin;
let domen = "https://secret-falls-85852.herokuapp.com";
let stompClient;
let role;
let webSocket;

function wsConnection() {
    let url = domen + "/ws";

    webSocket = new SockJS(url);

    stompClient = Stomp.over(webSocket);
    //нижепредставленной строчкой можно запретить стомпу писать в консоль
    //stompClient.debug = null;
    stompClient.connect({}, onConnected, onError);
}