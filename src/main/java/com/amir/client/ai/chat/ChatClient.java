package com.amir.client.ai.chat;

import com.amir.client.ai.chat.dto.ChatRequest;
import com.amir.client.ai.chat.dto.ChatResponse;

public interface ChatClient {
    ChatResponse generate(ChatRequest chatRequest);
}
