package ware.revo.collections.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ware.revo.collections.RevoCollections;

import static ware.revo.collections.inventory.CollectionsMenu.collectionsMenus;
import static ware.revo.collections.inventory.CollectionsMenu.collectionsMenusMain;

public class OnInventoryClick implements Listener
{
    private final RevoCollections plugin;

    public OnInventoryClick(RevoCollections pl)
    {
        this.plugin = pl;
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event)
    {
        if ( collectionsMenusMain.contains(event.getInventory()) )
        {
            event.setCancelled(true);

            if ( event.getCurrentItem() == null )
            {
                return;
            }

            for ( String collectionName : plugin.config.getConfigurationSection("collections").getKeys(false) )
            {
                if ( event.getCurrentItem().getItemMeta().getLocalizedName().equals(collectionName) )
                {
                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();
                    player.openInventory(plugin.menuManager.CollectionInventory(player, collectionName));
                }
            }

            if ( event.getCurrentItem().getItemMeta().getLocalizedName().equals("nextpage") )
            {
                Player player = (Player) event.getWhoClicked();

                int index = collectionsMenusMain.indexOf(event.getInventory());

                player.closeInventory();
                player.openInventory(collectionsMenusMain.get(index + 1));

            }

            if ( event.getCurrentItem().getItemMeta().getLocalizedName().equals("lastpage") )
            {
                Player player = (Player) event.getWhoClicked();

                int index = collectionsMenusMain.indexOf(event.getInventory());

                player.closeInventory();
                player.openInventory(collectionsMenusMain.get(index - 1));
            }
        }

        if ( collectionsMenus.contains(event.getInventory()) )
        {
            event.setCancelled(true);

            if ( event.getCurrentItem() == null )
            {
                return;
            }

            if ( event.getCurrentItem().getItemMeta().getLocalizedName().equals("nextpage") )
            {
                Player player = (Player) event.getWhoClicked();

                int index = collectionsMenus.indexOf(event.getInventory());

                player.closeInventory();
                player.openInventory(collectionsMenus.get(index + 1));

            }

            if ( event.getCurrentItem().getItemMeta().getLocalizedName().equals("lastpage") )
            {
                Player player = (Player) event.getWhoClicked();

                int index = collectionsMenus.indexOf(event.getInventory());

                player.closeInventory();
                player.openInventory(collectionsMenus.get(index - 1));
            }

            if ( event.getCurrentItem().getItemMeta().getLocalizedName().equals("tomainmenu") )
            {
                Player player = (Player) event.getWhoClicked();

                player.closeInventory();
                player.openInventory(plugin.menuManager.MainInventory(player));

            }


        }
    }
}
