package br.com.tinycraft.arenax1.gui;

import br.com.tinycraft.arenax1.arena.ArenaManager;
import br.com.tinycraft.arenax1.executor.ArenaExecutor;
import br.com.tinycraft.arenax1.invite.InviteManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author Willian
 */
public class GUI implements Listener
{

    private final ArenaManager arenaManager;
    private final InviteManager inviteManager;
    private final ArenaExecutor arenaExecutor;

    public GUI(ArenaManager arenaManager,
            InviteManager inviteManager,
            ArenaExecutor arenaExecutor)
    {
        this.arenaExecutor = arenaExecutor;
        this.arenaManager = arenaManager;
        this.inviteManager = inviteManager;
    }

    public void openGui(Player author)
    {
        Inventory inventory = Bukkit.createInventory(author, 54, "§eArenaX1 GUI");

        int m = 0;
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (author.getName().equals(player.getName()))
            {
                continue;
            }
            if (arenaExecutor.getPlayerArena(player, false) != null)
            {
                continue;
            }
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(player.getName());
            skullMeta.setDisplayName("§aDuel with " + player.getName());

            int inviteRelation = inviteManager.getInviteRelation(author, player);

            if (inviteRelation == 1)
            {
                skullMeta.setDisplayName("§a" + player.getName() + " invite you. Click to accept!");
                skull.setDurability((byte) 1);
            } else if (inviteRelation == 2)
            {
                skullMeta.setDisplayName("§a" + player.getName() + " invite sended!");
                skull.setDurability((byte) 1);
            }
            skull.setItemMeta(skullMeta);
            inventory.setItem(m, skull);
            m++;

            if (m > 54)
            {
                break;
            }
        }

        author.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e)
    {
        if (!(e.getInventory().getName().equals("§eArenaX1 GUI")))
        {
            return;
        }
        e.setCancelled(true);

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.SKULL_ITEM)
        {
            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player author = (Player) e.getWhoClicked();
            Player target = Bukkit.getPlayer(meta.getOwner());

            if (target.isOnline())
            {
                int relation = inviteManager.getInviteRelation(author, target);
                if (relation == 1)
                {
                    author.playSound(author.getLocation(), Sound.NOTE_PLING, 20, 1);
                    inviteManager.getPendentInvite(author, target).setAccepted(true);
                    return;
                }
                if (inviteManager.createInvite(author, target))
                {
                    author.playSound(author.getLocation(), Sound.NOTE_PLING, 20, 1);
                    e.getCurrentItem().setDurability((byte) 1);
                    meta.setDisplayName("§a" + target.getName() + " invite sended!");
                    e.getCurrentItem().setItemMeta(meta);
                } else
                {
                    author.playSound(author.getLocation(), Sound.ANVIL_LAND, 20, 1);
                }
            }
        }
    }
}
