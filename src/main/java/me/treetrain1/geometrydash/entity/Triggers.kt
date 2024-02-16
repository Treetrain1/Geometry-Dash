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

// toggles a group of objects.
// btw this literally means this mod adds the fourth dimension to minecraft
open class ToggleTrigger(
    type: EntityType<out ToggleTrigger>,
    level: Level
) : Trigger(type, level)

// triggers a group of triggers at the same time
open class SpawnTrigger(
    type: EntityType<out SpawnTrigger>,
    level: Level
) : Trigger(type, level)

// rotates a group of objects
// how is this going to work for blocks???
open class RotationTrigger(
    type: EntityType<out RotationTrigger>,
    level: Level
) : Trigger(type, level)

// makes a group of objects follow the player
// dont know how to do this, maybe use falling blocks
open class FollowTrigger(
    type: EntityType<out FollowTrigger>,
    level: Level
) : Trigger(type, level)

// toggles a group of objects when the player is currently holding an input
open class TouchTrigger(
    type: EntityType<out TouchTrigger>,
    level: Level
) : Trigger(type, level)

// triggers actions when the player collects an amount of items
open class CountTrigger(
    type: EntityType<out CountTrigger>,
    level: Level
) : Trigger(type, level)

// idk
open class CollisionTrigger(
    type: EntityType<out CollisionTrigger>,
    level: Level
) : Trigger(type, level)

// enables or disables a group of objects on death
open class DeathTrigger(
    type: EntityType<out DeathTrigger>,
    level: Level
) : Trigger(type, level)

// screen shakes
open class ShakeTrigger(
    type: EntityType<out ShakeTrigger>,
    level: Level
) : Trigger(type, level)

open class EnableTrailTrigger(
    type: EntityType<out EnableTrailTrigger>,
    level: Level
) : Trigger(type, level)

open class DisableTrailTrigger(
    type: EntityType<out DisableTrailTrigger>,
    level: Level
) : Trigger(type, level)

// hides the player
open class HideTrigger(
    type: EntityType<out HideTrigger>,
    level: Level
) : Trigger(type, level)

// unhides the player
open class UnhideTrigger(
    type: EntityType<out UnhideTrigger>,
    level: Level
) : Trigger(type, level)

// enables player particles
open class EnableParticleTrigger(
    type: EntityType<out EnableParticleTrigger>,
    level: Level
) : Trigger(type, level)

// disables player particles
open class DisableParticleTrigger(
    type: EntityType<out DisableParticleTrigger>,
    level: Level
) : Trigger(type, level)

// sets camera zoom. just an fov change.
open class ZoomTrigger(
    type: EntityType<out ZoomTrigger>,
    level: Level
) : Trigger(type, level)

// fix the camera on an object group. Freed with exit static option.
open class StaticCameraTrigger(
    type: EntityType<out StaticCameraTrigger>,
    level: Level
) : Trigger(type, level)

// move the camera a certain amount
open class OffsetTrigger(
    type: EntityType<out OffsetTrigger>,
    level: Level
) : Trigger(type, level)

// changes distance between camera and player
open class PlayerOffsetTrigger(
    type: EntityType<out PlayerOffsetTrigger>,
    level: Level
) : Trigger(type, level)

// set camera rotation
open class CameraRotateTrigger(
    type: EntityType<out CameraRotateTrigger>,
    level: Level
) : Trigger(type, level)

// sets the camera edge to an object
open class EdgeTrigger(
    type: EntityType<out EdgeTrigger>,
    level: Level
) : Trigger(type, level)

// toggles free flying mode
open class CameraModeTrigger(
    type: EntityType<out CameraModeTrigger>,
    level: Level
) : Trigger(type, level)