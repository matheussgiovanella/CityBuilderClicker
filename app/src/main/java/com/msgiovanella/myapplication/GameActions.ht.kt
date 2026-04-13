package com.msgiovanella.myapplication

interface GameActions {
    fun addPopulation(amount: Int)
    fun addMoney(amount: Long)
    fun addHappiness(amount: Int)
    fun changeMaxHappiness(amount: Int)
}