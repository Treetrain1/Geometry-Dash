@file:Experimental

package me.treetrain1.geometrydash.entity

import me.treetrain1.geometrydash.util.Experimental
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

// TODO: implement basic trigger system
// TODO: add equivalent of group id
abstract class Trigger(
    type: EntityType<out Trigger>,
    level: Level
) : StaticEntity(type, level)

// stops platform wave damage
open class DamageTrigger(
    type: EntityType<out DamageTrigger>,
    level: Level
) : Trigger(type, level)

// locks input when the player is a cube
open class JumpTrigger(
    type: EntityType<out JumpTrigger>,
    level: Level
) : Trigger(type, level)

// stops dash rings
open class DashStopTrigger(
    type: EntityType<out DashStopTrigger>,
    level: Level
) : Trigger(type, level)

// suppresses overhead damage in cube, robot, and spider
open class HitTrigger(
    type: EntityType<out HitTrigger>,
    level: Level
) : Trigger(type, level)

// flips gravity
open class FlipTrigger(
    type: EntityType<out FlipTrigger>,
    level: Level
): Trigger(type, level)

// moves stuff
open class MoveTrigger(
    type: EntityType<out MoveTrigger>,
    level: Level
) : Trigger(type, level)

// stops triggers
open class StopTrigger(
    type: EntityType<out StopTrigger>,
    level: Level
) : Trigger(type, level)

open class ToggleTrigger(
    type: EntityType<out ToggleTrigger>,
    level: Level
) : Trigger(type, level)

open class TouchTrigger(
    type: EntityType<out TouchTrigger>,
    level: Level
) : Trigger(type, level)