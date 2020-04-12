function addUser(data) {
    let str = "<div class=\"oneMessageG\">";

    if (data.role == "user") {
        str += "<p class=\"textJoinChatG\">Пользователь <span class=\"nameUserJoinG\">" + data.name + "</span> подключился к чату</p>";
    }
    else {
        str += "<p class=\"textJoinChatG\"><span class=\"nameUserJoinG\">" + data.name + "</span> подключился к чату</p>";
    }
    str += "</div>";
    $("#board").prepend(str);
}

function removeUser(data) {
    let str = "<div class=\"oneMessageG\">";

    if (data.role == "user") {
        str += "<p class=\"textJoinChatG\">Пользователь <span class=\"nameUserJoinG\">" + data.name + "</span> отключился от чата</p>";
    }
    else {
        str += "<p class=\"textJoinChatG\"><span class=\"nameUserJoinG\">" + data.name + "</span> отключился от чата</p>";
    }
    str += "</div>";
    $("#board").prepend(str);
}

function massageShow(data, where) {
    let str = "<div class=\"oneMessageG message_item\" id=\"message" + data.id + "\">";

    str += "<div class=\"blockMessageMG\">";
    if (data.role == "admin") {
        str += "<img class=\"imgUserG\" src=\"admin.png\">";
    }
    else {
        str += "<img class=\"imgUserG\" src=\"user.png\">";
    }

    str += "<div class=\"rightMOMG\">";
    str += "<div class=\"titleRightMOMG\">";
    str += "<h3>" + data.name + "</h3>";
    if (data.email != null && data.email != "") {
        str += "<h5>" + data.email + "</h5>";
    }
    if (data.phone > 0) {
        str += "<h5>" + data.phone + "</h5>";
    }
    str += "<h5>" + data.date + "</h5>";
    str += "</div>";
    str += "<div class=\"textMessageG\">" + data.content + "</div>";
    str += "</div>";

    str += "</div>";
    str += "<div class=\"replyOMG\"><div class=\"comment_show\"></div>";
    str += "<div class=\"replyInputBlock\">\n<textarea onclick=\"showSignup()\" onkeyup=\"userWrite()\" class=\"textareaG textareaReplyG\" placeholder=\"Введите ответ\" name=\"inputMessage\"></textarea><div class=\"buttonReply\" onclick=\"sendComment(" + data.id + ")\">Ответить</div></div>";
    str+= "</div>";
    str += "</div>";

    if (where == "START") {
        $("#board").prepend(str);
        return ;
    }
    $("#board").append(str);

    for (let i = 0; data.comments != null && data.comments.length !== 0 && i < data.comments.length; i++) {
        commentShow(data.comments[i], data.id);
    }
}

function commentShow(data, messageId) {
    let str = "<div class=\"blockMessageMG\">";

    if (data.role == "admin") {
        str += "<img class=\"imgUserG\" src=\"admin.png\">";
    }
    else {
        str += "<img class=\"imgUserG\" src=\"user.png\">";
    }
    str += "<div class=\"rightMOMG\">";
    str += "<div class=\"titleRightMOMG\">";
    str += "<h3>" + data.name + "</h3>";
    if (data.email != null && data.email != "") {
        str += "<h5>" + data.email + "</h5>";
    }
    if (data.phone > 0) {
        str += "<h5>" + data.phone + "</h5>";
    }
    str += "<h5>" + data.date + "</h5>";
    str += "</div>";
    str += "<div class=\"textMessageG\">" + data.content + "</div>";
    str += "</div>";
    str += "</div>";

    $("#message" + messageId + " .comment_show").append(str);
}