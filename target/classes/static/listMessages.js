let limit = 50;
let offsetClassHtml = "message_item";

$("#loading").click(loadingMessages);

function loadingMessages() {
    $.ajax({
        url: domen + "/listMessages",
        type: 'GET',
        data: {
            offset: getOffset(),
            limit: limit
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
        message(data.content[i], "APPEND");
    }
}

// html class для подсчета общего числа (чтобы сместить offset)
function getOffset() {
    let tmp = document.querySelectorAll("." + offsetClassHtml);

    return tmp == null ? 0 : tmp.length;
}