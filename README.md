# Big Golem (Fabric 1.20.4)

Commands (op level 2):
- `/biggolem <scale>`            iron golem spawn, AI off (hilta-dulta nahi)
- `/biggolem <scale> <true|false>`  noai true/false
- `/golemscale <targets> <scale>`   maujooda golems ka size badlo, e.g. `/golemscale @e[type=iron_golem,distance=..10] 3`

Scale 0.1 se 20 tak. Size NBT (`BigGolemScale`) me save hota hai.

## Build
Java 17 chahiye.
1. `gradle wrapper --gradle-version 8.5`  (ya Fabric example mod ka `gradlew` + `gradle/` folder yahan copy karo)
2. `./gradlew build`
3. Jar: `build/libs/biggolem-1.0.0.jar` -> mods folder (Fabric API ke saath)
