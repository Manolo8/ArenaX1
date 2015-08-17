package br.com.tinycraft.arenax1.listener;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.arena.Arena;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.gui.GUI;
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

    public PlayerListener(ArenaExecutor arenaExecutor, ArenaConfig arenaConfig, GUI gui)
    {
        this.arenaExecutor = arenaExecutor;
        this.gui = gui;
        this.guiItem = arenaConfig.getGuiItem();
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
        if (e.getDamager() instanceof Player)
        {
            Player player = (Player) e.getDamager();

            Arena arena = arenaExecutor.getPlayerArena(player, false);

            if (arena == null)
            {
                return;
            }

            if (arena.isWaitinigToStart())
            {
                player.sendMessage("§cEspere o duelo começar!");
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
                player.sendMessage("§cEspere o duelo começar!");
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
            e.getPlayer().sendMessage("§cComandos bloqueados aqui! Espere ou digite /suicide");
            e.setCancelled(true);
        }
    }
}
