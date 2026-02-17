package com.saga.airlinesystem.airlineticketservice.saga.commands;

import com.saga.airlinesystem.airlineticketservice.saga.handlers.CommandHandler;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandBus {

    private final Map<Class<?>, CommandHandler<?>> handlers = new HashMap<>();

    public CommandBus(List<CommandHandler<?>> handlerList) {
        for (CommandHandler<?> h : handlerList) {
            Class<?> cmdType = GenericTypeResolver.resolveTypeArgument(h.getClass(), CommandHandler.class);
            handlers.put(cmdType, h);
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends SagaCommand> void send(C command) {
        CommandHandler<C> handler =
                (CommandHandler<C>) handlers.get(command.getClass());

        if (handler == null) {
            throw new IllegalStateException("No handler for " + command.getClass());
        }

        handler.handle(command);
    }
}
