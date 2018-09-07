package network.aeternum.bananapuncher714.localresourcepackhoster;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ResourceCommand implements CommandExecutor, TabCompleter {
	private LocalResourcePackHoster plugin;
	
	public ResourceCommand( LocalResourcePackHoster plugin ) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( args.length == 0 ) {
			showHelp( sender );
			return false;
		} else if ( args.length == 1 ) {
			givePack( sender, args );
		} else if ( args.length == 2 ) {
			givePackToPlayer( sender, args );
		} else {
			showHelp( sender );
			return false;
		}
		return false;
	}
	
	private void givePack( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "localresourcepack.user" ) ) {
			sender.sendMessage( ChatColor.RED + "You do not have permission to run this command!" );
			return;
		}
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
				String fileName = args[ 0 ];
				Map< String, File > packs = plugin.getResourcePacks();
				if ( !packs.containsKey( fileName ) || !player.hasPermission( "localresourcepack.pack." + fileName ) ) {
					sender.sendMessage( ChatColor.RED + fileName + " does not exist!" );
					return;
				}
				plugin.sendResourcePack( player, fileName );
		} else {
			sender.sendMessage( ChatColor.RED + "You need to specify a player to send a resource pack to!" );
		}
	}
	
	private void givePackToPlayer( CommandSender sender, String[] args ) {
		if ( !sender.hasPermission( "localresourcepack.admin" ) ) {
			sender.sendMessage( ChatColor.RED + "You do not have permission to run this command!" );
			return;
		}
		
		String fileName = args[ 0 ];
		Player player = Bukkit.getPlayer( args[ 1 ] );
		
		if ( player == null ) {
			sender.sendMessage( ChatColor.RED + args[ 1 ] + " is not online right now!" );
			return;
		}
		
		Map< String, File > packs = plugin.getResourcePacks();
		if ( !packs.containsKey( fileName ) || !player.hasPermission( "localresourcepack.pack." + fileName ) ) {
			sender.sendMessage( ChatColor.RED + "'" + fileName + "' does not exist!" );
			return;
		}
		sender.sendMessage( ChatColor.GREEN + "Sent '" + fileName + "' to " + player.getName() );
		plugin.sendResourcePack( player, fileName );
	}
	
	private void showHelp( CommandSender sender ) {
		if ( sender.hasPermission( "localresourcepack.admin" ) ) {
			sender.sendMessage( ChatColor.RED + "Usage: /resourcepack <id> [player]" );
		} else if ( sender.hasPermission( "localresourcepack.user" ) ) {
			sender.sendMessage( ChatColor.RED + "Usage: /resourcepack <id>" );
		} else {
			sender.sendMessage( ChatColor.RED + "You do not have permission to run this command!" );
		}
	}

	@Override
	public List< String > onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
		List< String > aos = new ArrayList< String >();
		List< String > completions = new ArrayList< String >();
		
		if ( args.length == 1 ) {
			if ( sender.hasPermission( "localresourcepack.admin" ) || sender.hasPermission( "localresourcepack.user" ) ) {
				for ( String string : plugin.getResourcePacks().keySet() ) {
					if ( sender.hasPermission( "localresourcepack.pack." + string ) ) {
						aos.add( string );
					}
				}
			}
		} else if ( args.length == 2 ) {
			if ( sender.hasPermission( "localresourcepack.admin" ) ) {
				for ( Player player : Bukkit.getOnlinePlayers() ) {
					aos.add( player.getName() );
				}
			}
		}
		
		Collections.sort( completions );
		return StringUtil.copyPartialMatches( args[ args.length - 1 ], aos, completions );
	}

}
