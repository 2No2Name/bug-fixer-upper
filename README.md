<img src="src/main/resources/assets/bug-fixer-upper/icon.png" width="128" alt="BugFixerUpper Logo">

# BugFixerUpper

BugFixerUpper is a Minecraft mod that fixes a few critical and non-critical bugs in the game.

## Configuration
- All features are enabled by default.
- You can disable individual features by editing the mod's configuration file located in your Minecraft config folder.

## Features
Feel free to suggest more in the issue tracker! Currently, there are:

### Misc
- Leads pull on mobs and boats that the player is riding **[Client side]**
- Ghast balls, wind charges and shulker bullets do not have an extended click box for creative mode players **[Client side]**
- Curse of vanishing does not apply to items inside donkey/mule/llama chest slots.
- Save vehicle passengers at their actual position (MC-263030).
- Parrot mob spawner blocks treat all air types equally (MC-232359).
- Regenerating exit end portal does not drop torches
- Zombie villager curing improves player reputation when logged out or in different dimension

### Chunk loading and generation
- Prevent item frames from sending sounds and events during chunk loading/generation.

### Structure blocks
- Structure blocks load paintings at the correct position (Thanks to BluSpring) (MC-102223).
- Structure blocks with combined mirror and rotation place paintings and item frames with the correct rotation.

### Tick freeze
- Prevent players from unfreezing other passengers in multi-seat vehicles during tick freeze (MC-268358).

## Installation

Must be installed on the server to work in multiplayer. For usage in singleplayer worlds, the mod has to be installed on the client.
1. Download the mod from [Modrinth](https://modrinth.com/mod/bugfixerupper) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/bugfixerupper).
2. Place the downloaded `.jar` file into the `mods` folder of your Minecraft directory.
3. Launch Minecraft with the Fabric mod loader.

## Usage

- Install the mod and be happy that some bugs are fixed.