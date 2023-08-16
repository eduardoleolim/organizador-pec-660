package org.eduardoleolim.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.core.federalEntity.domain.*
import org.eduardoleolim.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.ZoneId
import java.util.*

class KtormFederalEntityRepository(private val database: Database) : FederalEntityRepository {
    private val federalEntities = FederalEntities("f")

    override fun matching(criteria: Criteria): List<FederalEntity> {
        return KtormFederalEntitiesCriteriaParser.select(database, federalEntities, criteria).map {
            federalEntities.createEntity(it)
        }.map {
            FederalEntity.from(
                it.id,
                it.keyCode,
                it.name,
                Date.from(it.createdAt.atZone(ZoneId.systemDefault()).toInstant()),
                it.updatedAt?.let { date ->
                    Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
                }
            )
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormFederalEntitiesCriteriaParser.count(database, federalEntities, criteria)
            .rowSet.apply {
                next()
            }.getInt(1)
    }


    override fun save(federalEntity: FederalEntity) {
        this.count(FederalEntityCriteria.idCriteria(federalEntity.id().toString())).let { count ->
            if (count > 0) {
                this.update(federalEntity)
            } else {
                this.insert(federalEntity)
            }
        }
    }

    private fun insert(federalEntity: FederalEntity) {
        database.insert(federalEntities) {
            set(it.id, federalEntity.id().toString())
            set(it.keyCode, federalEntity.keyCode())
            set(it.name, federalEntity.name())
            set(it.createdAt, federalEntity.createdAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            federalEntity.updatedAt()?.let { date ->
                set(it.updatedAt, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            }
        }
    }

    private fun update(federalEntity: FederalEntity) {
        database.update(federalEntities) {
            val updateAt = federalEntity.updatedAt() ?: Date()
            set(it.keyCode, federalEntity.keyCode())
            set(it.name, federalEntity.name())
            set(it.updatedAt, updateAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            where {
                it.id eq federalEntity.id().toString()
            }
        }
    }

    override fun delete(federalEntityId: FederalEntityId) {
        this.count(FederalEntityCriteria.idCriteria(federalEntityId.value.toString())).let { count ->
            if (count == 0)
                throw FederalEntityNotFound(federalEntityId.value.toString())

            database.delete(federalEntities) {
                it.id eq federalEntityId.toString()
            }
        }
    }
}
