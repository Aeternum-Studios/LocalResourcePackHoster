package network.aeternum.bananapuncher714.localresourcepackhoster;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import network.aeternum.bananapuncher714.localresourcepackhoster.httpd.MineHttpd;
import network.aeternum.bananapuncher714.localresourcepackhoster.util.BukkitUtil;
import network.aeternum.bananapuncher714.localresourcepackhoster.util.FileUtil;
import network.aeternum.bananapuncher714.localresourcepackhoster.util.Util;
import network.aeternum.bananapuncher714.localresourcepackhoster.util.WebUtil;

public class LocalResourcePackHoster extends JavaPlugin {
	private int httpd_port;
	private String ip;
	private MineHttpd httpd;
	private boolean verbose = false;
	private boolean localhost = false;
	
	protected Map< String, File > resourcepacks = new HashMap< String, File >();
	
	@Override
	public void onEnable() {
		saveResource( "README.md", true );
		if ( !new File( getDataFolder(), "config.yml" ).exists() ) {
			getLogger().info( ChatColor.GREEN + "Detected a fresh install! Please be sure to read the README.md provided!" );
			FileUtil.saveToFile( getResource( "data/demopack.zip" ), new File( getDataFolder() + "/resourcepacks", "demopack.zip" ), false );
		}
		saveDefaultConfig();
		loadConfig();
		new File( getDataFolder(), "resourcepacks" ).mkdirs();
		
		ResourceCommand command = new ResourceCommand( this );
		getCommand( "resourcepack" ).setExecutor( command );
		getCommand( "resourcepack" ).setTabCompleter( command );
		
		startHttpd();
		
		if ( localhost ) {
			ip = Bukkit.getIp();
		} else {
			ip = BukkitUtil.getIp();
		}
		getLogger().info( "IP is '" + ip + "'" );
	}
	
	@Override
	public void onDisable() {
		httpd.terminate();
	}
	
	private void startHttpd() {
		try {
			httpd = new MineHttpd( httpd_port ) {
				@Override
				public File requestFileCallback( MineConnection connection, String request ) {
					Player player = WebUtil.getAddress( connection );
					if ( player == null ) {
						verbose( "Unknown connection from '" + connection.getClient().getInetAddress() + "'. Aborting..." );
						return null;
					}
					
					if ( !resourcepacks.containsKey( request ) ) {
						return null;
					}
					
					if ( player.hasPermission( "localresourcepack.pack." + request ) ) {
						verbose( "Serving '" + request + "' to " + player.getName() + "(" + connection.getClient().getInetAddress() + ")" );
						return resourcepacks.get( request );
					} else {
						verbose( "Denied access to '" + request + "' from " + player.getName() + " due to insufficient permissions" );
					}
					return null;
				}
				
				public void onSuccessfulRequest( MineConnection connection, String request ) {
					verbose( "Successfully served '" + request + "' to " + connection.getClient().getInetAddress() );
				}
				
				public void onClientRequest( MineConnection connection, String request ) {
					verbose( "Request '" + request + "' recieved from " + connection.getClient().getInetAddress() );
				}
				
				public void onRequestError( MineConnection connection, int code ) {
					verbose( "Error " + code + " when attempting to serve " + connection.getClient().getInetAddress() );
				}
			};
			// Start the web server
			httpd.start();
			getLogger().info( ChatColor.GREEN + "Successfully started the mini http daemon!" );
		} catch ( IOException e1 ) {
			getLogger().severe( ChatColor.RED + "Unable to start the mini http daemon! Disabling..." );
			Bukkit.getPluginManager().disablePlugin( this );
		}
	}
	
	private void loadConfig() {
		FileConfiguration config = YamlConfiguration.loadConfiguration( new File( getDataFolder(), "config.yml" ) );
		
		httpd_port = config.getInt( "port" );
		verbose = config.getBoolean( "verbose" );
		localhost = config.getBoolean( "localhost" );
		
		if ( config.contains( "packs" ) ) {
			for ( String key : config.getConfigurationSection( "packs" ).getKeys( false ) ) {
				String name = config.getString( "packs." + key );
				File file = new File( getDataFolder() + "/resourcepacks", name );
				if ( !file.exists() ) {
					getLogger().severe( ChatColor.RED + "Resource pack '" + name + "' does not exist!" );
				} else {
					getLogger().info( "Discovered resource pack '" + name + "'" );
				}
				resourcepacks.put( key, file );
			}
		} else {
			getLogger().warning( ChatColor.RED + "There are no resource packs listed in the config!" );
		}
	}
	
	/**
	 * Send a resource pack to the desired player
	 * 
	 * @param player
	 * The player to send it to, note that they must have the right permission to download the pack
	 * @param resourcepack
	 * The id of the resourcepack to send; must be valid
	 */
	public void sendResourcePack( Player player, String resourcepack ) {
		File file = resourcepacks.get( resourcepack );
		try {
			player.setResourcePack( "http://" + ip + ":" + httpd_port + "/" + resourcepack, Util.calcSHA1( file ) );
		} catch ( NoSuchAlgorithmException | IOException e ) {
			e.printStackTrace();
		}
	}
	
	private void verbose( Object object ) {
		if ( verbose ) {
			getLogger().info( object.toString() );
		}
	}
	
	public Map< String, File > getResourcePacks() {
		return resourcepacks;
	}
}
