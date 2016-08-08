package us.fihgu.minigamecore.matchmaking;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import us.fihgu.minigamecore.bungeecord.NetworkManager;
import us.fihgu.minigamecore.mysql.DatabaseManager;

public class Lobby
{

	public static Map defaultMap = new Map("lobby", "./minigamecore/maps/lobby/");

	protected String name;
	protected String description;

	/**
	 * a lobby usually hold a single world, but GamePhases may create more
	 * worlds if that's necessary.
	 */
	protected World world;
	protected HashMap<UUID, MinigamePlayer> players;

	protected GameSession currentSession = null;
	public int id;

	public Lobby(int id)
	{
		this.id = id;
		players = new HashMap<UUID, MinigamePlayer>();
	}

	public void startSession(GameSession session)
	{
		this.currentSession = session;
		this.currentSession.start();
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
		if (this.world != null)
		{
			player.getOnlinePlayer().teleport(this.world.getSpawnLocation());
			player.getOnlinePlayer().setBedSpawnLocation(this.world.getSpawnLocation(), true);
		}

		DatabaseManager.setLobby(player, id);
		DatabaseManager.setPlayerCount(id, this.getPlayers().size());
	}

	/**
	 * remove given player from this lobby, return him to the hub.
	 */
	public void removePlayer(MinigamePlayer player)
	{
		unregisterPlayer(player);
		if(player.getOnlinePlayer() != null)
		{
			NetworkManager.instance.sendPlayerToServer(player.getOnlinePlayer(), NetworkManager.getHubServer());
		}
	}
	
	/**
	 * unregister the given player from this lobby, but not move the player else where.
	 */
	public void unregisterPlayer(MinigamePlayer player)
	{
		this.players.remove(player.getUUID());
		DatabaseManager.setLobby(player, -1);

		// set playerCount in database, remove lobby if empty
		int playerCount = this.getPlayers().size();
		if (playerCount > 0)
		{
			DatabaseManager.setPlayerCount(id, playerCount);
		}
		else
		{
			MatchmakingManager.removeLocalLobby(this);
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	public GameSession getCurrentSession()
	{
		return currentSession;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
