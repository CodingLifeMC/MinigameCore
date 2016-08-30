package us.fihgu.minigamecore.matchmaking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import us.fihgu.minigamecore.game.GamePhase;
import us.fihgu.minigamecore.game.Minigame;

/**
 * represents a single session of minigame. <br>
 * A session is divided into multiple {@link GamePhase}.
 */
public class GameSession
{
	protected Lobby lobby;
	
	protected Minigame game;
	protected LinkedList<GamePhase> gamePhases;
	protected GamePhase currentPhase = null;
	
	protected HashMap<String, String> meta;
	
	public GameSession(Lobby lobby, Minigame game)
	{
		this.lobby = lobby;
		this.game = game;
		
		this.gamePhases = this.game.createGamePhases(this);
	}
	
	public void start()
	{
		meta = new HashMap<String, String>();
		
		//start phase
		this.nextPhase();
	}
	
	public void onFinish()
	{
		//clear player meta
		for(UUID uuid : this.getPlayers().keySet())
		{
			MinigamePlayer player = this.getPlayers().get(uuid);
			player.meta = null;
		}
		
		this.meta = null;
		this.lobby.currentSession = null;
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
}
