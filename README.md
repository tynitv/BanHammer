# BanHammer (Ban Hammer & Mace Ban Engine) v1.2.0

<p align="center">
  <a href="https://papermc.io"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/supported/paper_vector.svg" alt="Available for Paper" height="52"></a>
  <a href="https://spigotmc.org"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/supported/spigot_vector.svg" alt="Available for Spigot" height="52"></a>
  <a href="https://purpurmc.org"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/cozy/supported/purpur_vector.svg" alt="Available for Purpur" height="52"></a>
</p>

BanHammer is the ultimate punishment and admin utility plugin for Paper 1.21.x and 1.21.11+ Minecraft servers. It turns the 1.21 Mace into an unstoppable weapon of justice with custom textures, celestial shockwaves, configurable sanction modes, and rich visual effects.

---

## Key Features

- 1.21 Mace Custom Model & Resource Pack: Custom texture (mace_banhammertexture.png) with automatic in-game resource pack delivery.
- Onde de Choc Céleste (Shockwave Ability): Right-Click to release a massive lightning shockwave pushing back surrounding entities with explosion particles and thunder.
- Flexible Sanction Modes (action-mode): Choose how the BanHammer punishes targets:
  - BAN: Permanent instant ban.
  - TEMP_BAN: Configurable temporary ban duration (e.g. 24 hours).
  - KICK: Immediate player expulsion.
  - LIGHTNING_ONLY: Visual and particle shock without punishment.
- Rich Effects & Potion Debuffs: Lightning strikes, explosion particles, Wither death audio, plus configurable debuffs (Blindness, Slowness, etc.).
- Adventure MiniMessage: Fully customizable gradient colors, prefixes, and broadcast messages.
- Live Reload & ResourcePack Commands: /banhammer reload to update configs on the fly without server restarts.

---

## Commands & Permissions

| Command | Description | Permission |
|---|---|---|
| `/banhammer give <player>` | Gives the legendary Ban Hammer to a player | `banhammer.give` |
| `/banhammer pack [player]` | Sends or re-sends the custom Resource Pack | `banhammer.use` |
| `/banhammer reload` | Reloads the configuration and Resource Pack server | `banhammer.admin` |

---

## Configuration Example (config.yml)

```yaml
# Action Mode: BAN, TEMP_BAN, KICK, MUTE, LIGHTNING_ONLY
action-mode: "BAN"
temp-ban-duration-hours: 24

# Shockwave Ability (Right-Click)
shockwave:
  enabled: true
  cooldown-seconds: 10
  radius: 6.0
  knockback-force: 1.8

# Automatic Resource Pack Settings
resource-pack:
  enabled: true
  auto-send-on-join: true
  port: 8765
  url: "https://raw.githubusercontent.com/tynitv/BanHammer/main/BanHammer_ResourcePack.zip"
```

---

## Installation

1. Download BanHammer-1.2.0.jar
2. Place it in your server's plugins/ directory
3. Restart or reload your server
4. Run /banhammer give <player> to get the hammer

---

## Links & Community

- Modrinth: https://modrinth.com/plugin/maceban
- GitHub: https://github.com/tynitv/BanHammer
- Discord: https://discord.gg/ex4236ZSh9
