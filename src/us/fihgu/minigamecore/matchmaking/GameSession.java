package us.fihgu.minigamecore.matchmaking;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.World;

import us.fihgu.minigamecore.game.GamePhase;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.toolbox.world.MapManager;

/**
 * represents a single session of minigame. <br>
 * A session is divided into multiple {@link GamePhase}.
 */
public class GameSession
{
	protected Lobby lobby;
	
	protected World world;
	protected Map map;
	
	protected Minigame game;
	protected LinkedList<GamePhase> gamePhases;
	protected GamePhase currentPhase = null;
	
	protected HashMap<String, String> meta;
	
	public void start()
	{
		meta = new HashMap<String, String>();
		
		//build world
		world = MapManager.createWorld(this.getMapName(), new File(this.map.path));
		
		//start phase
		this.nextPhase();
	}
	
	public void onFinish()
	{
		MapManager.deleteWorld(this.getMapName(), this.lobby.world.getSpawnLocation());
		
		//clear player meta
		for(UUID uuid : this.getPlayers().keySet())
		{
			MinigamePlayer player = this.getPlayers().get(uuid);
			player.meta = null;
		}
		
		this.meta = null;
		this.lobby.currentSession = null;
	}
	
	public String getMapName()
	{
		return this.lobby.name + "-" + this.map.name;
	}
	
	public void nextPhase()
	{
		if(currentPhase != null)
		{
			currentPhase.onExitingPhase();
		}
		
		if(gamePhases.size() > 0)
		{
			this.currentPhase = gamePhases.pop();
			this.currentPhase.onEnteringPhase();
		}
		else
		{
			this.onFinish();
		}
	}
	
	public HashMap<String, String> getMeta()
	{
		return this.meta;
	}
	
	public HashMap<UUID, MinigamePlayer> getPlayers()
	{
		return this.lobby.getPlayers();
	}
	
	public World getWorld()
	{
		return this.world;
	}
	
	public Map getMap()
	{
		return this.map;
	}
}
