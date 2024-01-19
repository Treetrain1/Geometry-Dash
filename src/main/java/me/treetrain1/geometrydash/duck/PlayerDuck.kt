package me.treetrain1.geometrydash.duck

import me.treetrain1.geometrydash.data.GDData

@Suppress("FunctionName")
interface PlayerDuck {

    fun `geometryDash$getGDData`(): GDData
    fun `geometryDash$updateSyncedGDData`()
}
