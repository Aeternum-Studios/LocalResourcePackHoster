package network.aeternum.bananapuncher714.localresourcepackhoster.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	public static String calcSHA1String( File file ) throws IOException, NoSuchAlgorithmException {
		return bytesToHexString( calcSHA1( file ) );
	}

	public static byte[] calcSHA1( File file ) throws IOException, NoSuchAlgorithmException {
		FileInputStream fileInputStream = new FileInputStream( file );
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
		byte[] bytes = new byte[1024];
		// read all file content
		while (digestInputStream.read(bytes) > 0);

		byte[] resultByteArry = digest.digest();
		digestInputStream.close();
		return resultByteArry;
	}
	
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			int value = b & 0xFF;
			if (value < 16) {
				// if value less than 16, then it's hex String will be only
				// one character, so we need to append a character of '0'
				sb.append("0");
			}
			sb.append(Integer.toHexString(value).toUpperCase());
		}
		return sb.toString();
	}

	public static void saveFile( String url, File file ) {
		try {
			file.getParentFile().mkdirs();
			URL website = new URL( url );
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream( file );
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	public static boolean isYoutubeURL( String url ) {
		return url.matches( "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+" );
	}

	public static void recursiveDelete( File file ) {
		if ( file.isDirectory() ) {
			for ( File element : file.listFiles() ) {
				recursiveDelete( element );
			}
		}
		file.delete();
	}
}
