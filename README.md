# Zombie Syndrome
Generally a simple zombie infection system.
## Features
- Zombie-like entities will try to infect players on their attack.
- If they succeeded, "Zombification" effect will appear on the player.
- When Zombification duration comes to zero, the player will die instantly and a zombie will appear on the death position.
- When you eat a curative item of zombification, the harmful effect will be removed and "Desinfection" effct will appear on you. During its duration, zombie-like entities will never successfully infect you.
- Curation comes at a price, you will get slowness and weakness effects on it.

## Configuration
### Main
- Zombification effect's minimum & maximum duration on zombie-like entities attack.
- The duration of weakness and slowness effects on zombification curation.
- The possibility percentage of zombie infection.
- Whether the corresponding zombie `copies your armors` / `is persistent` / `can pick up items from ground` on your zombification death.
- The curative item to prevent you from infection (by default golden apples, recommended for modpackers changing it to a customized KubeJS/CraftTweaker item)
- Unremovable effects by milk-like curation, default entry zombification but accepts any effects' registry names
- Infection sources list for mods who don't implement their zombie-like entities properly and infection cannot happen from them.
### Effects
- Whether to render Desinfection & Zombification's icon/ambient particles.
- Desinfection's duration.
- Zombificarion's duration is a random number generated from configurable maximum and minimum values.
