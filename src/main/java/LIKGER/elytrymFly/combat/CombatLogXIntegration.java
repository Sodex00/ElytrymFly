package LIKGER.elytrymFly.combat;

import LIKGER.elytrymFly.flight.FlightManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class CombatLogXIntegration implements CombatIntegration, Listener {
    private static final String PLUGIN_NAME = "CombatLogX";

    private final Plugin plugin;
    private final FlightManager flightManager;

    public CombatLogXIntegration(Plugin plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    @Override
    public CombatType getType() {
        return CombatType.COMBATLOGX;
    }

    @Override
    public boolean isAvailable() {
        return getApi() != null;
    }

    @Override
    public boolean isInCombat(Player player) {
        ICombatLogX api = getApi();
        return api != null && api.getCombatManager().isInCombat(player);
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private ICombatLogX getApi() {
        Plugin combatLogX = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        return combatLogX instanceof ICombatLogX ? (ICombatLogX) combatLogX : null;
    }

    @EventHandler
    public void onPlayerTag(PlayerTagEvent event) {
        flightManager.handleCombatTag(event.getPlayer(), "flight-disabled-combat");
    }
}
