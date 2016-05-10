----------
Long term
----------
Interface design and refactoring


----
TODO
----
3. ActionManager that receive various of actions and trigger them conditionally, or trigger other conditional actions, like secrets
5. Design of cards mechanism

- Battlecry
- Card draw effect
- Charge
- Choose One
- Combo
- Copy effect
- Deal damage
- Deathrattle
- Destroy effect
- Discard effect
- Divine shield
- Elusive
- Enrage
- Equip
- Forgetful
- Freeze
- Generate effect
- Immune
- Inspire
- Joust
- Mind control effect
- Overload
- Poison
- Restore Health
- Return effect
- Secret
- Shuffle into deck
- Silence
- Spell damage
- Summon
- Taunt
- Take control
- Transform
- Triggered effect
- Windfury

7. Add logger object.

-----------
In Progress
-----------
Design of cards mechanism

- Stealth

----
Done
----
04/08/2016 Refactor abstract class Card as a super parent. Super constructor is annoying. And manaCrystal should be private final attribute.
04/08/2016 Replace attribute with Attribute Type instead of primitive type.
05/06/2016 Redo attribute. Health is a special one. attack and crystal mana cost is less special with buffs. Armor is simply a int.
Actually it's a little bit complicated than I thought. Buff is can be done in three ways:
I. Buff other minion when a minion with such effect is on the board
II. Buff exist only for one round(either your round or opponent round)
III. Permanent buff until this minion dies.
- Health will be health and health upper bound. That being said, buff health upper bound need to increase the health value as well.
Which will be considered as two actions.


---------------
Invalid and Why
---------------
Decide the way of defining a card. I don't think it's a good way to define a class(Card) in a dynamic way(loading raw stats from json) in Java. But on the other hand
I will go with the json/property file way to avoid tons of boilerplate code to repeatedly implementing the interfaces with same way

1.5. Get rid of all the abstract class.
4. Create Hero/Spell/Minion/HeroPower stats in properties file.(yaml because of the complexity of a card stats and mechanism)
