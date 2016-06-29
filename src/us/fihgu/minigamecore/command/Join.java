package us.fihgu.minigamecore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.fihgu.minigamecore.bungeecord.NetworkManager;

public class Join implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if(args.length == 1)
			{
				String serverName = args[0];
				
				NetworkManager.instance.sendPlayerToServer(player, serverName);
			}
		}
		
		return true;
	}
}
