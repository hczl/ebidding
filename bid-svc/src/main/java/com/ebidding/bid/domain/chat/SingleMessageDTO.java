package com.ebidding.bid.domain.chat;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SingleMessageDTO {
    private String role;
    private String content;

}
