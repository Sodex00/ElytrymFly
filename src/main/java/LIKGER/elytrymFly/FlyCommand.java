package LIKGER.elytrymFly;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlyCommand implements CommandExecutor {
    private final ElytrymFly plugin;

    public FlyCommand(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("elytrymfly.use")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        boolean hasElytra = chestplate != null && chestplate.getType() == Material.ELYTRA;

        if (!hasElytra && !player.hasPermission("elytrymfly.bypass")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-elytra"));
            return true;
        }

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            plugin.getFlyingPlayers().remove(player.getUniqueId());
            player.sendMessage(plugin.getConfigManager().getMessage("flight-disabled"));
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            plugin.getFlyingPlayers().add(player.getUniqueId());
            player.sendMessage(plugin.getConfigManager().getMessage("flight-enabled"));
        }
        return true;
    }
}