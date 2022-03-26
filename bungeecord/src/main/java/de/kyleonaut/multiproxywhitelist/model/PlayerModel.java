package de.kyleonaut.multiproxywhitelist.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;

import java.util.UUID;

/**
 * @author kyleonaut
 */
@Data
public class PlayerModel {
    private UUID uuid;
    private String name;

    public static PlayerModel fromJson(String json) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, PlayerModel.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJson() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        try {
            return writer.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
