package de.kyleonaut.multiproxywhitelist.model;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerProfile {
    private String id;
    private String name;

    public UUID getUUID() {
        return UUID.fromString(this.id.replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));
    }

    public PlayerModel toPlayerModel() {
        final PlayerModel playerModel = new PlayerModel();
        playerModel.setUuid(getUUID());
        playerModel.setName(this.name);
        return playerModel;
    }
}