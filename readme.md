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
