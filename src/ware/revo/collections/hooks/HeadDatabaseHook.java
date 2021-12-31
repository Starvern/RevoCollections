package ware.revo.collections.hooks;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ware.revo.collections.RevoCollections;

public class HeadDatabaseHook implements Listener
{
    public static HeadDatabaseAPI hdbAPI;
    private final RevoCollections plugin;

    public HeadDatabaseHook(RevoCollections pl)
    {
        this.plugin = pl;
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event)
    {
        hdbAPI = new HeadDatabaseAPI();
    }
}
