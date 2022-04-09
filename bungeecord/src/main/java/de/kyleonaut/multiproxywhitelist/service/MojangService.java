package de.kyleonaut.multiproxywhitelist.service;

import de.kyleonaut.multiproxywhitelist.model.PlayerModel;
import de.kyleonaut.multiproxywhitelist.model.PlayerProfile;
import de.kyleonaut.multiproxywhitelist.repository.MojangRepository;
import de.kyleonaut.multiproxywhitelist.repository.WhitelistRepository;
import lombok.RequiredArgsConstructor;

/**
 * @author kyleonaut
 */
@RequiredArgsConstructor
public class MojangService {
    private final MojangRepository mojangRepository;
    private final WhitelistRepository whitelistRepository;

    public PlayerProfile getPlayerProfile(String name) {
        final PlayerModel player = whitelistRepository.getPlayer(name);
        if (player == null) {
            return mojangRepository.getPlayerProfileByName(name);
        }
        return player.toPlayerProfile();
    }
}
