let token = null;
let sessionId = document.getElementById("sessionId").innerHTML;

$(document).ready(function() {

    $("#adminAuth").click(function (e) {
        e.preventDefault();
        let login = $("#admInputLogin").val();
        let pass = $("#admInputPass").val();

        if (login.length === 0 && pass.length === 0) {
            alert("Заполните необходимые поля");
            return;
        }

        tryAuth("admin", {login: login, password: pass});
    });

    $("#userAuth").click(generateAuth);

    $("#closeForm1").click(function (e) {
        e.preventDefault();
        hideSignup();
    });
    $("#closeForm2").click(function (e) {
        e.preventDefault();
        hideSignup();
    });
});

function generateAuth(e) {
    let data = readSessionCookie();


    if (e !== null) {
        e.preventDefault();
    }

    let userName = $("#userName").val();
    let userEmail = $("#userEmail").val();
    let userPhone = $("#userPhone").val();
    let array = [];
    let tmp;

    for (let i = 0; i < 5; i++) {
        tmp = $("#field" + i);

        if (tmp.length === 0)
            break ;
        array.push(tmp.val());
    }
    console.log(array);
    if (userName.length === 0 || (userEmail.length === 0 && userPhone.length === 0)) {
        alert("Заполните поля");
        return ;
    }
    tryAuth("user", {
        name: userName,
        email: userEmail,
        phone: userPhone,
        fields: array,
        sessionId: sessionId,
        id: data === null || data.id === undefined ? 0 : data.id,
        token: data === null || data.token === undefined ? null : data.token
    });
}

function tryAuth (type, obj) {
    $.ajax({
        url: domen + "/auth/" + type,
        type: 'POST',
        data: JSON.stringify(obj),
        contentType: 'application/json',
        success: function(response) {
            if (response.status === false) {
                isAuth = false;
                alert("Ошибка авторизации. Проверьте данные");
                return ;
            }
            console.log(response);
            role = type;
            token = response.token;
            isAuth = true;
            hideSignup();
            socketReconnection(response);
        }
    });
}

function getUserFields(str) {
    return str === null || str === undefined ? null : str.split("@|$|@");
}