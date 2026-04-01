package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.FinancialHelpEntity
import eu.tutorials.data.datasource.entity.HelpEntity
import eu.tutorials.data.datasource.entity.MaterialHelpEntity
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.data.datasource.table.FinancialHelpTable
import eu.tutorials.data.datasource.table.MaterialHelpTable
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.repository.IHelpRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class HelpRepositoryImpl : RepositoryImpl<HelpEntity, Help>(HelpEntity), IHelpRepository {
    override suspend fun create(help: Help): Help = transaction {
        val helpEntity = HelpEntity.new {
            this.title = help.title
            this.description = help.description
            this.id_recipient = UserEntity.findById(help.idRecipient) ?: error("User not found")
            this.created_at = help.createdAt
            this.type = when (help) {
                is Help.Financial -> "FINANCIAL"
                is Help.Material -> "MATERIAL"
            }
        }

        when (help) {
            is Help.Financial -> {
                FinancialHelpEntity.new {
                    this.help = helpEntity
                    this.from = help.from
                    this.to = help.to
                    this.planned_amount = help.plannedAmount
                    this.collected = help.collected
                }
            }
            is Help.Material -> {
                MaterialHelpEntity.new {
                    this.help = helpEntity
                    this.category = help.category
                    this.region = help.region
                    this.area = help.area
                    this.city = help.city
                }
            }
        }

        helpEntity.entityToDomain()
    }

    override suspend fun update(help: Help): Boolean = transaction {
        val helpEntity = HelpEntity.findById(help.id!!)
        helpEntity?.title = help.title
        helpEntity?.description = help.description
        helpEntity?.updated_at = LocalDateTime.now()

        when (help) {
            is Help.Financial -> {
                val financial = FinancialHelpEntity.find {
                    FinancialHelpTable.id eq help.id
                }.firstOrNull()

                financial?.from = help.from
                financial?.to = help.to
                financial?.planned_amount = help.plannedAmount
                financial?.collected = help.collected
            }
            is Help.Material -> {
                val material = MaterialHelpEntity.find {
                    MaterialHelpTable.id eq help.id
                }.firstOrNull()

                material?.category = help.category
                material?.region = help.region
                material?.area = help.area
                material?.city = help.city
            }
        }
        true
    }

    override suspend fun delete(id: Int): Boolean = transaction {
        val helpEntity = HelpEntity.findById(id)
        if (helpEntity != null) {
            when (helpEntity.type) {
                "FINANCIAL" -> {
                    FinancialHelpEntity.find { FinancialHelpTable.id eq id }
                        .forEach { it.delete() }
                }
                "MATERIAL" -> {
                    MaterialHelpEntity.find { MaterialHelpTable.id eq id }
                        .forEach { it.delete() }
                }
            }

            helpEntity.delete()
            true
        } else {
            false
        }
    }
}