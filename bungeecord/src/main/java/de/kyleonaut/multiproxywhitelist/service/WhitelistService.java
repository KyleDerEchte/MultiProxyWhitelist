package de.kyleonaut.multiproxywhitelist.service;

import de.kyleonaut.multiproxywhitelist.manager.JedisManager;
import de.kyleonaut.multiproxywhitelist.model.PlayerModel;
import de.kyleonaut.multiproxywhitelist.repository.WhitelistRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

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

    private void handleSubscriptions() {
        subscribeToAddPlayer();
        subscribeToRemovePlayer();
        subscribeToWhitelistChange();
    }

    public void addPlayer(String name) {
        mojangService.getPlayerProfile(name, playerProfile -> {
            whitelistRepository.addPlayerToWhitelist(playerProfile.toPlayerModel());
            jedisManager.publish("MULTI_PROXY_WHITELIST:ADD_PLAYER", playerProfile.toPlayerModel().toJson());
            this.whitelistedPlayers.add(playerProfile.toPlayerModel());
        });
    }

    public void removePlayer(String name) {
        mojangService.getPlayerProfile(name, playerProfile -> {
            whitelistRepository.removePlayerFromWhitelist(playerProfile.toPlayerModel());
            jedisManager.publish("MULTI_PROXY_WHITELIST:REMOVE_PLAYER", playerProfile.toPlayerModel().toJson());
            final Optional<PlayerModel> optionalPlayerModel = this.whitelistedPlayers.stream()
                    .filter(playerModel -> playerModel.getUuid().equals(playerProfile.getUUID()))
                    .findFirst();
            optionalPlayerModel.ifPresent(whitelistedPlayers::remove);
        });
    }

    private void subscribeToAddPlayer() {
        jedisManager.subscribe("MULTI_PROXY_WHITELIST:ADD_PLAYER", s -> {
            final PlayerModel playerModel = PlayerModel.fromJson(s);
            if (playerModel == null) {
                return;
            }
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
            this.whitelistedPlayers.stream()
                    .filter(p -> p.getUuid().equals(playerModel.getUuid()))
                    .findFirst()
                    .ifPresent(whitelistedPlayers::remove);
        });
    }

    public void activateWhitelist() {
        this.whitelistStatus = true;
        jedisManager.publish("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST", "true");
    }

    public void deactivateWhitelist() {
        this.whitelistStatus = false;
        jedisManager.publish("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST", "false");
    }

    private void subscribeToWhitelistChange() {
        jedisManager.subscribe("MULTI_PROXY_WHITELIST:CHANGE_WHITELIST", s -> {
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
