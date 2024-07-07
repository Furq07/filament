# Decoration Behaviours

Example of all behaviour fields for decorations:
```json
{
  "id": "mynamespace:clown_horn",
  "vanillaItem": "minecraft:paper",
  "models": {
    "default": "mynamespace:custom/misc/clown_horn"
  },
  "behaviour": {
    "animation": {
      "model": "mynamespace:mymodel",
      "autoplay": "myAnimationName"
    },
    "container": {
      "name": "Example Container",
      "size": 9,
      "purge": false,
      "openAnimation": "openAnimation",
      "closeAnimation": "closeAnimation"
    },
    "lock": {
      "key": "minecraft:tripwire_hook",
      "consumeKey": false,
      "discard": false,
      "unlockAnimation": "unlockAnimation",
      "command": "say Unlocked"
    },
    "seat": [
      {
        "offset": [0.0, 0.0, 0.0],
        "direction": 0.0
      }
    ],
    "showcase": [
      {
        "offset": [0.0, 0.0, 0.0],
        "scale": [1.0, 1.0, 1.0],
        "rotation": [0.0, 0.0, 0.0, 1.0],
        "type": "item",
        "filterItems": null,
        "filterTags": null
      }
    ],
    "fuel": {
      "value": 10
    },
    "food": {
      "hunger": 2,
      "saturation": 1.0,
      "canAlwaysEat": true,
      "fastfood": true
    },
    "cosmetic": {
      "slot": "head",
      "model": "mynamespace:custom/models/clown_backpack_animated",
      "autoplay": "idle",
      "scale": [1.5, 1.5, 1.5],
      "translation": [0.0, 0.5, 0.0]
    }
  }
}
```

### `animation` behaviour

**Description**:
Defines animation behaviours for decorations.

- **Fields**:
    - `model`: The name of the animated model associated with this animation (if applicable).
    - `autoplay`: The name of the animation to autoplay (if specified).

### `container` behaviour

**Description**:
Defines container behaviours for decorations.

- **Fields**:
    - `name`: The name displayed in the container UI.
    - `size`: The size of the container, has to be 5 slots or a multiple of 9, up to 6 rows of 9 slots.
    - `purge`: Indicates whether the container's contents should be cleared when no player is viewing the inventory.
    - `openAnimation`: The name of the animation to play when the container is opened (if applicable).
    - `closeAnimation`: The name of the animation to play when the container is closed (if applicable).

### `lock` behaviour

**Description**:
Defines lock behaviours for decorations.

- **Fields**:
    - `key`: The identifier of the key required to unlock.
    - `consumeKey`: Determines whether the key should be consumed upon unlocking.
    - `discard`: Specifies whether the lock utility should be discarded after unlocking.
    - `unlockAnimation`: Name of the animation to play upon successful unlocking (if applicable).
    - `command`: Command to execute when the lock is successfully unlocked (if specified).

### `seat` behaviour

**Description**:
Defines seating behaviours for decorations.

- **Fields**:
    - `offset`: The player seating offset.
    - `direction`: The rotation direction of the seat.

### `showcase` behaviour

**Description**:
Defines showcase behaviours for decorations.

- **Fields**:
    - `offset`: Offset for positioning the showcased item.
    - `scale`: Scale of the showcased item.
    - `rotation`: Rotation of the showcased item.
    - `type`: Type to display, block for blocks (block display), item for items (item display), dynamic uses blocks if possible, otherwise item (block/item display).
    - `filterItems`: Items to allow.
    - `filterTags`: Items with given item tags to allow.

### `fuel` behaviour

**Description**:
Defines fuel behaviours for decorations, specifying their value used in furnaces and similar item-burning blocks.

- **Fields**:
    - `value`: The value associated with the fuel, determining burn duration.

### `food` behaviour

**Description**:
Defines food item behaviours for edible decorations.

- **Fields**:
    - `hunger`: The amount of hunger restored when consumed.
    - `saturation`: The saturation modifier provided by the food.
    - `canAlwaysEat`: Indicates whether the item can be eaten when the hunger bar is full.
    - `fastfood`: Boolean indicating whether the food item is considered fast food (eats faster than normal).

### `cosmetic` behaviour

**Description**:
Defines cosmetic behaviours for decorations, supporting both Blockbench models for chestplates and simple item models.

- **Fields**:
    - `slot`: The equipment slot for the cosmetic (head or chest).
    - `model`: Optional, the resource location of the animated blockbench or animated-java model for the cosmetic.
    - `autoplay`: Optional, the name of the animation to autoplay, which should be loopable.
    - `scale`: Scale of the chest cosmetic, defaulting to (1, 1, 1).
    - `translation`: Translation of the chest cosmetic, defaulting to (0, 0, 0).