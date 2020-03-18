package dev.alpas.alpasdev.database.migrations

import dev.alpas.alpasdev.entities.Users
import dev.alpas.ozone.migration.Migration

class CreateUsersTable : Migration() {
    override var name = "2020_03_17_171758_create_users_table"
    
    override fun up() {
        createTable(Users)
    }
    
    override fun down() {
        dropTable(Users)
    }
}