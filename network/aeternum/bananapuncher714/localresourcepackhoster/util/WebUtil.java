package network.aeternum.bananapuncher714.localresourcepackhoster.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import network.aeternum.bananapuncher714.localresourcepackhoster.httpd.MineHttpd.MineConnection;

public class WebUtil {
	public static Player getAddress( MineConnection connection ) {
		byte[] mac = connection.getClient().getInetAddress().getAddress();
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			if ( Arrays.equals( player.getAddress().getAddress().getAddress(), mac ) ) {
				return player;
			}
		}
		return null;
	}
	
	public static byte[] getMAC( InetAddress address ) {
		try {
			return NetworkInterface.getByInetAddress( address ).getHardwareAddress();
		} catch ( SocketException e ) {
			e.printStackTrace();
			return null;
		}
	}
}
