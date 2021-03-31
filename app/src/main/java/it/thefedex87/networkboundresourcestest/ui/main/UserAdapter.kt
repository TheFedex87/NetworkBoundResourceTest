package it.thefedex87.networkboundresourcestest.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.thefedex87.networkboundresourcestest.data.db.User
import it.thefedex87.networkboundresourcestest.databinding.UserItemBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAdapter @Inject constructor() : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var users: List<User> = listOf()

    fun setUserList(users: List<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                textViewUser.text = "${user.firstName} - ${user.lastName} - ${user.nickName}"
            }
        }
    }
}