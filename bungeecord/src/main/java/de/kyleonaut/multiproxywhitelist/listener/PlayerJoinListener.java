package de.kyleonaut.multiproxywhitelist.listener;

import de.kyleonaut.multiproxywhitelist.service.WhitelistService;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author kyleonaut
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private final WhitelistService whitelistService;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        if (!whitelistService.isWhitelistActivated()) {
            return;
        }
        if (whitelistService.isWhitelisted(event.getPlayer().getUniqueId())) {
            return;
        }
        event.getPlayer().disconnect(new TextComponent("§7[§6MultiProxyWhitelist§7] §cYou are not whitelisted."));
    }


}
