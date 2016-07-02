package us.fihgu.minigamecore.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.md_5.bungee.api.ChatColor;
import us.fihgu.minigamecore.game.Minigame;
import us.fihgu.minigamecore.game.MinigameManager;
import us.fihgu.minigamecore.matchmaking.MatchmakingManager;
import us.fihgu.minigamecore.mysql.DatabaseManager;
import us.fihgu.toolbox.ui.Button;
import us.fihgu.toolbox.ui.ListButtonMenu;

public class ChooseMinigameMenu extends ListButtonMenu
{
	public ChooseMinigameMenu()
	{
		super("Choose minigame", 6);
		
		for(String minigameId : MinigameManager.getMinigames().keySet())
		{
			Minigame minigame = MinigameManager.getMinigame(minigameId);
			this.addButton(new MinigameButton(minigame));
		}
		
		this.update();
	}
}

class MinigameButton extends Button
{
	Minigame minigame = null;
	public MinigameButton(Minigame minigame)
	{
		super(minigame.getIcon());
		this.minigame = minigame;
	}

	@Override
	public void onClick(InventoryClickEvent event)
	{
		HumanEntity clicker = event.getWhoClicked();
		if(clicker instanceof Player)
		{
			Player player = (Player)clicker;
			String serverName = DatabaseManager.getEmptiestServer();
			if(serverName != null)
			{
				int lobbyId = DatabaseManager.registerNewLobby(minigame, serverName);
				
				if(lobbyId >= 0)
				{
					player.closeInventory();
					MatchmakingManager.joinLobby(lobbyId, player);
					return;
				}
			}
			player.closeInventory();
			player.sendMessage(ChatColor.DARK_RED + "Sorry, we can not open a lobby for you at the moment.");
		}
	}
}
