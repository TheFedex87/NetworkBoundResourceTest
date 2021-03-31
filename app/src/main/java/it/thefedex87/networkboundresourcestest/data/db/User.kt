package it.thefedex87.networkboundresourcestest.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Users")
data class User(
    val firstName: String,
    val lastName: String,
    @PrimaryKey val nickName: String
) : Parcelable