---
navigation:
    title: Getting Started
    position: 1
    icon: 'rifts:rift_steel_ingot'
item_ids:
  - 'rifts:rift_steel_nugget'
  - 'rifts:rift_steel_ingot'
  - 'rifts:rift_steel_block'
  - 'rifts:displacer'

---

# Getting Started

## First Steps
To get started, you first need to get some Rift Steel Ingots. Initially these can be obtained from killing Rift Elementals which spawn when you throw a Displacer at an Iron Golem.

## Displacer
A Displacer is an item that can be thrown to pull blocks and entities from an alternative dimension to the current one. This will replace the current blocks and entities with these ones. The only craftable displacer you can make is the one for the Rift Dimension that when thrown will create Rift dimension stuff
<RecipeFor id="rifts:displacer" />

## Rift Elemental
A strong mob that spawns when you throw a Displacer at an Iron Golem. It is very strong and can be hard to kill, but it drops Rift Steel Ingots when killed.
<GameScene zoom={4} interactive={true}>
  <Entity id="rifts:rift_elemental" />
</GameScene>

## Rift Steel Armor
Rift Steel Armor will protect you when you enter the Rift Dimension. It does not need to be powered to protect you inside the Rift Dimension but will not offer any actual protection until the armor is charged. When powered will grant flight inside the rift dimension
<Row>
    <RecipeFor id="rifts:rift_steel_helmet" />
    <RecipeFor id="rifts:rift_steel_chestplate" />
    <RecipeFor id="rifts:rift_steel_leggings" />
    <RecipeFor id="rifts:rift_steel_boots" />
</Row>