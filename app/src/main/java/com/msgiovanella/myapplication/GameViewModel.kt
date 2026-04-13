package com.msgiovanella.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel : ViewModel(), GameActions {
    private val _money = MutableStateFlow(0L)
    val money: StateFlow<Long> = _money.asStateFlow()

    private val _happiness = MutableStateFlow(100)
    val happiness: StateFlow<Int> = _happiness.asStateFlow()

    private val _population = MutableStateFlow(100)
    val population: StateFlow<Int> = _population.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _isGamePaused = MutableStateFlow(false)
    val isGamePaused: StateFlow<Boolean> = _isGamePaused.asStateFlow()

    var maxAllowedHappiness = 100
        private set

    var maxHappiness = 100
        private set

    private val minHappy = 60
    private val minNeutral = 30

    val availableUpgrades = UpgradesProvider.getUpgrades(this)

    init {
        startGameLoops()
    }

    private fun startGameLoops() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)

                if (!isGameStopped()) {
                    addHappiness(1)
                    val isAngry = _happiness.value < minNeutral

                    availableUpgrades.forEach { upgrade ->
                        if (upgrade.count > 0) {
                            upgrade.onTick(upgrade.count, isAngry)
                        }
                    }
                }
            }
        }
    }

    private fun isGameStopped(): Boolean {
        return _isGamePaused.value || _isGameOver.value
    }

    override fun addPopulation(amount: Int) {
        _population.value += amount
    }

    override fun addMoney(amount: Long) {
        _money.value += amount
    }

    override fun changeMaxHappiness(amount: Int) {
        maxHappiness = (maxHappiness + amount).coerceAtMost(maxAllowedHappiness)

        if (_happiness.value > maxHappiness) {
            _happiness.value = maxHappiness
        }
    }

    override fun addHappiness(amount: Int) {
        if (isGameStopped()) return

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
        if (isGameStopped()) return

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
        if (isGameStopped()) return

        if (_money.value >= upgrade.currentCost) {
            _money.value -= upgrade.currentCost
            upgrade.count += 1

            upgrade.onPurchase()
        }
    }

    fun pauseGame() {
        _isGamePaused.value = true
    }

    fun unpauseGame() {
        _isGamePaused.value = false
    }

    fun restartGame() {
        _money.value = 0L
        _happiness.value = 100
        _population.value = 100
        maxHappiness = 100

        availableUpgrades.forEach { it.count = 0 }

        _isGameOver.value = false
        _isGamePaused.value = false
        startGameLoops()
    }
}