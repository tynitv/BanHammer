# BanHammer (Ban Hammer & Mace Ban Engine)

Give your admins the ultimate banning power with a legendary mace!

BanHammer adds an enchanted, custom-textured Mace to your Minecraft server. When an admin hits a player or right-clicks for a celestial shockwave, it strikes with lightning, explosions, and instant punishment!

---

## Features

- Custom Mace Texture & Resource Pack: Custom model texture (mace_banhammertexture.png) with automatic in-game delivery.
- Shockwave Ability (Right-Click): Release a massive lightning shockwave pushing back nearby entities with explosion particles and thunder.
- Multiple Sanction Modes: Configure action on hit: BAN, TEMP_BAN, KICK, MUTE, or LIGHTNING_ONLY.
- Spectacular Visuals & Debuffs: Lightning strikes, visual explosions, Wither death audio, and configurable potion debuffs (Blindness, Slowness).
- Adventure MiniMessage Broadcasts: Fully customizable rich-text color formatting and broadcast messages.
- Live Reload & Commands: /banhammer reload to update configs and /banhammer pack to re-send the Resource Pack.

---

## Commands & Permissions

| Command | Description | Permission |
|---|---|---|
| `/banhammer give <player>` | Gives the Ban Hammer to the specified player | `banhammer.give` |
| `/banhammer pack [player]` | Sends the custom Resource Pack to a player | `banhammer.use` |
| `/banhammer reload` | Reloads plugin config and Resource Pack server | `banhammer.admin` |

---

## Configuration (config.yml)

```yaml
action-mode: "BAN"
temp-ban-duration-hours: 24

shockwave:
  enabled: true
  cooldown-seconds: 10
  radius: 6.0
  knockback-force: 1.8

resource-pack:
  enabled: true
  auto-send-on-join: true
  url: "https://raw.githubusercontent.com/tynitv/BanHammer/main/BanHammer_ResourcePack.zip"
```

---

## Installation & Compatibility

1. Place BanHammer-1.2.0.jar into your server's plugins/ folder
2. Restart your server
3. Compatible with Paper / Spigot 1.21.x & 1.21.11+ (Java 21+)
