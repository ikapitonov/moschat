let limit = 50;

$("#loading").click(loadingMessages);

function loadingMessages() {
    console.log(sessionId);
    $.ajax({
        url: domen + "/chat/listMessages",
        type: 'GET',
        data: {
            offset: getOffset(),
            limit: limit,
            token: token,
            sessionId: sessionId
        },
        contentType: 'application/json',
        success: function(response) {
            console.log(response);
            listMessages(response);
        }
    });
}

function listMessages(data) {

    if (data == null || data.length == 0) {
        $("#loading").css("display","none");
        return ;
    }
    if (data.length < limit) {
        $("#loading").css("display", "none");
    }
    else {
        $("#loading").css("display", "block");
    }

    for (let i = 0; i < data.length; ++i) {
        massageShow(data[i], "APPEND");
    }
}

function getOffset() {
    let tmp = document.querySelectorAll(".message_item");

    return tmp == null ? 0 : tmp.length;
}