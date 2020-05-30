let cockieTimeDays = 2;
let adminAuth = false;

function saveSessionCookie (data) {
    deleteCookie(sessionId);
    setCookie(sessionId, JSON.stringify(data), cockieTimeDays);
}

function usePhone() {
    return $("#use_phone").length > 0;
}

function includeSessionCookie() {
    let dataStr = getCookie(sessionId);
    let data;
    let fields;

    if (dataStr !== null && dataStr.length > 0) {
        data = JSON.parse(dataStr);

        if (data.admin !== undefined && data.admin !== null && data.admin === true) {

            $("#admInputLogin").val(data.login);
            $("#admInputPass").val(data.password);
            isAuth = true;
            adminAuth = true;
            return ;
        }
        $("#userName").val(data.name);
     //   $("#userEmail").val(data.email == null || data.email == "" || data.email == "null" ? "" : data.email);

        if (usePhone()) {
            $("#userPhone").val(data.phone != null && data.phone != "" ? data.phone : "");
        }

        fields = data.fields == null || data.fields === undefined ? null : getUserFields(data.fields);

        for (let i = 0; fields != null && i < fields.length; i++) {
            $("#field" + i).val(fields[i]);
        }

        if (usePhone()) {
            if (data.name !== null && data.name.length > 0 && data.phone > 0) {
                isAuth = true;
                generateAuth(null);
            }
        }
        else {
            if (data.name !== null && data.name.length > 0) {
                isAuth = true;
                generateAuth(null);
            }
        }
    }
}

function readSessionCookie() {
    let dataStr = getCookie(sessionId);

    return dataStr !== null && dataStr.length > 0 ? JSON.parse(dataStr) : null;
}
