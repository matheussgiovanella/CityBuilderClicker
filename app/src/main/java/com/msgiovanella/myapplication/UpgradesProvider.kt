package com.msgiovanella.myapplication

object UpgradesProvider {
    fun getUpgrades(actions: GameActions): List<Upgrade> {
        return listOf(
            Upgrade(
                id = 1,
                name = "Residência",
                baseCost = 50,
                onPurchase = { actions.addPopulation(20) },
                description = "Moradia de alta qualidade para seus habitantes",
                purchaseDescription = "Aumenta a população em 20 habitantes"
            ),
            Upgrade(
                id = 2,
                name = "Comércio",
                baseCost = 250,
                onTick = { count, _ -> actions.addMoney(5L * count) },
                description = "Lojas e mercados para seus habitantes trabalharem",
                tickDescription = "A cada segundo, aumenta o dinheiro em $5"
            ),
            Upgrade(
                id = 3,
                name = "Entretenimento",
                baseCost = 1200,
                onTick = { count, _ ->
                    actions.addMoney(3L * count)
                    actions.addHappiness(1 * count)
                },
                description = "Espaços de diversão e entretenimento para seus habitantes",
                tickDescription = "A cada segundo, aumenta o dinheiro em $3 e aumenta a felicidade em 1"
            ),
            Upgrade(
                id = 4,
                name = "Indústria",
                baseCost = 4500,
                onPurchase = { actions.changeMaxHappiness(-5) },
                onTick = { count, isAngry ->
                    val income = if (isAngry) 17L else 35L
                    actions.addMoney(income * count)
                    actions.addHappiness(-2 * count)
                },
                description = "Industrias pesadas para seus habitantes trabalharem, aumenta a poluição",
                purchaseDescription = "Devido a poluição, diminui a felicidade em 5",
                tickDescription = "A cada segundo, aumenta o dinheiro em $35 ($17 se a população estiver irritada), mas diminui a felicidade em 2"
            ),
            Upgrade(
                id = 5,
                name = "Parque",
                baseCost = 8000,
                onPurchase = { actions.changeMaxHappiness(5) },
                onTick = { count, _ -> actions.addHappiness(2 * count) },
                description = "Espaço verde, diminuindo a poluição e servindo como espaço de lazer",
                purchaseDescription = "Aumenta a felicidade em 5",
                tickDescription = "A cada segundo, aumenta a felicidade em 2"
            )
        )
    }
}