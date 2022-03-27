package de.kyleonaut.multiproxywhitelist.service;

import de.kyleonaut.multiproxywhitelist.model.PlayerProfile;
import de.kyleonaut.multiproxywhitelist.repository.MojangRepository;
import lombok.RequiredArgsConstructor;

/**
 * @author kyleonaut
 */
@RequiredArgsConstructor
public class MojangService {
    private final MojangRepository mojangRepository;

    public PlayerProfile getPlayerProfile(String name) {
        return mojangRepository.getPlayerProfileByName(name);
    }
}
