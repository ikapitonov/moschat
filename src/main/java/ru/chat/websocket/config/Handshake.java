package ru.chat.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import ru.chat.utils.Html;
import ru.chat.websocket.permissions.Admin;
import ru.chat.websocket.permissions.User;

import java.net.URLDecoder;
import java.util.Map;

public class Handshake implements HandshakeInterceptor {
    @Autowired
    private User user;

    @Autowired
    private Admin admin;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(request.getURI().toString()).build().getQueryParams();
            String role = getFromMapWithTrim(parameters, "role");

            if (role == null || role.isEmpty())
                return closeConnection(response);

            if (role.equals("user")) {
                String name = getFromMapWithTrim(parameters, "name");
                if (name != null)
                    name = URLDecoder.decode(name, "UTF-8").trim();
                name = Html.fullDecode(name);

                if (!user.isAllowed() || !user.validateName(name))
                    return closeConnection(response);

                if (!user.validatePhone(attributes, getFromMapWithTrim(parameters, "phone")) &&
                        !user.validateEmail(attributes, getFromMapWithTrim(parameters, "email")))
                    return closeConnection(response);

                attributes.put("role", "user");
                attributes.put("name", name);

                return true;
            }
            if (role.equals("admin")) {
                if (!admin.isAllowed(getFromMap(parameters, "login"), getFromMap(parameters, "password")))
                    return closeConnection(response);

                attributes.put("role", "admin");
                attributes.put("name", Admin.name);
                return true;
            }
        }
        return closeConnection(response);
    }

    private boolean closeConnection(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.close();
        return false;
    }

    private String getFromMap(MultiValueMap<String, String> parameters, String what) {
        try {
            return parameters.get(what).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    private String getFromMapWithTrim(MultiValueMap<String, String> parameters, String what) {
        try {
            return parameters.get(what).get(0).trim();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        //
    }
}