package network.aeternum.bananapuncher714.localresourcepackhoster.resoucepack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ResourcePackWrapper extends ZipWrapper {
	protected final Gson gson;
	protected JsonObject packMcmeta;
	protected boolean hasChanged = false;
	protected String description;
	
	public ResourcePackWrapper( File resource ) throws IOException {
		super( resource );
		gson = new GsonBuilder().setPrettyPrinting().create();

		InputStream stream = readElement( "pack.mcmeta" );
		if ( stream != null ) {
			packMcmeta = gson.fromJson( new InputStreamReader( stream ), JsonObject.class );
			JsonElement element = packMcmeta.get( "pack" );
			description = element.getAsJsonObject().get( "description" ).getAsString();
			close();
		} else {
			description = "A resource pack";
			packMcmeta = new JsonObject();
			JsonObject pack = new JsonObject();
			pack.addProperty( "pack_format", 3 );
			pack.addProperty( "description", description );
			packMcmeta.add( "pack", pack );
			hasChanged = true;
			save();
		}
	}

	public void setDescription( String description ) {
		hasChanged = hasChanged || !description.equalsIgnoreCase( this.description );
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean hasUnsavedChanges() {
		return hasChanged;
	}
	
	public void save() {
		if ( hasChanged ) {
			hasChanged = false;
		}
		packMcmeta.get( "pack" ).getAsJsonObject().addProperty( "description", description );
		try {
			addElement( "pack.mcmeta", gson.toJson( packMcmeta ).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING );
			close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}
