package network.aeternum.bananapuncher714.localresourcepackhoster.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class BukkitUtil {
	private static final String IP_URL = "https://api.ipify.org";
	
	public static String getIp() {
		try {
			URL url = new URL( IP_URL );
			InputStream stream = url.openStream();
			Scanner s = new Scanner( stream, "UTF-8" ).useDelimiter( "\\A" );
			String ip = s.next();
			s.close();
			stream.close();
			return ip;
		} catch ( IOException e ) {
		    e.printStackTrace();
		    return null;
		}
	}
}
