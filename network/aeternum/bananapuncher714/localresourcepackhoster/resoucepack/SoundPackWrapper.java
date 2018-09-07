package network.aeternum.bananapuncher714.localresourcepackhoster.resoucepack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SoundPackWrapper extends ResourcePackWrapper {
	protected JsonObject soundFile;
	
	public SoundPackWrapper( File resource ) throws IOException {
		super( resource );

		InputStream stream = readElement( "assets/minecraft/sounds.json" );
		if ( stream != null ) {
			soundFile = gson.fromJson( new InputStreamReader( stream ), JsonObject.class );
		} else {
			soundFile = new JsonObject();
		}
		close();
	}

	public boolean containsSound( String id ) {
		return soundFile.has( id );
	}
	
	public Set< String > getIds() {
		Set< String > values = new HashSet< String >();
		for ( Entry< String, JsonElement > element : soundFile.entrySet() ) {
			values.add( element.getKey() );
		}
		return values;
	}
	
	public void removeSound( String id ) {
		List< String > values = new ArrayList< String >();
		if ( soundFile.has( id  ) ) {
			JsonArray array = soundFile.get( id ).getAsJsonObject().get( "sounds" ).getAsJsonArray();
			for ( JsonElement obj : array ) {
				values.add( obj.getAsJsonObject().get( "name" ).getAsString() );
			}
			soundFile.remove( id );
		}
		try {
			addElement( "assets/minecraft/sounds.json", gson.toJson( soundFile ).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING );
			for ( String str : values ) {
				removeElement( "assets/minecraft/sounds/" + str + ".ogg", null );
			}
			close();
		} catch ( IOException exception ) {
			exception.printStackTrace();
		}
	}
	
	public void addSound( SoundResource sound, String parentDir, boolean force ) {
		if ( !force && soundFile.has( sound.id ) ) {
			return;
		}
		
		JsonArray arr = new JsonArray();
		arr.add( sound.toJsonObject( parentDir ) );
		JsonObject soundObject = new JsonObject();
		soundObject.add( "sounds", arr );
		
		soundFile.add( sound.id, soundObject );
		
		try {
			addElement( "assets/minecraft/sounds.json", gson.toJson( soundFile ).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING );
			addElement( "assets/minecraft/sounds/" + parentDir + "/" + sound.id + ".ogg", sound.getFile(), StandardCopyOption.REPLACE_EXISTING );
			close();
		} catch ( IOException exception ) {
			exception.printStackTrace();
		}
	}
	
	public static class SoundResource {
		public final String id;
		protected final File file;
		protected boolean stream;
		
		public SoundResource( String id, File file, boolean stream ) {
			this.id = id;
			this.file = file;
			this.stream = stream;
		}

		public boolean isStream() {
			return stream;
		}

		public void setStream( boolean stream ) {
			this.stream = stream;
		}

		public File getFile() {
			return file;
		}
		
		public JsonElement toJsonObject( String base ) {
			JsonObject obj = new JsonObject();
			obj.addProperty( "name", base + "/" + id );
			obj.addProperty( "stream", stream );
			return obj;
		}
	}
}
