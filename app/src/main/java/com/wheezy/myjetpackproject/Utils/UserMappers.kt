package com.wheezy.myjetpackproject.Utils

import com.wheezy.myjetpackproject.Data.Dto.UserResponseDto
import com.wheezy.myjetpackproject.Data.Model.User

fun UserResponseDto.toDomain(): User = User(
    id             = this.id,
    email          = this.email,
    name           = this.name,
    profilePicture = this.profilePicture
)