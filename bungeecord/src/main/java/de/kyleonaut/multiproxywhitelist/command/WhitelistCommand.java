package de.kyleonaut.multiproxywhitelist.command;

import de.kyleonaut.multiproxywhitelist.model.PlayerModel;
import de.kyleonaut.multiproxywhitelist.service.WhitelistService;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

/**
 * @author kyleonaut
 */
public class WhitelistCommand extends Command {
    private final WhitelistService whitelistService;

    public WhitelistCommand(WhitelistService whitelistService) {
        super("bwhitelist", "bwhitelist.command", "bungeewhitelist");
        this.whitelistService = whitelistService;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            sendHelp(commandSender);
            return;
        }
        if (args[0].equalsIgnoreCase("list")) {
            final List<PlayerModel> whitelistedPlayers = whitelistService.getWhitelistedPlayers();
            if (whitelistedPlayers.size() == 0) {
                commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §cThere is no one whitelisted."));
                return;
            }
            commandSender.sendMessage(new TextComponent("§7§m---§r§7[§6MultiProxyWhitelist§7]§m---"));
            int counter = 1;
            for (PlayerModel whitelistedPlayer : whitelistedPlayers) {
                commandSender.sendMessage(
                        new TextComponent(
                                "§7[§6" + counter + "§7] §6"
                                        + whitelistedPlayer.getName()
                                        + " §7[§6"
                                        + whitelistedPlayer.getUuid()
                                        + "§7]")
                );
                counter++;
            }
            commandSender.sendMessage(new TextComponent("§7§m---§r§7[§6MultiProxyWhitelist§7]§m---"));
            return;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                sendHelp(commandSender);
                return;
            }
            whitelistService.addPlayer(args[1], (wasAdded, playerProfile) -> {
                if (!wasAdded) {
                    commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §cThe provided player couldn't be added."));
                    return;
                }
                commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §7The player §6" + playerProfile.getName() + "§7 was added to the whitelist."));
            });
            return;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                sendHelp(commandSender);
                return;
            }
            whitelistService.removePlayer(args[1], (wasAdded, playerProfile) -> {
                if (!wasAdded) {
                    commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §cThe provided player couldn't be found."));
                    return;
                }
                commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §7The player §6" + playerProfile.getName() + "§7 was removed from the whitelist."));
            });
            return;
        }
        if (args[0].equalsIgnoreCase("on")) {
            if (whitelistService.isWhitelistActivated()) {
                commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §cThe whitelist is already activated."));
                return;
            }
            whitelistService.activateWhitelist();
            commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §7The whitelist was activated."));
            return;
        }
        if (args[0].equalsIgnoreCase("off")) {
            if (!whitelistService.isWhitelistActivated()) {
                commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §cThe whitelist is already deactivated."));
                return;
            }
            whitelistService.deactivateWhitelist();
            commandSender.sendMessage(new TextComponent("§7[§6MultiProxyWhitelist§7] §7The whitelist was deactivated."));
        }
    }

    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(new TextComponent("§7§m---§r§7[§6MultiProxyWhitelist§7]§m---"));
        commandSender.sendMessage(new TextComponent("§7/bwhitelist - §eShows this help message"));
        commandSender.sendMessage(new TextComponent("§7/bwhitelist list - §eLists every whitelisted player"));
        commandSender.sendMessage(new TextComponent("§7/bwhitelist add <name> - §eAdds a player to the whitelist"));
        commandSender.sendMessage(new TextComponent("§7/bwhitelist remove <name> - §eRemoves a player from the whitelist"));
        commandSender.sendMessage(new TextComponent("§7/bwhitelist on - §eActivates the whitelist"));
        commandSender.sendMessage(new TextComponent("§7/bwhitelist off - §eDeactivates the whitelist"));
    }
}
