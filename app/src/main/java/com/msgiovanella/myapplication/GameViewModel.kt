package com.msgiovanella.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _money = MutableStateFlow(0L)
    val money: StateFlow<Long> = _money.asStateFlow()

    private val _happiness = MutableStateFlow(100)
    val happiness: StateFlow<Int> = _happiness.asStateFlow()

    private val _population = MutableStateFlow(100)
    val population: StateFlow<Int> = _population.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    var maxHappiness = 100
    val minHappy = 60
    val minNeutral = 30

    init {
        startGameLoops()
    }

    val availableUpgrades = listOf(
        Upgrade(
            id = 1, name = "Residência", baseCost = 50, iconResId = R.drawable.happy,
            onPurchase = { _population.value += 20 }
        ),
        Upgrade(
            id = 2, name = "Comércio", baseCost = 250, iconResId = R.drawable.neutral,
            onTick = { count, _ -> _money.value += (4L * count) }
        ),
        Upgrade(
            id = 3, name = "Entretenimento", baseCost = 1200, iconResId = R.drawable.happy,
            onTick = { count, _ ->
                _money.value += (5L * count)
                addHappiness(1 * count)
            }
        ),
        Upgrade(
            id = 4, name = "Indústria", baseCost = 4500, iconResId = R.drawable.angry,
            onPurchase = {
                maxHappiness -= 5
                if (_happiness.value > maxHappiness) _happiness.value = maxHappiness
            },
            onTick = { count, isAngry ->
                val income = if (isAngry) 17L else 35L

                _money.value += (income * count)
                addHappiness(-2 * count)
            }
        ),
        Upgrade(
            id = 5, name = "Parque", baseCost = 8000, iconResId = R.drawable.happy,
            onPurchase = {
                maxHappiness = (maxHappiness + 5).coerceAtMost(100)
            },
            onTick = { count, _ ->
                addHappiness(2 * count)
            }
        )
    )

    private fun startGameLoops() {
        viewModelScope.launch {
            while (isActive) {
                delay(2000)
                addHappiness(1)
            }
        }

        viewModelScope.launch {
            while (isActive) {
                delay(1000)

                val isAngry = _happiness.value < minNeutral

                availableUpgrades.forEach { upgrade ->
                    if (upgrade.count > 0) {
                        upgrade.onTick(upgrade.count, isAngry)
                    }
                }
            }
        }
    }

    private fun addHappiness(amount: Int) {
        if (_isGameOver.value) return

        val currentValue = _happiness.value
        val newValue = (currentValue + amount).coerceIn(0, maxHappiness)

        if (currentValue != newValue) {
            _happiness.value = newValue

            if (newValue == 0) {
                _isGameOver.value = true
            }
        }
    }

    fun collectTaxes() {
        if (_isGameOver.value) return

        _money.value += _population.value / 10
        addHappiness(-1)
    }

    fun getCurrentHappinessState(): String {
        return when {
            _happiness.value >= minHappy   -> "happy"
            _happiness.value >= minNeutral -> "neutral"
            else                           -> "angry"
        }
    }

    fun buyUpgrade(upgrade: Upgrade) {
        if (_isGameOver.value) return

        if (_money.value >= upgrade.currentCost) {
            _money.value -= upgrade.currentCost
            upgrade.count += 1

            upgrade.onPurchase()
        }
    }

    fun restartGame() {
        _money.value = 0L
        _happiness.value = 100
        _population.value = 100
        maxHappiness = 100

        availableUpgrades.forEach { it.count = 0 }

        _isGameOver.value = false
        startGameLoops()
    }
}