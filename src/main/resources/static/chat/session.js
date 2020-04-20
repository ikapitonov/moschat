let cockieTimeDays = 2;

function saveSessionCookie (data) {
    deleteCookie(sessionId);
    setCookie(sessionId, JSON.stringify(data), cockieTimeDays);
}

function includeSessionCookie() {
    let dataStr = getCookie(sessionId);
    let data;
    let fields;

    if (dataStr !== null && dataStr.length > 0) {
        data = JSON.parse(dataStr);

        $("#userName").val(data.name);
        $("#userEmail").val(data.email == null || data.email == "" || data.email == "null" ? "" : data.email);
        $("#userPhone").val(data.phone > 0 ? data.phone : "");

        fields = data.fields == null || data.fields === undefined ? null : getUserFields(data.fields);

        for (let i = 0; fields != null && i < fields.length; i++) {
            $("#field" + i).val(fields[i]);
        }
    }
}

function readSessionCookie() {
    let dataStr = getCookie(sessionId);

    return dataStr !== null && dataStr.length > 0 ? JSON.parse(dataStr) : null;
}