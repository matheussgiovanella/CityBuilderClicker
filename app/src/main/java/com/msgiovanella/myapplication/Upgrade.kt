package com.msgiovanella.myapplication

import kotlin.math.pow

data class Upgrade (
    val id: Int,
    val name: String,
    val baseCost: Long,
    var count: Int = 0,
    val onPurchase: () -> Unit = {},
    val onTick: (count: Int, isAngry: Boolean) -> Unit = { _, _ -> },
    val description: String = "",
    val purchaseDescription: String = "",
    val tickDescription: String = ""
) {
    val currentCost: Long
        get() = (baseCost * 1.05.pow(count.toDouble())).toLong()
}