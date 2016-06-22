package us.fihgu.minigamecore.matchmaking;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MinigamePlayer
{
	protected String name;
	protected UUID uuid;
	
	protected HashMap<String, String> meta;
	
	protected MinigamePlayer(Player player)
	{
		this.name = player.getName();
		this.uuid = player.getUniqueId();
		
		meta = new HashMap<String, String>();
	}
	
	/**
	 * @return a HashMap for storing meta data for a player though the game session. <br>
	 * player's meta data will be cleared once the game session is over.
	 */
	public HashMap<String, String> getMeta()
	{
		return this.meta;
	}
	
	/**
	 * @return null if player is offline
	 */
	public Player getOnlinePlayer()
	{
		Player player =  Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline())
		{
			return player;
		}
		
		return null;
	}

	public String getName()
	{
		return this.name;
	}

	public UUID getUUID()
	{
		return this.uuid;
	}
}
