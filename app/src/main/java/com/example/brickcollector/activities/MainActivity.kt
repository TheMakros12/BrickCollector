package com.example.brickcollector.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.brickcollector.R
import com.example.brickcollector.data.Usuario
import com.example.brickcollector.databinding.ActivityMainBinding
import com.example.brickcollector.fragments.BuscarLegosFragment
import com.example.brickcollector.fragments.MisLegosFragment
import com.example.brickcollector.fragments.PerfilFragment
import com.example.brickcollector.fragments.WebFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.util.Log
import com.example.brickcollector.database.LegoApplication
import com.example.brickcollector.api.RetrofitInstance

class MainActivity : AppCompatActivity(), NavigationWebListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuario = intent.getSerializableExtra("usuario") as? Usuario
            ?: Usuario("Desconocido", "", "")

        Toast.makeText(this, "Bienvenido ${usuario.nombre}!!!", Toast.LENGTH_SHORT).show()

        replaceTabFragment(BuscarLegosFragment())
        sincronizarCategorias()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_buscar -> {
                    replaceTabFragment(BuscarLegosFragment())
                    true
                }
                R.id.navigation_mis_legos -> {
                    replaceTabFragment(MisLegosFragment())
                    true
                }
                R.id.navigation_perfil -> {
                    replaceTabFragment(PerfilFragment.newInstance(usuario))
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceTabFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out,
                R.anim.slide_in_back,
                R.anim.slide_out_back
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun replaceDetailFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out,
                R.anim.slide_in_back,
                R.anim.slide_out_back
            )
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun abrirWebFragment(url: String) {
        val fragment = WebFragment.newInstance(url)
        replaceDetailFragment(fragment)
    }

    private fun sincronizarCategorias() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dao = LegoApplication.database.legoDao()
                if (dao.getThemesCount() == 0) {
                    val response = RetrofitInstance.api.getThemes(limit = 1000)
                    dao.insertThemes(response.results)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al sincronizar temas", e)
            }
        }
    }
}
