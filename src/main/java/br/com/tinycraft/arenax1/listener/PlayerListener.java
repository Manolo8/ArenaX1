package br.com.tinycraft.arenax1.listener;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.language.Language;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.event.EventPriority.HIGHEST;

/**
 * @author AB
 */
public class PlayerListener implements Listener {

    private final ArenaExecutor arenaExecutor;
    private final Language language;

    public PlayerListener(ArenaExecutor arenaExecutor, ArenaConfig arenaConfig, Language language) {
        this.arenaExecutor = arenaExecutor;
        this.language = language;
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent e) {
        if (e.isCancelled()) {
            return;
        }

        arenaExecutor.playerDeath(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        arenaExecutor.playerDeath(e.getEntity());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        arenaExecutor.playerDeath(e.getPlayer());
    }

    @EventHandler(priority = HIGHEST)
    public void onPlayerDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {

            Player player = null;

            if (e.getDamager() instanceof Player)
                player = (Player) e.getDamager();
            else if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player)
                    player = (Player) arrow.getShooter();
            }

            if (player == null) return;

            Arena arena = arenaExecutor.getPlayerArena(player);

            if (arena == null) return;

            checkStarted(e, player, arena);
        }
    }

    @EventHandler(priority = HIGHEST)
    public void onPlayerShootBowEvent(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            Arena arena = arenaExecutor.getPlayerArena(player);

            if (arena == null) return;

            checkStarted(e, player, arena);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        if (arenaExecutor.getPlayerArena(e.getPlayer()) == null) return;

        if (e.getMessage().equalsIgnoreCase("/suicide")) {
            e.getPlayer().setHealth(0);
            return;
        }

        if (!e.getPlayer().isOp()) {
            e.getPlayer().sendMessage(language.getMessage("ErrorCommandsBlocked"));
            e.setCancelled(true);
        }
    }

    private void checkStarted(Cancellable e, Player player, Arena arena) {
        if (arena.isWaitinigToStart()) {
            player.sendMessage(language.getMessage("ErrorWaitDuelStart"));
            e.setCancelled(true);
        } else if (arena.isOccurring()) e.setCancelled(false);
    }
}
