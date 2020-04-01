package dev.alpas.alpasdev.entities

import dev.alpas.auth.BaseUser
import dev.alpas.auth.BaseUsersTable
import dev.alpas.ozone.OzoneEntity

// https://alpas.dev/docs/ozone#dao
interface User : BaseUser<User> {
    // https://alpas.dev/docs/email-verification
    override val mustVerifyEmail get() = true

    companion object : OzoneEntity.Of<User>()

    fun isAdmin(emails: List<String>): Boolean {

       return email in emails
    }
}

// https://alpas.dev/docs/ozone#dsl
object Users : BaseUsersTable<User>()