# (Legacy) Brigadier
## Minecraft Brigadier in 1.7.10
The minecraft brigadier command manager introduced in 1.13 was impressive compared to the older command system. I've converted some of the brigadier code to work in 1.7.10 minecraft.
No this implementation is not perfect and it requires the server and client to have the mod installed. It has the legacy 
command packets built into the brigadier support, but the client does not send the legacy packets anymore. 
I couldn't get the implementation to work how I wanted so it requires server and client to have the mod.

I wish to make the code even more abstract to make it easier to implement in the legacy minecraft versions, ex 1.8.8 and 1.12.2. This currently is possible, but the 1.7.10 implementation still needs tweakings I am not able to make right now.

## Videos

### How brigadier commands are shown in the 1.21.4 minecraft
https://www.youtube.com/watch?v=K1wIR5Vix4A&list=PLK5blL1iKf7YQzdKSh5VS4DHu72gNhhun

### How legacy minecraft command manager shows the commands in 1.7.10
The command manager just pushes the commands to the chat which can affect the gameplay of the players.
I dislike this command system because of it. It can leave the 100+ suggestions visible in the chat window.

https://www.youtube.com/watch?v=veaeDM4pNvg&list=PLK5blL1iKf7YQzdKSh5VS4DHu72gNhhun&index=3

### How I've implemented brigadier to 1.7.10
I've implemented the 1.21.4 command suggestion system to the 1.7.10 command suggestions. It no longer spills out the commands to the chat and leaves them there.
It now shows them in the correct position above everything else in the chat box. 

https://www.youtube.com/watch?v=0-1AxBUvNmA&list=PLK5blL1iKf7YQzdKSh5VS4DHu72gNhhun&index=2

## What did I change?
### "API" to allow legacy and modern brigadier to be used.
https://github.com/Antritus/LegacyBrigadier/tree/master/src/main/java/bet/astral/flunkie

The implementation of the brigadier command library to support legacy and brigadier commands
https://github.com/Antritus/LegacyBrigadier/blob/master/src/main/java/bet/astral/flunkie/command/CommandManager.java

Rewrite of the networking to server and client
https://github.com/Antritus/LegacyBrigadier/tree/master/src/main/java/bet/astral/flunkie/network

Text components for minecraft Language (for example converts key.lang to Language) and text components (this is text)
https://github.com/Antritus/LegacyBrigadier/tree/master/src/main/java/bet/astral/flunkie/text

Basic registry interfaces to be implemented by the client
https://github.com/Antritus/LegacyBrigadier/tree/master/src/main/java/bet/astral/flunkie/registry

### 1.7.10 Minecraft Client and Server
https://github.com/Antritus/LegacyBrigadier/tree/master/forge-1.7.10

I rewrote the entire suggestion list system from the client side to have proper control over everything in the suggestions.

https://github.com/Antritus/LegacyBrigadier/blob/master/forge-1.7.10/src/main/java/bet/astral/flunkie/forge/client/gui/SuggestionList.java

Wrote a new class to render suggestions in the chat when typing commands
https://github.com/Antritus/LegacyBrigadier/blob/49f5fc5037734f00569ee71ad74ecaed3d8bba9f/forge-1.7.10/src/main/java/bet/astral/flunkie/forge/client/gui/SuggestionGui.java#L133C1-L184C6

Also made methods to receive legacy packets and more modern packets in the same class
https://github.com/Antritus/LegacyBrigadier/blob/49f5fc5037734f00569ee71ad74ecaed3d8bba9f/forge-1.7.10/src/main/java/bet/astral/flunkie/forge/client/gui/SuggestionGui.java#L186C1-L219C6

Used "Mixin"s to overwrite or add to methods in the minecraft source. Basically adding more code to the minecraft server/client to have it run like I want it to run. 
Mixins are required for this to have the client and server work like I want with the brigadier command library

https://github.com/Antritus/LegacyBrigadier/tree/master/forge-1.7.10/src/main/java/bet/astral/flunkie/forge/mixin

This is the command manager overwrite

https://github.com/Antritus/LegacyBrigadier/blob/master/forge-1.7.10/src/main/java/bet/astral/flunkie/forge/mixin/common/command/CommandHandlerMixin.java


