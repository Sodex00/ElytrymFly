package LIKGER.elytrymFly.combat;

import LIKGER.elytrymFly.ElytrymFly;
import LIKGER.elytrymFly.flight.FlightManager;

import java.util.logging.Logger;

public final class CombatIntegrationFactory {
    private CombatIntegrationFactory() {
    }

    public static CombatIntegration resolve(ElytrymFly plugin, FlightManager flightManager) {
        Logger logger = plugin.getLogger();
        CombatType configured = plugin.getConfigManager().getCombatType();

        CombatLogXIntegration combatLogX = new CombatLogXIntegration(plugin, flightManager);
        GreatCombatIntegration greatCombat = new GreatCombatIntegration(plugin, flightManager);
        NoneCombatIntegration none = new NoneCombatIntegration(plugin, flightManager);

        CombatIntegration chosen = switch (configured) {
            case COMBATLOGX -> pickOrFallback(combatLogX, none, logger);
            case GREATCOMBAT -> pickOrFallback(greatCombat, none, logger);
            case NONE -> none;
            case AUTO -> autoDetect(combatLogX, greatCombat, none, logger);
        };

        chosen.register();
        logger.info("Combat integration in use: " + chosen.getType());
        return chosen;
    }

    private static CombatIntegration pickOrFallback(CombatIntegration preferred, CombatIntegration fallback, Logger logger) {
        if (preferred.isAvailable()) {
            return preferred;
        }
        logger.warning("combat.plugin is set to " + preferred.getType()
                + " but that plugin isn't installed/enabled. Falling back to " + fallback.getType() + ".");
        return fallback;
    }

    private static CombatIntegration autoDetect(CombatLogXIntegration combatLogX, GreatCombatIntegration greatCombat,
                                                 NoneCombatIntegration none, Logger logger) {
        boolean hasCombatLogX = combatLogX.isAvailable();
        boolean hasGreatCombat = greatCombat.isAvailable();

        if (hasCombatLogX && hasGreatCombat) {
            logger.warning("Both CombatLogX and GreatCombat are installed; using CombatLogX. "
                    + "Set combat.plugin in config.yml to choose explicitly.");
            return combatLogX;
        }
        if (hasCombatLogX) {
            return combatLogX;
        }
        if (hasGreatCombat) {
            return greatCombat;
        }
        return none;
    }
}
