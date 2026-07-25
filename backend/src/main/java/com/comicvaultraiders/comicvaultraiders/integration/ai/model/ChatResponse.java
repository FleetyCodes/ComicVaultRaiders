package com.comicvaultraiders.comicvaultraiders.integration.ai.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponse {

    private String responseMessage;

    public ChatResponse(String msg) {
        this.responseMessage = msg;
    }
}
