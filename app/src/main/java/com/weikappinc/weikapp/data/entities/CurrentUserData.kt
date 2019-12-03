package com.weikappinc.weikapp.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class CurrentUserData(
    @PrimaryKey
    var id: String,
    var id_user: String,
    val email: String,
    val name: String
) {
    @Ignore
    constructor(id_user: String, email: String, name: String) : this ("1", id_user, email, name)
}