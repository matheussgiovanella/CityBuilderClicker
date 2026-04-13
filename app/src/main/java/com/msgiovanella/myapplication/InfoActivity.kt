package com.msgiovanella.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.msgiovanella.myapplication.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupListeners()
        changeContent()
    }

    private fun setupWindow() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val bgDrawable = ContextCompat.getDrawable(this, R.drawable.background_game)
        binding.imgBackground.setImageDrawable(bgDrawable)
    }

    private fun setupListeners() {
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun changeContent() {
        val bundle = intent.extras

        if (bundle != null) {
            val name = bundle.getString("name")
            val baseCost = bundle.getLong("base_cost")
            val description = bundle.getString("description")
            val purchaseDescription = bundle.getString("purchase_description")
            val tickDescription = bundle.getString("tick_description")

            binding.tvNome.text = name
            binding.tvCusto.text = "$baseCost"
            binding.tvDescricao.text = description
            binding.tvComprarDescricao.text = purchaseDescription
            binding.tvSegundoDescricao.text = tickDescription
        }
    }

    override fun onStart() {
        super.onStart()
        println("(InfoActivity) onStart")
    }

    override fun onResume() {
        super.onResume()
        println("(InfoActivity) onResume")
    }

    override fun onPause() {
        super.onPause()
        println("(InfoActivity) onPause")
    }

    override fun onStop() {
        super.onStop()
        println("(InfoActivity) onStop")
    }

    override fun onRestart() {
        super.onRestart()
        println("(InfoActivity) onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("(InfoActivity) onDestroy")
    }
}