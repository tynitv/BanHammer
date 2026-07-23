# 🔨 BanHammer

**Give your admins the ultimate banning power — with a legendary hammer!**

BanHammer adds a **special enchanted Mace** to your Minecraft server. When an admin hits a player with this mythical weapon, that player is **instantly banned** — complete with explosive visual effects and a server-wide announcement!

---

## ✨ Features

- 🔨 **Unique item** — A custom unbreakable, glowing Mace named `BAN HAMMER`
- ⚡ **Instant ban** — Hitting a player with the hammer bans them immediately
- 💥 **Spectacular effects** — Lightning strike, visual explosion, and Wither death sound
- 📢 **Global broadcast** — A message is sent to all players on the server
- 🔒 **Permissions** — Access is protected to avoid abuse
- ⚙️ **Fully configurable** — Customize messages and effects in `config.yml`

---

## 📜 Commands & Permissions

| Command | Description | Permission |
|---|---|---|
| `/banhammer give <player>` | Gives the Ban Hammer to the specified player | `banhammer.give` |

---

## ⚙️ Configuration (`config.yml`)

```yaml
ban-message: "You have been banned by the Ban Hammer!"
broadcast-message: "&c{player} was annihilated by the Ban Hammer!"
lightning-effect: true
explosion-effect: true
sound-effect: true
```

---

## 📦 Installation

1. Download the `.jar` file
2. Place it in your server's `plugins/` folder
3. Restart your server
4. Use `/banhammer give <player>` to get the hammer!

---

## 🔧 Compatibility

- **Paper / Spigot** 1.21.x
- Java 21+

---

*Perfect for SMP servers, events, or keeping order in the most spectacular way possible!* ⚒️
