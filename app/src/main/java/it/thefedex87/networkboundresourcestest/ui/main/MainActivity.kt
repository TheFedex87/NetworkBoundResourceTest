package it.thefedex87.networkboundresourcestest.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import it.thefedex87.networkboundresourcestest.R
import it.thefedex87.networkboundresourcestest.databinding.ActivityMainBinding
import it.thefedex87.networkboundresourcestest.util.Resource
import it.thefedex87.networkboundresourcestest.util.TAG
import it.thefedex87.networkboundresourcestest.util.onQueryTextChanged
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupUi()
    }

    private fun setupUi() {
        binding.apply {
            recyclerViewUsers.apply {
                adapter = this@MainActivity.adapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            editTextQuery.addTextChangedListener {
                mainViewModel.query.value = it.toString()
            }
        }
    }

    private fun setupObservers() {
        mainViewModel.mainShared.observe(this) {
            when(it) {
                is MainStateEvent.FetchUsersSuccess -> {
                    // Managed as State
                }
                is MainStateEvent.FetchUsersLoading -> {
                    // Managed as State
                }
                is MainStateEvent.FetchUsersError -> {
                    Toast.makeText(this, it.t.message, Toast.LENGTH_LONG).show()
                }
                is MainStateEvent.Empty -> {
                    // Do Nothing
                }
            }
        }

        mainViewModel.mainState.observe(this) {
            when(it) {
                is MainStateEvent.FetchUsersSuccess -> {
                    binding.recyclerViewUsers.isVisible = true
                    binding.progressBarLoadingUsers.isVisible = false
                }
                is MainStateEvent.FetchUsersLoading -> {
                    binding.recyclerViewUsers.isVisible = true
                    binding.progressBarLoadingUsers.isVisible = true
                }
                is MainStateEvent.FetchUsersError -> {
                    // Managed as Event
                }
                is MainStateEvent.Empty -> {
                    binding.recyclerViewUsers.isVisible = true
                    binding.progressBarLoadingUsers.isVisible = false
                }
            }
        }

        mainViewModel.users.observe(this) {
            adapter.setUserList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_refresh) {
            mainViewModel.updateUsers()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}