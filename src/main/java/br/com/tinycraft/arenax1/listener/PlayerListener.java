package br.com.tinycraft.arenax1.listener;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.gui.GUI;
import br.com.tinycraft.arenax1.language.Language;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import static org.bukkit.event.EventPriority.HIGHEST;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author AB
 */
public class PlayerListener implements Listener
{

    private final ArenaExecutor arenaExecutor;
    private final GUI gui;
    private final int guiItem;
    private final Language language;

    public PlayerListener(ArenaExecutor arenaExecutor, ArenaConfig arenaConfig, GUI gui, Language language)
    {
        this.arenaExecutor = arenaExecutor;
        this.gui = gui;
        this.guiItem = arenaConfig.getGuiItem();
        this.language = language;
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent e)
    {
        if (e.isCancelled())
        {
            return;
        }

        arenaExecutor.playerDeath(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e)
    {
        arenaExecutor.playerDeath(e.getEntity());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e)
    {
        arenaExecutor.playerDeath(e.getPlayer());
    }

    @EventHandler(priority = HIGHEST)
    public void onPlayerDamageEvent(EntityDamageByEntityEvent e)
    {
        if (e.getEntity() instanceof Player)
        {

            Player player = null;
            if (e.getDamager() instanceof Player)
            {
                player = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Arrow)
            {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player)
                {
                    player = (Player) arrow.getShooter();
                }
            } else
            {
                return;
            }

            Arena arena = arenaExecutor.getPlayerArena(player, false);

            if (arena == null)
            {
                return;
            }

            if (arena.isWaitinigToStart())
            {
                player.sendMessage(language.getMessage("ErrorWaitDuelStart", new String[0]));
                e.setCancelled(true);
            } else if (arena.isOcurring())
            {
                e.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = HIGHEST)
    public void onPlayerShootBowEvent(EntityShootBowEvent e)
    {
        if (e.getEntity() instanceof Player)
        {
            Player player = (Player) e.getEntity();

            Arena arena = arenaExecutor.getPlayerArena(player, false);

            if (arena == null)
            {
                return;
            }

            if (arena.isWaitinigToStart())
            {
                player.sendMessage(language.getMessage("ErrorWaitDuelStart", new String[0]));
                e.setCancelled(true);
            } else if (arena.isOcurring())
            {
                e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e)
    {
        if (e.getPlayer().getItemInHand().getTypeId() == this.guiItem)
        {
            gui.openGui(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e)
    {
        if (e.getMessage().equalsIgnoreCase("/suicide"))
        {
            e.getPlayer().setHealth(0);
        }

        if (arenaExecutor.playerCommand(e.getPlayer()) && !e.getPlayer().isOp())
        {
            e.getPlayer().sendMessage(language.getMessage("ErrorCommandsBlocked", new String[0]));
            e.setCancelled(true);
        }
    }
}
