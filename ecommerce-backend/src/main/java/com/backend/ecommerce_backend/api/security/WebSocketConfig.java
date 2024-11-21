package com.backend.ecommerce_backend.api.security;

import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.service.UserService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

import java.util.Map;


@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ApplicationContext context;
    private final JWTRequestFilter jwtRequestFilter;
    private static final AntPathMatcher MATCHER  = new AntPathMatcher();
    private final UserService userService;


    public WebSocketConfig(ApplicationContext context, JWTRequestFilter jwtRequestFilter, UserService userService) {
        this.context = context;
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("**").withSockJS();//set allowed origin is only to bypass cors

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");

    }

    private AuthorizationManager<Message<?>> makeMassageAuthorizationManager(){
        MessageMatcherDelegatingAuthorizationManager.Builder messages = new MessageMatcherDelegatingAuthorizationManager.Builder();
        // authenticate messages to path and permit messages to any other and deny all messages of type message
        messages.simpDestMatchers("/topic/user/**").authenticated()
                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()
                .anyMessage().permitAll();
        return messages.build();

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationManager<Message<?>> authorizationManager = makeMassageAuthorizationManager();
        AuthorizationChannelInterceptor authInterceptor = new AuthorizationChannelInterceptor(authorizationManager);

        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
        authInterceptor.setAuthorizationEventPublisher(publisher);

        registration.interceptors(jwtRequestFilter,authInterceptor
                , new RejectClientMessagesOnChannelsChannelInceptors()
                , new DestinationLevelAuthorizationChannelInterceptor());
    }

    private class RejectClientMessagesOnChannelsChannelInceptors implements ChannelInterceptor {
        private String[] paths = new String[] {
                "/topic/user/*/address",
        };
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if(message.getHeaders().get("SimpMessageType").equals(SimpMessageType.MESSAGE)){
                String destination = (String) message.getHeaders().get("simpDestination");
                for (String path : paths) {
                    if(MATCHER.match(path,destination)){
                        message = null;
                    }
                }
            }
            return message;
        }
    }
    private class DestinationLevelAuthorizationChannelInterceptor implements ChannelInterceptor {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if(message.getHeaders().get("SimpMessageType").equals(SimpMessageType.MESSAGE)){
                String destination = (String) message.getHeaders().get("simpDestination");
                Map<String,String> params = MATCHER.extractUriTemplateVariables("/topic/user/{userId}",destination);
                try{
                    long userId = Long.parseLong(params.get("userId"));
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null) {
                        LocalUser user = (LocalUser) authentication.getPrincipal();
                        if (!userService.userHasPermissionToUser(user, userId)) {
                            message = null;
                        }
                    }else{
                        message = null;
                    }
                } catch (NumberFormatException ignored) {
                    message = null;
                }
            }
            return message;
        }
    }
}
