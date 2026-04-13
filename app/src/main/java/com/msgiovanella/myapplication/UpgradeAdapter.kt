package com.msgiovanella.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.msgiovanella.myapplication.databinding.ItemUpgradeBinding
import androidx.core.graphics.toColorInt

class UpgradeAdapter(
    private val upgrades: List<Upgrade>,
    private val onBuyClick: (Upgrade) -> Unit,
    private val onInfoClick: (Upgrade) -> Unit
) : RecyclerView.Adapter<UpgradeAdapter.UpgradeViewHolder>() {

    var currentMoney: Long = 0L
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class UpgradeViewHolder(val binding: ItemUpgradeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(upgrade: Upgrade) {
            binding.tvUpgradeName.text = "${upgrade.name} (${upgrade.count}x)"
            binding.tvUpgradeCost.text = "Custo $${upgrade.currentCost}"

            binding.imgUpgradeIcon.drawable?.isFilterBitmap = false

            val canAfford = currentMoney >= upgrade.currentCost
            binding.btnBuy.isEnabled = canAfford

            if (canAfford) {
                binding.btnBuy.setBackgroundColor("#4CAF50".toColorInt())
                binding.btnBuy.setTextColor(Color.WHITE)
            } else {
                binding.btnBuy.setBackgroundColor("#888888".toColorInt())
                binding.btnBuy.setTextColor(Color.LTGRAY)
            }

            binding.imgUpgradeIcon.setOnClickListener {
                onInfoClick(upgrade)
            }

            binding.btnBuy.setOnClickListener {
                onBuyClick(upgrade)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpgradeViewHolder {
        val binding = ItemUpgradeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpgradeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpgradeViewHolder, position: Int) {
        holder.bind(upgrades[position])
    }

    override fun getItemCount(): Int = upgrades.size
}