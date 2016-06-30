----------
Long term
----------
Interface design and refactoring


----
TODO
----
- Improve CLI and dummy view
- Add serialize/deserialize for all models for deepcopy/communicate between frontend and backend

-----------
In Progress
-----------
Design and implement effect card mechanics

- Battlecry
- Card draw effect
- Choose One
- Combo
- Copy effect
- Deal damage
- Deathrattle
- Destroy effect
- Discard effect
- Enrage
- Equip
- Generate effect
- Inspire
- Joust
- Mind control effect
- Overload
- Restore Health
- Return effect
- Secret
- Shuffle into deck
- Silence
- Spell damage
- Summon
- Take control
- Transform
- Triggered effect



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
Health will be health and health upper bound. That being said, buff health upper bound need to increase the health value as well.
Which will be considered as two actions.
05/10/2016 Get rid of all the abstract class.
05/19/2016 Use google.true for unit tests so they can be more readable I guess.
05/20/2016 Define target scopes. For example, fireball can target all(enemy minions, enemy hero, minions on your own side, hero on your own side)
06/01/2016 Everybody should has its own view for both CLI and UI(json). The proper board/game should be rendered with combinations of views.
06/10/2016 Add logger object
06/20/2016 Implement boolean type of card mechanics like: Charge/Divine shield/Elusive/Forgetful/Freeze/Frozen/Immune/Poison/Taunt/Windfury


---------------
Invalid and Why
---------------
Decide the way of defining a card. I don't think it's a good way to define a class(Card) in a dynamic way(loading raw stats from json) in Java. But on the other hand
I will go with the json/property file way to avoid tons of boilerplate code to repeatedly implementing the interfaces with same way

Create Hero/Spell/Minion/HeroPower stats in properties file.(yaml because of the complexity of a card stats and mechanism)

ActionManager that receive various of actions and trigger them conditionally, or trigger other conditional actions, like secrets(Let's implement it first)
