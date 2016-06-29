package us.fihgu.minigamecore.matchmaking;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.mysql.DatabaseManager;
import us.fihgu.toolbox.world.MapManager;

public class Lobby
{
	protected String name;
	protected String description;
	
	/**
	 * map for the lobby when players are voting for next game
	 */
	protected Map map;
	protected World world;
	protected HashMap<UUID, MinigamePlayer> players;
	
	protected GameSession currentSession = null;
	public int id;
	
	public Lobby()
	{
		this.name = "untitled";
		this.description = "no describtion.";
		
		this.map = new Map("lobby", "./minigamecore/maps/lobby/");
	}
	
	public void startSession(GameSession session)
	{
		this.currentSession = session;
		this.currentSession.start();
	}
	
	public void init()
	{
		players = new HashMap<UUID, MinigamePlayer>();
		this.world = MapManager.createWorld(name, new File(map.path));
	}
	
	public HashMap<UUID, MinigamePlayer> getPlayers()
	{
		return this.players;
	}
	
	public void addPlayer(Player player)
	{
		this.addPlayer(new MinigamePlayer(player));
	}
	
	public void addPlayer(MinigamePlayer player)
	{
		this.players.put(player.getUUID(), player);
		//TODO: what about inspector spawn?
		player.getOnlinePlayer().teleport(this.world.getSpawnLocation());
		DatabaseManager.setPlayerCount(id, this.getPlayers().size());
	}
	
	public void removePlayer(MinigamePlayer player)
	{
		this.players.remove(player.getUUID());
		DatabaseManager.setLobby(player, NetworkManager.getHubServer());
		NetworkManager.instance.sendPlayerToServer(player.getOnlinePlayer(), NetworkManager.getHubServer());
		
		//set playerCount in database, remove lobby if empty
		int playerCount = this.getPlayers().size();
		if(playerCount > 0)
		{
			DatabaseManager.setPlayerCount(id, playerCount);
		}
		else
		{
			MatchmakingManager.removeLocalLobby(this);
		}
		
	}
}
