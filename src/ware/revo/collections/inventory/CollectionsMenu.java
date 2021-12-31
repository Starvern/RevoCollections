package ware.revo.collections.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ware.revo.collections.RevoCollections;
import ware.revo.collections.hooks.HeadDatabaseHook;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CollectionsMenu
{
    private final RevoCollections plugin;

    public static List<Inventory> collectionsMenusMain = new ArrayList<>();
    public static List<Inventory> collectionsMenus = new ArrayList<>();

    public CollectionsMenu(RevoCollections pl)
    {
        this.plugin = pl;
    }

    private Inventory generatePage(List<Inventory> invList, Inventory inv)
    {
        String nextPageItemName = plugin.config.getString("gui.next_page.name");
        String nextPageItemMaterial = plugin.config.getString("gui.next_page.material");
        int nextPageItemSlot = plugin.config.getInt("gui.next_page.slot");

        ItemStack nextPageItem ;
        if ( nextPageItemMaterial.startsWith("hdb-") )
        {
            try { nextPageItem = HeadDatabaseHook.hdbAPI.getItemHead(nextPageItemMaterial.replace("hdb-", "")); }
            catch ( NullPointerException e ) { nextPageItem = new ItemStack(Material.PLAYER_HEAD, 1); }
        }
        else
        {
            nextPageItem = new ItemStack(Material.getMaterial(nextPageItemMaterial), 1);
        }

        ItemMeta nextPageItemMeta = nextPageItem.getItemMeta();
        nextPageItemMeta.setDisplayName(plugin.formatColors(nextPageItemName));
        nextPageItemMeta.setLocalizedName("nextpage");
        nextPageItem.setItemMeta(nextPageItemMeta);

        inv.setItem(nextPageItemSlot, nextPageItem);

        String inventoryTitle = plugin.formatColors(plugin.config.getString("gui.main.title"));
        Inventory newInv = Bukkit.createInventory(null, 54, inventoryTitle);
        invList.add(newInv);

        generateFiller(newInv);

        String lastPageItemName = plugin.config.getString("gui.last_page.name");
        String lastPageItemMaterial = plugin.config.getString("gui.last_page.material");
        int lastPageItemSlot = plugin.config.getInt("gui.last_page.slot");

        ItemStack lastPageItem;
        if ( lastPageItemMaterial.startsWith("hdb-") )
        {
            try { lastPageItem = HeadDatabaseHook.hdbAPI.getItemHead(lastPageItemMaterial.replace("hdb-", "")); }
            catch ( NullPointerException e ) { lastPageItem = new ItemStack(Material.PLAYER_HEAD, 1); }
        }
        else
        {
            lastPageItem = new ItemStack(Material.getMaterial(lastPageItemMaterial), 1);
        }

        ItemMeta lastPageItemMeta = nextPageItem.getItemMeta();
        lastPageItemMeta.setDisplayName(plugin.formatColors(lastPageItemName));
        lastPageItemMeta.setLocalizedName("lastpage");
        lastPageItem.setItemMeta(lastPageItemMeta);

        newInv.setItem(lastPageItemSlot, lastPageItem);
        invList.add(newInv);

        return newInv;
    }

    private void generateFiller(Inventory inventory)
    {
        for ( int slot : plugin.config.getIntegerList("gui.filler_item.slots") )
        {
            String fillerItemName = plugin.config.getString("gui.filler_item.name");
            String fillerItemMaterial = plugin.config.getString("gui.filler_item.material");

            ItemStack fillerItem;

            if ( fillerItemMaterial.startsWith("hdb-") )
            {
                try { fillerItem = HeadDatabaseHook.hdbAPI.getItemHead(fillerItemMaterial.replace("hdb-", "")); }
                catch ( NullPointerException e ) { fillerItem = new ItemStack(Material.PLAYER_HEAD, 1); }
            }
            else
            {
                fillerItem = new ItemStack(Material.getMaterial(fillerItemMaterial), 1);
            }

            ItemMeta fillerItemMeta = fillerItem.getItemMeta();
            fillerItemMeta.setDisplayName(plugin.formatColors(fillerItemName));
            fillerItem.setItemMeta(fillerItemMeta);

            inventory.setItem(slot, fillerItem);

        }
    }

    public Inventory MainInventory(Player player)
    {
        collectionsMenusMain.clear();
        String inventoryTitle = plugin.formatColors(plugin.config.getString("gui.main.title"));
        Inventory collectionsMenuMain = Bukkit.createInventory(null, 54, inventoryTitle);
        collectionsMenusMain.add(collectionsMenuMain);

        generateFiller(collectionsMenuMain);

        int slotKey = 0;
        List<Integer> availableSlots = plugin.config.getIntegerList("gui.main.collections.slots");

        for ( String collectionName : plugin.config.getConfigurationSection("collections").getKeys(false) )
        {
            String collectionPermission = plugin.config.getString(
                    "collections." + collectionName + ".access_permission");
            String collectionStatus = "locked";

            if ( collectionPermission == null || player.hasPermission(collectionPermission) )
            {
                collectionStatus = "unlocked";
            }

            Set<String> requiredPermissions = plugin.config.getConfigurationSection(
                    "collections." + collectionName + ".required_permissions").getKeys(false);

            int permissionCount = 0;
            for ( String item : requiredPermissions )
            {
                String itemPermission = plugin.config.getString(
                        "collections." + collectionName + ".required_permissions." + item + ".permission");
                if (collectionPermission != null && player.hasPermission(collectionPermission)
                        && player.hasPermission(itemPermission))
                {
                    permissionCount ++;
                }

                if ( permissionCount >= requiredPermissions.size() )
                {
                    collectionStatus = "completed";
                    break;
                }
            }

            String itemName = plugin.config.getString(
                    "collections." + collectionName + "." + collectionStatus + ".name");
            List<String> itemLore = plugin.config.getStringList(
                    "collections." + collectionName + "." + collectionStatus + ".lore");
            String materialName = plugin.config.getString(
                    "collections." + collectionName + "." + collectionStatus + ".material");
            boolean enchanted = plugin.config.getBoolean(
                    "collections." + collectionName + "." + collectionStatus + ".enchanted");

            ItemStack collectionItem;

            if ( materialName.startsWith("hdb-") )
            {
                try { collectionItem = HeadDatabaseHook.hdbAPI.getItemHead(materialName.replace("hdb-", "")); }
                catch ( NullPointerException e ) { collectionItem = new ItemStack(Material.PLAYER_HEAD, 1); }
            }
            else
            {
                collectionItem = new ItemStack(Material.getMaterial(materialName), 1);
            }

            for ( String line : itemLore )
            {
                itemLore.set(itemLore.indexOf(line), plugin.formatColors(itemLore.get(itemLore.indexOf(line))));
            }

            ItemMeta collectionItemMeta = collectionItem.getItemMeta();
            collectionItemMeta.setDisplayName(plugin.formatColors(itemName));
            collectionItemMeta.setLore(itemLore);

            if ( enchanted )
            {
                collectionItemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                collectionItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            if ( collectionStatus.equals("unlocked") || collectionStatus.equals("completed") )
            {
                collectionItemMeta.setLocalizedName(collectionName);
            }
            collectionItem.setItemMeta(collectionItemMeta);

            collectionsMenuMain.setItem(availableSlots.get(slotKey), collectionItem);
            slotKey ++;

            int maximumSlot = 0;

            for ( int i : availableSlots )
            {
                if ( i >= maximumSlot ) { maximumSlot = i; }
            }

            try
            {
                if ( availableSlots.get(slotKey) > maximumSlot )
                {
                    collectionsMenuMain = generatePage(collectionsMenusMain, collectionsMenuMain);
                    slotKey = 0;
                }
            }
            catch ( IndexOutOfBoundsException e )
            {
                collectionsMenuMain = generatePage(collectionsMenusMain, collectionsMenuMain);
                slotKey = 0;
            }


        }
        return collectionsMenusMain.get(0);
    }

    public Inventory CollectionInventory(Player player, String collection)
    {
        collectionsMenus.clear();
        String inventoryTitle = plugin.formatColors(
                plugin.config.getString("gui.collection.title").replace("%collection_name%", collection));
        Inventory collectionsMenu = Bukkit.createInventory(null, 54, inventoryTitle);
        collectionsMenus.add(collectionsMenu);

        generateFiller(collectionsMenu);

        String lastPageItemName = plugin.config.getString("gui.last_page.name");
        String lastPageItemMaterial = plugin.config.getString("gui.last_page.material");
        int lastPageItemSlot = plugin.config.getInt("gui.last_page.slot");

        ItemStack lastPageItem;
        if ( lastPageItemMaterial.startsWith("hdb-") )
        {
            try { lastPageItem = HeadDatabaseHook.hdbAPI.getItemHead(lastPageItemMaterial.replace("hdb-", "")); }
            catch ( NullPointerException e ) { lastPageItem = new ItemStack(Material.PLAYER_HEAD, 1); }
        }
        else
        {
            lastPageItem = new ItemStack(Material.getMaterial(lastPageItemMaterial), 1);
        }

        ItemMeta lastPageItemMeta = lastPageItem.getItemMeta();
        lastPageItemMeta.setDisplayName(plugin.formatColors(lastPageItemName));
        lastPageItemMeta.setLocalizedName("tomainmenu");
        lastPageItem.setItemMeta(lastPageItemMeta);

        collectionsMenu.setItem(lastPageItemSlot, lastPageItem);

        int slotCount = 0;
        List<Integer> availableSlots = plugin.config.getIntegerList("gui.collection.slots");

        String rewardItemName = plugin.config.getString("collections." + collection + ".reward_item.name");
        String rewardItemMaterialName = plugin.config.getString("collections." + collection + ".reward_item.material");
        int rewardItemSlot = plugin.config.getInt("collections." + collection + ".reward_item.slot");
        List<String> rewardItemLore = plugin.config.getStringList("collections." + collection + ".reward_item.lore");

        for ( String line : rewardItemLore )
        {
            rewardItemLore.set(rewardItemLore.indexOf(line), plugin.formatColors(line));
        }

        ItemStack rewardItem;
        if ( rewardItemMaterialName.startsWith("hdb-") )
        {
            try { rewardItem = HeadDatabaseHook.hdbAPI.getItemHead(rewardItemMaterialName.replace("hdb-", "")); }
            catch ( NullPointerException e ) { rewardItem = new ItemStack(Material.PLAYER_HEAD, 1); }
        }
        else
        {
            rewardItem = new ItemStack(Material.getMaterial(rewardItemMaterialName), 1);
        }

        ItemMeta rewardItemMeta = rewardItem.getItemMeta();
        rewardItemMeta.setDisplayName(plugin.formatColors(rewardItemName));
        rewardItemMeta.setLore(rewardItemLore);
        rewardItem.setItemMeta(rewardItemMeta);

        collectionsMenu.setItem(rewardItemSlot, rewardItem);

        for ( String item :
                plugin.config.getConfigurationSection(
                        "collections." + collection + ".required_permissions").getKeys(false))
        {
            String itemPermission = plugin.config.getString(
                    "collections." + collection + ".required_permissions." + item + ".permission");

            String status;

            if ( player.hasPermission(itemPermission) )
            {
                status = "completed";
            }
            else
            {
                status = "incomplete";
            }

            status = plugin.config.getString("status." + status);

            String itemName = plugin.config.getString(
                    "collections." + collection + ".required_permissions." + item + ".name");

            List<String> itemLore = plugin.config.getStringList(
                    "collections." + collection + ".required_permissions." + item + ".lore");

            for ( String line : itemLore )
            {
                itemLore.set(itemLore.indexOf(line), plugin.formatColors(
                        line.replace("%status%", status)));
            }

            String itemMaterialName = plugin.config.getString(
                    "collections." + collection + ".required_permissions." + item + ".material");

            ItemStack taskItem;
            if ( itemMaterialName.startsWith("hdb-") )
            {
                try { taskItem = HeadDatabaseHook.hdbAPI.getItemHead(itemMaterialName.replace("hdb-", "")); }
                catch ( NullPointerException e ) { taskItem = new ItemStack(Material.PLAYER_HEAD, 1); }
            }
            else
            {
                taskItem = new ItemStack(Material.getMaterial(itemMaterialName), 1);
            }

            ItemMeta taskItemMeta = taskItem.getItemMeta();
            taskItemMeta.setDisplayName(plugin.formatColors(itemName));
            taskItemMeta.setLore(itemLore);
            taskItemMeta.setLocalizedName(item);
            taskItem.setItemMeta(taskItemMeta);

            collectionsMenu.setItem(availableSlots.get(slotCount), taskItem);
            slotCount++;

            int maximumSlot = 0;

            for ( int i : availableSlots )
            {
                if ( i >= maximumSlot ) { maximumSlot = i; }
            }

            try
            {
                if ( availableSlots.get(slotCount) > maximumSlot )
                {
                    generatePage(collectionsMenus, collectionsMenu);
                    slotCount = 0;
                }
            }
            catch ( IndexOutOfBoundsException e )
            {
                collectionsMenu = generatePage(collectionsMenus, collectionsMenu);
                collectionsMenu.setItem(rewardItemSlot, rewardItem);

                slotCount = 0;
            }

        }
        return collectionsMenus.get(0);
    }

}
