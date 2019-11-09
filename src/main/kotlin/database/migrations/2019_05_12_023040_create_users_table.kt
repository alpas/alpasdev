package dev.alpas.alpasdev.database.migrations

import dev.alpas.alpasdev.entities.Users
import dev.alpas.ozone.migration.Migration

class CreateUsersTable : Migration() {
    override fun up() {
        createTable(Users) {
            addIndex(Users.email)
        }
    }

    override fun down() {
        dropTable(Users)
    }
}
