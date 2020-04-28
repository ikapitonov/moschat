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

    $("#textarea-Btn-button").click( function (e) {
        if (isAuth === false) {
            showDropdown();
            showSignup();
            return ;
        }
        showDropdown();
        stompClient.send("/app/chat.removeUser", {}, JSON.stringify({}));
        $("#textarea-Btn-button").text("Войти");

        if (role == "admin") {
            $("#board").empty();
            loadingMessages();
            isAuth = false;
            token = null;
            setTimeout(function () {
                adminSub.unsubscribe();
                adminSub = null;
            }, 400);

            setTimeout(function () {
                userSub = stompClient.subscribe('/topic/' + sessionId + '/user', commonController);
            }, 500);
        }
        else {
            deleteCookie(sessionId);
            isAuth = false;
        }
        role = null;
    })
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
                $("#textarea-Btn-button").text("Войти");
                alert("Ошибка авторизации. Проверьте данные");
                return ;
            }
            $("#textarea-Btn-button").text("Выйти");
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