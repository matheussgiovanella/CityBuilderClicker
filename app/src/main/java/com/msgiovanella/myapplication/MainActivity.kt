package com.msgiovanella.myapplication

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.msgiovanella.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: GameViewModel by viewModels()

    private lateinit var upgradeAdapter: UpgradeAdapter

    private val icDrawablesByState = mapOf(
        "happy"   to R.drawable.happy,
        "neutral" to R.drawable.neutral,
        "angry"   to R.drawable.angry
    )

    private val pbDrawablesByState = mapOf(
        "happy"   to R.drawable.pb_felicidade_happy,
        "neutral" to R.drawable.pb_felicidade_neutral,
        "angry"   to R.drawable.pb_felicidade_angry
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupListeners()
        observeViewModel()
        setupRecyclerView()
    }

    private fun setupWindow() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val bgDrawable = ContextCompat.getDrawable(this, R.drawable.background_game)
        binding.imgBackground.setImageDrawable(bgDrawable)
    }

    private fun setupListeners() {
        binding.btnPrefeitura.setOnClickListener {
            viewModel.collectTaxes()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.money.collect { currentMoney ->
                        binding.tvDinheiro.text = "$currentMoney"

                        upgradeAdapter.currentMoney = currentMoney
                    }
                }

                launch {
                    viewModel.happiness.collect { currentHappiness ->
                        binding.tvFelicidade.text = "$currentHappiness"
                        binding.pbFelicidade.progress = currentHappiness
                        updateVisualStates()
                    }
                }

                launch {
                    viewModel.population.collect { currentPopulation ->
                        binding.tvPopulacao.text = "$currentPopulation"
                    }
                }

                launch {
                    viewModel.isGameOver.collect { isOver ->
                        if (isOver) {
                            showGameOverDialog()
                            binding.btnPrefeitura.isEnabled = false
                            binding.btnPrefeitura.alpha = 0.5f
                        } else {
                            binding.btnPrefeitura.isEnabled = true
                            binding.btnPrefeitura.alpha = 1.0f
                        }
                    }
                }
            }
        }
    }

    private fun updateVisualStates() {
        val state = viewModel.getCurrentHappinessState()

        icDrawablesByState[state]?.let { resId ->
            setPixelArtIcon(binding.icFelicidade, resId)
        }

        pbDrawablesByState[state]?.let { resId ->
            binding.pbFelicidade.progressDrawable = ContextCompat.getDrawable(this, resId)
        }
    }

    private fun setPixelArtIcon(imageView: ImageView, resId: Int) {
        val drawable = ContextCompat.getDrawable(this, resId) as? BitmapDrawable
        drawable?.isFilterBitmap = false
        imageView.setImageDrawable(drawable)
    }

    private fun setupRecyclerView() {
        upgradeAdapter = UpgradeAdapter(viewModel.availableUpgrades) { upgradeClicado ->
            viewModel.buyUpgrade(upgradeClicado)
        }

        binding.rvUpgrades.layoutManager = LinearLayoutManager(this)
        binding.rvUpgrades.adapter = upgradeAdapter
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Fim de Jogo!")
            .setMessage("A população se revoltou e você foi deposto do cargo de Prefeito.\n\nGostaria de tentar novamente?")
            .setCancelable(false)
            .setPositiveButton("Recomeçar Jogo") { dialog, _ ->
                restartGameUI()
                dialog.dismiss()
            }
            .show()
    }

    private fun restartGameUI() {
        viewModel.restartGame()

        upgradeAdapter.notifyDataSetChanged()
    }
}