package us.fihgu.minigamecore.game;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import us.fihgu.minigamecore.matchmaking.GameSession;
import us.fihgu.minigamecore.matchmaking.Lobby;
import us.fihgu.toolbox.item.ItemUtils;

/**
 * Instead of being an instance of a single minigame, each Minigame instance describes how a type of minigame works. <br>
 * Each game instance is represented by the {@link GameSession} class. <br>
 */
public abstract class Minigame
{
	/**
	 * the id of this minigame, must be unique, and contains no space or special character.
	 */
	protected String id;
	protected String name;
	
	/**
	 * A one line description of this minigame.
	 */
	protected String description;
	
	protected int minPlayer = 3;
	protected int maxPlayer = 12;
	
	protected ItemStack icon;
	
	public String getId()
	{
		return this.id;
	}
	
	/**
	 * creates a list of fresh new game phases.
	 */
	public abstract LinkedList<GamePhase> createGamePhases(GameSession session);
	
	/**
	 * @return the possible maps for this minigame.<br>
	 */
	public abstract Collection<String> getMaps();
	
	public int getMinPlayer()
	{
		return this.minPlayer;
	}
	
	public int getMaxPlayer()
	{
		return this.maxPlayer;
	}
	
	/**
	 * create a custom lobby for this minigame.
	 */
	public Lobby createLobby()
	{
		Lobby lobby = new Lobby();
		return lobby;
	}
	
	public ItemStack getIcon()
	{
		if(this.icon == null)
		{
			return getDefaultIcon(this);
		}
		else
		{
			return this.icon;
		}
	}
	
	public static ItemStack getDefaultIcon(Minigame minigame)
	{
		ItemStack icon = new ItemStack(Material.APPLE);
		ItemUtils.setDisplayName(icon, minigame.name);
		ItemUtils.setLore(icon, minigame.description);
		
		return icon;
	}

	public String getName()
	{
		return this.name;
	}
}
