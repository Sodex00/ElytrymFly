package LIKGER.elytrymFly.combat;

import org.bukkit.entity.Player;

/**
 * Abstracts the PvP/combat-tag backend ElytrymFly reacts to.
 * Implementations must only reference their backing plugin's API classes
 * lazily (inside register()/isInCombat()), never from always-loaded classes,
 * so the plugin keeps working when that backend is not installed.
 */
public interface CombatIntegration {
    CombatType getType();

    /**
     * @return true if the backing plugin (or vanilla, for NONE) is present and usable.
     */
    boolean isAvailable();

    boolean isInCombat(Player player);

    /**
     * Registers any listeners needed to detect combat tags. Called once, after isAvailable() returned true.
     */
    void register();
}
