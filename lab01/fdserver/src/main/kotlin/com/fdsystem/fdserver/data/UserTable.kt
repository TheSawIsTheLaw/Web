package com.fdsystem.fdserver.data

import org.jetbrains.exposed.sql.Table

object UserTable: Table()
{
    data class UserDTO(val id: Int, val username: String, val password: String, val dbToken: String)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)

    val id = integer("id").autoIncrement()
    val username = varchar("username", 200).uniqueIndex()
    val password = varchar("password", 200)
    val dbToken = varchar("dbTocken", 200)
}