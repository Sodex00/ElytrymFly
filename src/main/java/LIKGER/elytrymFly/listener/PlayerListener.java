package LIKGER.elytrymFly.listener;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.flight.FlightManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final ElytrymFly plugin;

    public PlayerListener(ElytrymFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getFlightManager().clearPlayerData(player.getUniqueId());
        plugin.getDurabilityService().clear(player.getUniqueId());
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (!plugin.getConfigManager().isDisableOnGamemodeChange()) {
            return;
        }

        Player player = event.getPlayer();
        FlightManager flightManager = plugin.getFlightManager();
        if (!flightManager.isFlying(player.getUniqueId())) {
            return;
        }

        GameMode newMode = event.getNewGameMode();
        if (newMode == GameMode.CREATIVE || newMode == GameMode.SPECTATOR) {
            // Those modes manage their own flight; stop tracking without touching allow-flight/flying,
            // Bukkit already grants/handles it for these modes.
            flightManager.forceStop(player.getUniqueId());
        }
    }
}
