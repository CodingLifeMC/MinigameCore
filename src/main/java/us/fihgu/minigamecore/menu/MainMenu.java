package us.fihgu.minigamecore.menu;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import us.fihgu.minigamecore.matchmaking.MatchmakingManager;
import us.fihgu.toolbox.ui.Button;
import us.fihgu.toolbox.ui.StaticButtonMenu;

public class MainMenu extends StaticButtonMenu
{
	public MainMenu()
	{
		super("Minigames", 4);
		
		
		Button createLobbyButton = new Button(Material.ANVIL, "create lobby", new String[]{}, 1)
		{
			@Override
			public void onClick(InventoryClickEvent event)
			{
				new ChooseMinigameMenu().show(event.getWhoClicked());
			}
		};
		
		this.setButton(0, createLobbyButton);
		
		Button joinLobbyButton = new Button(Material.CARROT_ITEM, "join lobby", new String[]{}, 1)
		{
			@Override
			public void onClick(InventoryClickEvent event)
			{
				JoinLobbyMenu.instance.show(event.getWhoClicked());
			}
		};
		
		this.setButton(1, joinLobbyButton);
		
		Button hubButton = new Button(Material.IRON_DOOR, "Hub", new String[]{}, 1)
		{
			@Override
			public void onClick(InventoryClickEvent event)
			{
				HumanEntity entity = event.getWhoClicked();
				if(entity instanceof Player)
				{
					Player player = (Player) entity;
					player.closeInventory();
					MatchmakingManager.joinLobby(-1, player);
				}
			}
		};
		
		this.setButton(8, hubButton);
		
		this.update();
	}
}
