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

import ru.chat.repositories.SessionRepo;
import ru.chat.utils.Html;
import ru.chat.websocket.model.Session;
import ru.chat.websocket.permissions.Admin;
import ru.chat.websocket.permissions.User;

import java.net.URLDecoder;
import java.util.Map;

public class Handshake implements HandshakeInterceptor {
    @Autowired
    private User user;

    @Autowired
    private Admin admin;

    @Autowired
    private SessionRepo sessionRepo;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(request.getURI().toString()).build().getQueryParams();
            int id;
            Session session;

            try {
                id = Integer.parseInt(parameters.get("sessionId").get(0));
                session = sessionRepo.findById(id).get();

                if (session.getId() > 0) {
                    attributes.put("session", session);
                    return true;
                }
            }
            catch (Exception e) {
                return closeConnection(response);
            }
        }
        return closeConnection(response);
    }

    private boolean closeConnection(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.close();
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        //
    }
}