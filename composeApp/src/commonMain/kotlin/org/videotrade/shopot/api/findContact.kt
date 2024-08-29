package org.videotrade.shopot.api

import org.videotrade.shopot.domain.model.ContactDTO

fun findContactByPhone(phone: String , contacts: List<ContactDTO>): ContactDTO? {
    return contacts.find { it.phone == phone }
}

fun normalizePhoneNumber(phone: String): String {
    return phone.replace(Regex("[^0-9]"), "")
}