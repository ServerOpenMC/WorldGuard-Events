package net.raidstone.wgevents;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * @author Weby &amp; Anrza (info@raidstone.net)
 * @since 2/24/19
 */
public class WorldGuardEvents implements Listener {
    static RegionContainer container;

    public void enable(JavaPlugin plugin) {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(Entry.factory, null);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onDisable(PluginDisableEvent event)
    {
        container = null;
    }
    
    /**
     * Gets the regions a player is currently in.
     *
     * @param playerUUID UUID of the player in question.
     * @return Set of WorldGuard protected regions that the player is currently in.
     */
    @Nonnull
    public static Set<ProtectedRegion> getRegions(UUID playerUUID)
    {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline())
            return Collections.emptySet();
        
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        return set.getRegions();
    }
    
    /**
     * Gets the regions names a player is currently in.
     *
     * @param playerUUID UUID of the player in question.
     * @return Set of Strings with the names of the regions the player is currently in.
     */
    @Nonnull
    public static Set<String> getRegionsNames(UUID playerUUID)
    {
        return getRegions(playerUUID).stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
    }
    
    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionNames Set of regions to check.
     * @return True if the player is in (all) the named region(s).
     */
    public static boolean isPlayerInAllRegions(UUID playerUUID, Set<String> regionNames)
    {
        Set<String> regions = getRegionsNames(playerUUID);
        if(regionNames.isEmpty()) throw new IllegalArgumentException("You need to check for at least one region !");
        
        return regions.containsAll(regionNames.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }
    
    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionNames Set of regions to check.
     * @return True if the player is in (any of) the named region(s).
     */
    public static boolean isPlayerInAnyRegion(UUID playerUUID, Set<String> regionNames)
    {
        Set<String> regions = getRegionsNames(playerUUID);
        if(regionNames.isEmpty()) throw new IllegalArgumentException("You need to check for at least one region !");
        for(String region : regionNames)
        {
            if(regions.contains(region.toLowerCase()))
                return true;
        }
        return false;
    }
    
    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionName List of regions to check.
     * @return True if the player is in (any of) the named region(s).
     */
    public static boolean isPlayerInAnyRegion(UUID playerUUID, String... regionName)
    {
        return isPlayerInAnyRegion(playerUUID, new HashSet<>(Arrays.asList(regionName)));
    }
    
    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionName List of regions to check.
     * @return True if the player is in (any of) the named region(s).
     */
    public static boolean isPlayerInAllRegions(UUID playerUUID, String... regionName)
    {
        return isPlayerInAllRegions(playerUUID, new HashSet<>(Arrays.asList(regionName)));
    }
    
}
