package de.kyleonaut.multiproxywhitelist.service;

import de.kyleonaut.multiproxywhitelist.model.PlayerProfile;
import de.kyleonaut.multiproxywhitelist.repository.MojangRepository;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author kyleonaut
 */
@RequiredArgsConstructor
public class MojangService {
    private final MojangRepository mojangRepository;

    public void getPlayerProfile(String name, Consumer<PlayerProfile> playerProfileConsumer) {
        CompletableFuture.runAsync(() -> {
            playerProfileConsumer.accept(mojangRepository.getPlayerProfileByName(name));
        });
    }
}
