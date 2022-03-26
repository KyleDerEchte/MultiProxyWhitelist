package de.kyleonaut.multiproxywhitelist.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author kyleonaut
 */
public class WhitelistCommand extends Command {
    public WhitelistCommand() {
        super("bwhitelist", "bwhitelist.command", "bungeewhitelist");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(new TextComponent("§7§m---§r§7[§6MultiProxyWhitelist§7]§m---"));
            commandSender.sendMessage(new TextComponent("§7/bwhitelist - §eShows this help message"));
            commandSender.sendMessage(new TextComponent("§7/bwhitelist list - §eLists every whitelisted player"));
            commandSender.sendMessage(new TextComponent("§7/bwhitelist add <name> - §eAdds a player to the whitelist"));
            commandSender.sendMessage(new TextComponent("§7/bwhitelist remove <name> - §eRemoves a player from the whitelist"));
            commandSender.sendMessage(new TextComponent("§7/bwhitelist on - §eActivates the whitelist"));
            commandSender.sendMessage(new TextComponent("§7/bwhitelist off - §eDeactivates the whitelist"));
            return;
        }
    }
}
