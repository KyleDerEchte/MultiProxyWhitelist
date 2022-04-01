package de.kyleonaut.multiproxywhitelist.service;

import de.kyleonaut.multiproxywhitelist.BungeeWhitelistPlugin;
import de.kyleonaut.multiproxywhitelist.manager.JedisManager;
import de.kyleonaut.multiproxywhitelist.model.PlayerModel;
import de.kyleonaut.multiproxywhitelist.model.PlayerProfile;
import de.kyleonaut.multiproxywhitelist.repository.WhitelistRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * @author kyleonaut
 */
@RequiredArgsConstructor
public class WhitelistService {
    private final MojangService mojangService;
    private final WhitelistRepository whitelistRepository;
    private final JedisManager jedisManager;
    @Getter
    private List<PlayerModel> whitelistedPlayers;
    private boolean whitelistStatus;

    public void init() {
        this.whitelistedPlayers = whitelistRepository.getWhitelistedPlayers();
        handleSubscriptions();
    }

    public boolean isWhitelisted(UUID uuid) {
        final Optional<PlayerModel> optionalPlayerModel = this.whitelistedPlayers.stream()
                .filter(playerModel -> playerModel.getUuid().equals(uuid))
                .findFirst();
        return optionalPlayerModel.isPresent();
    }

    private void handleSubscriptions() {
        subscribeToAddPlayer();
        subscribeToRemovePlayer();
        subscribeToWhitelistChange();
    }

    public void addPlayer(String name, BiConsumer<Boolean, PlayerProfile> booleanPlayerProfileBiConsumer) {
        BungeeWhitelistPlugin.getRequestExecutor().execute(() -> {
            final PlayerProfile playerProfile = mojangService.getPlayerProfile(name);
            if (playerProfile == null) {
                booleanPlayerProfileBiConsumer.accept(false, null);
                return;
            }
            if (this.isWhitelisted(playerProfile.getUUID())) {
                booleanPlayerProfileBiConsumer.accept(false, null);
                return;
            }
            whitelistRepository.addPlayerToWhitelist(playerProfile.toPlayerModel());
            this.whitelistedPlayers.add(playerProfile.toPlayerModel());
            jedisManager.publish("MULTI_PROXY_WHITELIST:ADD_PLAYER", playerProfile.toPlayerModel().toJson(), false);
            booleanPlayerProfileBiConsumer.accept(true, playerProfile);
        });
    }

    public void removePlayer(String name, BiConsumer<Boolean, PlayerProfile> booleanPlayerProfileBiConsumer) {
        BungeeWhitelistPlugin.getRequestExecutor().execute(() -> {
            final PlayerProfile playerProfile = mojangService.getPlayerProfile(name);
            if (playerProfile == null) {
                booleanPlayerProfileBiConsumer.accept(false, null);
                return;
            }
            whitelistRepository.removePlayerFromWhitelist(playerProfile.toPlayerModel());
            jedisManager.publish("MULTI_PROXY_WHITELIST:REMOVE_PLAYER", playerProfile.toPlayerModel().toJson(), true);
            final Optional<PlayerModel> optionalPlayerModel = this.whitelistedPlayers.stream()
                    .filter(playerModel -> playerModel.getUuid().equals(playerProfile.getUUID()))
                    .findFirst();
            optionalPlayerModel.ifPresent(whitelistedPlayers::remove);
            booleanPlayerProfileBiConsumer.accept(true, playerProfile);
        });
    }

    private void subscribeToAddPlayer() {
        jedisManager.subscribe("MULTI_PROXY_WHITELIST:ADD_PLAYER", s -> {
            final PlayerModel playerModel = PlayerModel.fromJson(s);
            if (playerModel == null) {
                return;
            }
            System.out.println("MULTI_PROXY_WHITELIST:ADD_PLAYER >> " + playerModel.toJson());
            this.whitelistedPlayers.stream()
                    .filter(p -> p.getUuid().equals(playerModel.getUuid()))
                    .findFirst()
                    .ifPresentOrElse(p -> {
                    }, () -> this.whitelistedPlayers.add(playerModel));
        });
    }

    private void subscribeToRemovePlayer() {
        jedisManager.subscribe("MULTI_PROXY_WHITELIST:REMOVE_PLAYER", s -> {
            final PlayerModel playerModel = PlayerModel.fromJson(s);
            if (playerModel == null) {
                return;
            }
            System.out.println("MULTI_PROXY_WHITELIST:REMOVE_PLAYER >> " + playerModel.toJson());
            this.whitelistedPlayers.stream()
                    .filter(p -> p.getUuid().equals(playerModel.getUuid()))
                    .findFirst()
                    .ifPresent(whitelistedPlayers::remove);
        });
    }

    public void activateWhitelist() {
        this.whitelistStatus = true;
        jedisManager.publish("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST", "true", true);
    }

    public void deactivateWhitelist() {
        this.whitelistStatus = false;
        jedisManager.publish("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST", "false", true);
    }

    private void subscribeToWhitelistChange() {
        jedisManager.subscribe("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST", s -> {
            System.out.println("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST >> " + s);
            if (s.equals("true")) {
                this.whitelistStatus = true;
            } else if (s.equals("false")) {
                this.whitelistStatus = false;
            }
        });
    }

    public boolean isWhitelistActivated() {
        return whitelistStatus;
    }
}
