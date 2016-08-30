package us.fihgu.minigamecore.menu;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.game.MinigameManager;
import us.fihgu.minigamecore.matchmaking.MatchmakingManager;
import us.fihgu.minigamecore.matchmaking.MinigamePlayer;
import us.fihgu.minigamecore.mysql.DatabaseManager;
import us.fihgu.toolbox.item.ItemUtils;
import us.fihgu.toolbox.ui.Button;
import us.fihgu.toolbox.ui.ListButtonMenu;

public class JoinLobbyMenu extends ListButtonMenu
{

	public static JoinLobbyMenu instance = new JoinLobbyMenu();

	private JoinLobbyMenu()
	{
		super("Join lobby", 6);
		this.update();
	}

	@Override
	public void update()
	{
		this.buttons.clear();

		for (int lobbyId : DatabaseManager.getLobbyList())
		{
			this.addButton(new JoinLobbyButton(lobbyId));
		}

		super.update();
	}
}

class JoinLobbyButton extends Button
{

	private int lobbyId = -1;

	public JoinLobbyButton(int lobbyId)
	{
		super(getIcon(lobbyId));
		this.lobbyId = lobbyId;
	}

	private static ItemStack getIcon(int lobbyId)
	{
		String minigameId = DatabaseManager.getMinigame(lobbyId);
		if (minigameId != null)
		{
			Minigame minigame = MinigameManager.getMinigame(minigameId);
			int playerCount = DatabaseManager.getPlayerCount(lobbyId);
			ItemStack icon = null;
			
			if(minigame != null)
			{
				icon = minigame.getIcon();
				ItemUtils.setDisplayName(icon, minigame.getName());
				ItemUtils.setLore(icon, new String[] { "Lobby ID: " + lobbyId, "Players: " + playerCount + "/" + minigame.getMaxPlayer() });
			}
			
			return icon;
		}

		return new ItemStack(Material.POTATO);
	}

	@Override
	public void onClick(InventoryClickEvent event)
	{
		HumanEntity clicker = event.getWhoClicked();
		if (clicker instanceof Player)
		{
			MinigamePlayer player = new MinigamePlayer((Player) clicker);
			
			if (this.lobbyId >= 0)
			{
				//check minigame max player
				String minigameId = DatabaseManager.getMinigame(lobbyId);
				if (minigameId != null)
				{
					Minigame minigame = MinigameManager.getMinigame(minigameId);
					int playerCount = DatabaseManager.getPlayerCount(lobbyId);
					
					if(playerCount >= minigame.getMaxPlayer())
					{
						player.getOnlinePlayer().sendMessage(ChatColor.GRAY + "Sorry, that lobby is full.");
						return;
					}
				}
				
				MatchmakingManager.joinLobby(lobbyId, player.getOnlinePlayer());			
			}
		}
	}
}