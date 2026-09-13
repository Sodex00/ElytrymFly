package LIKGER.elytrymFly.command;

import LIKGER.elytrymFly.ElytrymFly;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    private final ElytrymFly plugin;

    public FlyCommand(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessagesManager().send(sender, "only-players");
            return true;
        }

        if (!player.hasPermission("elytrymfly.use")) {
            plugin.getMessagesManager().send(player, "no-permission");
            return true;
        }

        plugin.getFlightManager().toggle(player);
        return true;
    }
}
