package com.dragonsofmugloar.strategy;

import com.dragonsofmugloar.model.GameContext;
import com.dragonsofmugloar.model.MessageTask;

import java.util.List;

public interface MessageStrategy {
    MessageTask choose(
            List<MessageTask> messages,
            GameContext context
    );
}
