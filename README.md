# **Local Resource Pack Hoster**
### What is this?
Local Resource Pack Hoster allows your server to self-serve resource pack requests. An additional port is required for the mini http daemon used to handle client requests.

### Cool, so how does this work?
LocalResourcePackHoster hosts a mini http daemon which listens for client requests. Once it recieves a request, it will check if the ip of the client requesting matches an online player's ip. If so, it will check if the player has permission to access the resource pack requested. If so, it will send it to the player.

### How secure is it?
The mini http daemon only serves resource packs inside the config, and no more than that. Premium resource packs are safe and can only be accessed by people with the right permissions, since the ip of the request and player are checked so that it's not possible to request a resource pack from a computer that is not connected to the server.

### Commands
**/resourcepack <id> [player]** - Lets players download resource packs they have access to, and allows server admins or the console to prompt the desired player with a custom resource pack.

### Permissions
* localresourcepack.user - True by default; lets players change their own resource pack.
* localresourcepack.admin - OP by default; lets admins set players' resource packs. The desired player must have permission to use the resource pack specified.
* localresourcepack.pack.<id> - OP by default; lets players download the resource pack.

### The Config
Requires a restart to update in-game.  
```YAML
#The port to use for the mini-http daemon  
port: 40021
# Whether or not the console should print a message whenever a player attempts to fetch a resource pack
verbose: false
# Whether or not the server is localhost; Useful for testing situations if you're not able to open a port
localhost: false
# The packs are <id>: <path-to-resource-pack>
# The packs should be placed inside the '/plugins/LocalResourcePackHoster/resourcepacks/' folder
packs:
  demo: "demopack.zip"
```

### For developers
You can add/remove/view resource packs loaded by getting the LocaleResourcePackHoster instance like so:
```Java
LocalResourcePackHoster plugin = LocalResourcePackHoster.getPlugin( LocaleResourcePackHoster.class );
Map< String, File > resourcepacks = plugin.getResourcePacks();
```
Send a resource pack to a player like this:
```Java
Player player;
String resourcePackId;
plugin.sendResourcePack( player, resourcePackId );
```
Note that the resource pack id has to be a valid resource pack and the player must have permission to get it

### Authors
- BananaPuncher714