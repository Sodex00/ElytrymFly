package LIKGER.elytrymFly;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommand implements CommandExecutor {
    private final ElytrymFly plugin;

    public AdminCommand(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("elytrymfly.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("usage"));
            return true;
        }

        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(plugin.getConfigManager().getMessage("reload-success"));
        return true;
    }
}
