package org.eduardoleolim.organizadorpec660.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormFederalEntityRepository(private val database: Database) : FederalEntityRepository {
    private val federalEntities = FederalEntities("f")

    override fun matching(criteria: Criteria): List<FederalEntity> {
        return KtormFederalEntitiesCriteriaParser.select(database, federalEntities, criteria).map { rowSet ->
            federalEntities.createEntity(rowSet).let {
                FederalEntity.from(it.id, it.keyCode, it.name, it.createdAt.toDate(), it.updatedAt?.toDate())
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormFederalEntitiesCriteriaParser.count(database, federalEntities, criteria)
            .rowSet.let {
                it.next()
                it.getInt(1)
            }
    }


    override fun save(federalEntity: FederalEntity) {
        database.useTransaction {
            database.insertOrUpdate(federalEntities) {
                set(it.id, federalEntity.id().toString())
                set(it.keyCode, federalEntity.keyCode())
                set(it.name, federalEntity.name())
                set(it.createdAt, federalEntity.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.keyCode, federalEntity.keyCode())
                    set(it.name, federalEntity.name())
                    set(it.updatedAt, federalEntity.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }
        }
    }

    override fun delete(federalEntityId: String) {
        database.useTransaction {
            count(FederalEntityCriteria.idCriteria(federalEntityId)).let { count ->
                if (count == 0)
                    throw FederalEntityNotFoundError(federalEntityId)

                database.delete(federalEntities) {
                    it.id eq federalEntityId
                }
            }
        }
    }
}
