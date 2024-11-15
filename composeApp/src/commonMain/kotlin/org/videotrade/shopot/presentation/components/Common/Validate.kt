package org.videotrade.shopot.presentation.components.Common

fun validateFirstName(
    name: String,
    nameValidate1: String,
    nameValidate2: String,
    nameValidate3: String
): String? {
    return when {
        name.isEmpty() -> nameValidate1
        !name.matches(Regex("^[a-zA-Zа-яА-Я]+$")) -> nameValidate2
        name.length > 20 -> nameValidate3
        else -> null
    }
}

fun validateLastName(
    name: String,
    lastnameValidate1: String,
    lastnameValidate2: String
): String? {
    return when {
        !name.matches(Regex("^[a-zA-Zа-яА-Я]+$")) -> lastnameValidate1
        name.length > 20 -> lastnameValidate2
        else -> null
    }
}

fun validateDescription(
    desc: String,
    descValidate1: String = "",
    descValidate2: String
): String? {
    return when {
//        !desc.matches(Regex("^[a-zA-Z0-9]+$")) -> descValidate1
        desc.length > 50 -> descValidate2
        else -> null
    }
}

fun validateNickname(
    nickname: String,
    nickValidate1: String,
    nickValidate2: String,
    nickValidate3: String,
    nickValidate4: String
): String? {
    return when {
        nickname.isEmpty() -> nickValidate1
        nickname.length < 6 -> nickValidate2
        nickname.length > 30 -> nickValidate3
        !nickname.matches(Regex("^[a-zA-Z0-9]+$")) -> nickValidate4
        else -> null
    }
}