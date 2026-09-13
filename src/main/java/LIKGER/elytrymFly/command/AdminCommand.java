package LIKGER.elytrymFly.command;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.combat.CombatIntegration;
import LIKGER.elytrymFly.combat.CombatIntegrationFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS = List.of("reload", "status");

    private final ElytrymFly plugin;

    public AdminCommand(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("elytrymfly.admin")) {
            plugin.getMessagesManager().send(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessagesManager().send(sender, "usage");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reload(sender);
            case "status" -> status(sender);
            default -> plugin.getMessagesManager().send(sender, "unknown-command");
        }
        return true;
    }

    private void reload(CommandSender sender) {
        try {
            plugin.getConfigManager().reload();
            plugin.getMessagesManager().reload();
            CombatIntegration integration = CombatIntegrationFactory.resolve(plugin, plugin.getFlightManager());
            plugin.getFlightManager().setCombatIntegration(integration);
            plugin.getMessagesManager().send(sender, "reload-success");
        } catch (Exception ex) {
            plugin.getMessagesManager().send(sender, "reload-fail");
            plugin.getLogger().severe("Failed to reload ElytrymFly configuration: " + ex.getMessage());
        }
    }

    private void status(CommandSender sender) {
        CombatIntegration integration = plugin.getFlightManager().getCombatIntegration();
        String name = integration != null ? integration.getType().name() : "UNKNOWN";
        plugin.getMessagesManager().send(sender, "status", "%combat-plugin%", name);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1 || !sender.hasPermission("elytrymfly.admin")) {
            return List.of();
        }
        String partial = args[0].toLowerCase();
        return SUBCOMMANDS.stream()
                .filter(sub -> sub.startsWith(partial))
                .collect(Collectors.toList());
    }
}
