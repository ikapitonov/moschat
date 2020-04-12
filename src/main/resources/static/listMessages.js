let limit = 50;

$("#loading").click(loadingMessages);

function loadingMessages() {
    $.ajax({
        url: domen + "/listMessages",
        type: 'GET',
        data: {
            offset: getOffset(),
            limit: limit,
            token: token
        },
        contentType: 'application/json',
        success: function(response) {
            listMessages(response);
        }
    });
}

function listMessages(data) {

    if (data.content == null || data.content.length == 0) {
        $("#loading").css("display","none");
        return ;
    }
    if (data.content.length < limit) {
        $("#loading").css("display", "none");
    }
    else {
        $("#loading").css("display", "block");
    }

    for (let i = 0; i < data.content.length; ++i) {
        massageShow(data.content[i], "APPEND");
    }
}

function getOffset() {
    let tmp = document.querySelectorAll(".message_item");

    return tmp == null ? 0 : tmp.length;
}