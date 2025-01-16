<h1 align="center">AfkMinus</h1>

<p align="center">
    <img alt="Carpet Bot Placeholder" height="256" src="./src/main/resources/assets/afkminus/icon.png" title="Mod Icon" width="256"/>
</p>

## This is a pretty simple fabric mod that adds a configurable placeholder for players that haven't moved in a while, inspired by the [AfkPlus](https://modrinth.com/mod/afkplus) mod

** **
## Placeholder

### This mod adds a placeholder that you can use along other mods like [Styled Playerlist](https://github.com/Patbox/StyledPlayerList) and [Styled Chat](https://github.com/Patbox/StyledChat):
###### *Placeholders can be changed in the [config](#config) or with the command /afkminus set \<config option\> \<value\>*
 - `%afkminus:afk%` *(Placeholder for players who haven't sent an input in **time_until_afk** seconds)*

** **
## Config

The `afk_placeholder` config values use [Simplified Text Format](https://placeholders.pb4.eu/user/text-format/)

#####  To reload the config in game, use the command `/afkminus reload`
*Example & default config, file located here: `./config/AfkMinus.json`*
```json
{
  "config_version_DO_NOT_TOUCH": "1.0.0",
  "afk_placeholder": "<i><dark_gray>[<gray>AFK</gray>]</dark_gray></i> ",
  "time_until_afk": 300
}
```

** **
## Commands

##### This mod adds 1 command with 5 subcommands detailed below
*(Permissions required detailed in the [Permissions](#Permissions) section)*

`/afkminus reload` *- Reloads the config from the file*<br>
`/afkminus set <key> <value>` *- Changes the config from in game, reload not needed*

`/afkminus force <player(s)>` *- Forces the players given to be marked as afk, removed on player input*<br>
`/afkminus reset <player(s)>` *- Resets the last input time and forced afk status*

`/afkminus getstatus <player>` *- See the afk status of a player (afk, time, forced or not, by who)*<br>
Additionally, you can use it for command blocks/datapacks like so `/execute store result score <...> run afkminus getstatus <player>`, if player is afk the score will be set to 0 and 1 if the player is afk


** **
## Permissions
Base command (`/afkminus`) requires `afkminus.command.afkminus.base` or at least permission level 2 to see/use sub commands

`/afkminus reload` - `afkminus.command.afkminus.reload` || Permission level 4<br>
`/afkminus set <key> <value>` - `afkminus.command.afkminus.set` || Permission level 4<br>
    **- Can be additionally restricted with `afkminus.command.afkminus.set.<config_value>`, for example with `afkminus.command.afkminus.set.time_until_afk`, a player cannot edit the placeholder**


`/afkminus force <player(s)>` - `afkminus.command.afkminus.force` || Permission level 2<br>
`/afkminus reset <player(s)>`- `afkminus.command.afkminus.reset` || Permission level 2

`/afkminus getstatus <player>` - `afkminus.command.afkminus.getstatus` || Permission level 2

** **
### Known issues/oddities
#### Carpet Shadowed players not being added to the afk playerlist due to their joining order, leading them to be imcompatible with the mod
