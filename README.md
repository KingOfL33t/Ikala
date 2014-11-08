Ikala
=====
Due to the fact that the original engine project was the same thing as JMonkeyEngine,
this project has now become a video game server.

This is an open source game server for the game Knights Of Ikala written in Java.

#Game Concepts

##Hero Classes

###Tank
* Melee attacks
* High hit points
* High defence
* Low damage
* Used for holding aggro
* Primary power set: Defense
* Secondary power set: Melee

###Fighter
* Melee attacks
* Medium hit points
* Medium damage
* Does a decent amount of damage and supports the tank
* Primary power set: Melee
* Secondary power set: Defense

###Steath
* Melee attacks
* Low hit points
* High damage
* Focus on hiding, moves quickly and uses traps. Does better with single targets or assisting a tank
* Primary power set: Melee
* Secondary power set: Support

###Healer
* Ranged attacks
* Low hit points
* Heals and buffs others
* Fewer offensive powers, more useful when teamed up with damage dealing classes
* Primary power set: Buff/Debuff
* Secondary power set: Ranged

###Controller
* Ranged attacks
* Medium hit points
* Medium damage
* Focus on crowd control and area of effect attacks
* May have pets or minons to assist them
* Primary power set: Control
* Secondary power set: Buff/Debuff

###Blaster
* Ranged attacks
* Low hit points
* High damage
* Deals a lot of damage at range, does better when assisting a tank or with single targets
* Primary power set: Ranged
* Secondary power set: Support

##Power sets

* Melee
  * Blades
  * Super strength
  * Fire melee
  * Ice melee
  * Electric melee
  * Earth melee
* Ranged
  * Fire blast
  * Ice blast
  * Electric blast
  * Water blast
  * Guns
  * Magical blast
* Control
  * Fire control
  * Ice control
  * Water control
  * Earth control
  * Electricity control
  * Mind control
* Buff/Debuff
  * Empathy
  * Bubbles
  * Dark magic
  * Light magic
  * Alchemy
    * Allows for poisoning enemies and curing friends
  * Physics
* Support
  * Traps
    * Bombs
    * AoE slow/damage
    * Healing
  * Ninja
    * Allows hiding
    * Stealth attacks
    * Faster movement
  * Mental assault
  * Earth assault
  * Magical assault
  * Illusion
    * Confuse enemies
    * Hide yourself
    * Make yourself harder to hit
* Defense
  * Earth armor
  * Willpower
  * Unholy armor
    * Can drain enemies
  * Holy armor
    * Can buff self and friends
  * Fiery armor
    * Can burn enemies
  * Nature armor
    * Protects you with plants
    * Protection can deal damage to attackers (for example, bees surround you)
