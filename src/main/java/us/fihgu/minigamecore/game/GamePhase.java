package us.fihgu.minigamecore.game;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import us.fihgu.minigamecore.Loader;
import us.fihgu.minigamecore.matchmaking.GameSession;

public abstract class GamePhase
{
	protected GameSession session;
	
	public GamePhase(GameSession session)
	{
		this.session = session;
	}
	
	private LinkedList<Listener> listeners = new LinkedList<>();
	
	/**
	 * called before the phase starts.<br>
	 * events should be registered here with the {@link #registerListener(Listener)} method<br>
	 */
	public abstract void onEnteringPhase();
	
	/**
	 * called after the game phase is finished or when the game is exited, or when you call {@link #exitPhase()}
	 */
	public abstract void onExitingPhase();
	
	
	/**
	 * listeners registered with this method are automatically unregistered after the phase is finished.
	 */
	protected void registerListener(Listener listener)
	{
		this.listeners.add(listener);
		Bukkit.getPluginManager().registerEvents(listener, Loader.instance);
	}
	
	/**
	 * unregister all listeners for this phase.
	 */
	private void unregisterListeners()
	{
		Listener listener;
		while(listeners.size() > 0)
		{
			listener = listeners.pop();
			HandlerList.unregisterAll(listener);
		}
	}
	
	/**
	 * call this method to finish the game phase.
	 */
	public void exitPhase()
	{
		this.onExitingPhase();
		this.unregisterListeners();
		this.session = null;
	}
}
