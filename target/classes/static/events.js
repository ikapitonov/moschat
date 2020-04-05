$(document).ready(function() {

    $("#subUser").click(function (e) {
        e.preventDefault();

        let name = $("#input").val().trim();
        let phone = $("#input2").val().trim();
        let email = $("#input3").val().trim();

        // валидность phone и email
        if (name.length === 0 || (phone.length === 0 && email.length === 0)) {
            alert("Заполните поля");
            return ;
        }
        wsRunUser(name, phone, email);
    });


    $("#subAdm").click(function (e) {
        e.preventDefault();

        let login = $("#input4").val().trim();
        let password = $("#input5").val().trim();

        if (login.length === 0 || password.length === 0) {
            alert("Заполните поля");
            return ;
        }
        wsRunAdmin(login, password);
    });

    $("#sendMessage").click(function (e) {
        e.preventDefault();

        let text = $("#textarea").val().trim();

        if (text.length === 0)
            return ;
        sendMessage(text);
    });

    $("#loading").click(loadingMessages);

    $("#textarea").keyup(userWrite);
});