# Event Framework 📦
## An Event-Hosting Plugin for Paper 1.20.4

--- 

> ## 🔧 Support Notice
> 
> ---
> I only use this plugin for a small community Minecraft Server, so I don't plan on maintaining this much further than
> what is necessary for that server. 
> 
> - I'll add new events when I feel like it but PRs are welcome for adding new events yourself. 
>   - Please don't request any events via Issues <3
> - I'll only provide support for the latest version of Minecraft
> - I'll fix most reported bugs unless they require I major rewrite.


This Paper plugin acts as an engine for running and hosting in-world events on a Minecraft server, with a focus on
configurability and simplicity. It currently has support for creative building, however, there are plans to add more
minigame focused events like hide & seek and paintball!


## Using the Plugin

This plugin features autocomplete for its commands which should make working with it a bit easier! Either way, here's a
little guide to the commands below:

## Commands

| Command                                 | Example                     | Permission             | Description                                                                                                                       | 
|-----------------------------------------|-----------------------------|------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| /event new \<event_type\>               | /event new open_build       | `eventfw.admin.manage` | Creates a new event of a certain type in 'staging'. Players cannot join this yet but it can be configured with `/event configure` |
| /event stop                             | /event stop                 | `eventfw.admin.manage` | Stops & clears any currently running or staged events.                                                                            |
| /event configure \<property\> \<value\> | /event configure name "bob" | `eventfw.admin.manage` | Sets properties on the currently running / staged event. See "/event configure" Options below.                                    |
| /event launch                           | /event launch               | `eventfw.admin.manage` | Launches a 'staged' event. Players can join as soon as this is ran. See `/event new` on how to create a staged event.             |
| /join                                   | /join                       | N/A                    | Adds yourself to the currently running server event.                                                                              |


### "/event configure" Options:

> #### Defaults ⚠️
> 
> ---
> These options can have varying defaults depending on the event type specified in `/event new ...`
> All built-in event types will only vary from the defaults in favour of safety for players.

| Property                     | Default                  | Description                                                                                                                                               |
|------------------------------|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `name`                       | `"General Shenanigans!"` | Sets the name displayed when an event is advertised.                                                                                                      |
| `description`                | `"..."`                  | Sets the longer description displayed when an event is advertised.                                                                                        |
| `player_limit`               | `1000`                   | Sets a guidance player limit, stopping new joins to the event when capacity is reached.                                                                   |
| `announce_event_start`       | `true`                   | Should the event be announced when a player joins the server & when it starts?                                                                            |
| `dimension`                  | N/A                      | Which world should players be teleported to when they join the event? N/A keeps them in the world they ran /join in.                                      |
| `prevent_dimension_switches` | `true`                   | Can players switch dimensions while participating in the event?                                                                                           |
| `spawnpoint`                 | N/A                      | Where should players be teleported to when they run /join? Setting this causes the event to not teleport them unless an event border or dimension is set. |
| `border`                     | N/A                      | Should a mini world border be applied to people participating in the event? N/A means that the vanilla world border isn't overriden.                      |
| `disable_ender_chests`       | `false`                  | Should access to ender-chests be blocked while participating in this event? Only affects vanilla ender-chest blocks.                                      |
| `disable_player_drops`       | `false`                  | Should item-dropping from players be blocked while participating in this event?                                                                           |
| `use_temporary_players`      | `true`                   | Should player inventories, effects, and attributes be backed up when a player joins, and then restored when they leave?                                   |


## Currently Implemented Event Types

Here's a list of every implemented event that can be used with `/event new ...`

| Event Type Id | Description                                                                                             |
|---------------|---------------------------------------------------------------------------------------------------------|
| `open_build`  | Creative building open to all. This can be set in a plot world of choice or even just in the overworld. |
| `debug`       | Literally pay no attention to this. It gives you cookies.                                               |
