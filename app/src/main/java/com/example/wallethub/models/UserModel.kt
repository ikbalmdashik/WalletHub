package com.example.wallethub.models

data class UserModel(
    var birthDate: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var nidNumber: String? = null,
    var password: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var balance: Double = 0.0
)
