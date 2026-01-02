**Ender chests are overpowered.**  

Using shulker boxes, a single Ender Chest can hold : 64x27x27 = 46,656 items (or 729 unique unstackables).

For most casual players, that's almost all of a base's storage.
It makes item transportation infrastructure (chest boats, chest minecarts, animals...) practically useless.

This mod aims to rebalance Ender Chests to be better suited to only hold the essentials when adventuring.

## Features
- Prevents the use of skulkers boxes inside Ender Chests.
- Opening a chest requires an eye of ender. The Ender Chest stays opened for 10 minutes.
- Fully customizable, data driven config. Option to prevent Ender Chest use altogether (off by default)

---

### Configuring the Mod

Edit ```config/yafuce_nerfed_ender_chest.json``` to configure the mod.

<details>
<summary>Default config</summary>

```json
{
  "disableEchestAltogether": false,
  "costEchestOpening": true,
  "openerItem": "minecraft:ender_eye",
  "openerItemCount": 1,
  "timeTickFreeAccess": 12000,
  "blockedEchestItems": [
    "minecraft:shulker_box",
    "minecraft:white_shulker_box",
    "minecraft:orange_shulker_box",
    "minecraft:magenta_shulker_box",
    "minecraft:light_blue_shulker_box",
    "minecraft:yellow_shulker_box",
    "minecraft:lime_shulker_box",
    "minecraft:pink_shulker_box",
    "minecraft:gray_shulker_box",
    "minecraft:light_gray_shulker_box",
    "minecraft:cyan_shulker_box",
    "minecraft:purple_shulker_box",
    "minecraft:blue_shulker_box",
    "minecraft:brown_shulker_box",
    "minecraft:green_shulker_box",
    "minecraft:red_shulker_box",
    "minecraft:black_shulker_box"
  ]
}

```

</details>