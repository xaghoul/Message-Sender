package com.xaghoul.demo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageTemplate {

    @Id
    @GeneratedValue(generator = "UUID",
                    strategy = GenerationType.IDENTITY)
    private UUID id;
    private String name;
    private String template;
    @ElementCollection
    @JsonDeserialize
    @Builder.Default
    private List<URL> recipients = new ArrayList<>();

    // TODO: 4/1/2021 change to SpEL
    public Message createMessage(Map<String, String> variables) {
        String msg = template;
        for(Map.Entry<String, String> key : variables.entrySet()) {
            msg = msg.replace(key.getKey(), key.getValue());
        }
        return new Message(msg);
    }
}
