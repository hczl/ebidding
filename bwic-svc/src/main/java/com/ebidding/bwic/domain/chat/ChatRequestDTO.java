package com.ebidding.bwic.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

@AllArgsConstructor
public class ChatRequestDTO {
    private final String model = "moonshot-v1-8k";
    private Boolean stream = true;
    private List<SingleMessageDTO> messages;

    public ChatRequestDTO() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(String role, String content) {
        this.messages.add(new SingleMessageDTO(role, content));
    }

}
