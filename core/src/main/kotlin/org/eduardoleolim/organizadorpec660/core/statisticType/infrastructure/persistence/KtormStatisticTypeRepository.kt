package org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypes
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormStatisticTypeRepository(private val database: Database) : StatisticTypeRepository {
    private val statisticTypes = StatisticTypes("st")

    override fun matching(criteria: Criteria): List<StatisticType> {
        return KtormStatisticTypeCriteriaParser.parse(database, statisticTypes, criteria).map { rowSet ->
            statisticTypes.createEntity(rowSet).let {
                StatisticType.from(
                    it.id,
                    it.keyCode,
                    it.name,
                    it.createdAt.toDate(),
                    it.updatedAt?.toDate()
                )
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        return KtormStatisticTypeCriteriaParser.parse(database, statisticTypes, criteria).totalRecordsInAllPages
    }

    override fun save(statisticType: StatisticType) {
        database.useTransaction {
            database.insertOrUpdate(statisticTypes) {
                set(statisticTypes.id, statisticType.id().toString())
                set(statisticTypes.keyCode, statisticType.keyCode())
                set(statisticTypes.name, statisticType.name())
                set(statisticTypes.createdAt, statisticType.createdAt().toLocalDateTime())

                onConflict(statisticTypes.id) {
                    set(statisticTypes.keyCode, statisticType.keyCode())
                    set(statisticTypes.name, statisticType.name())
                    set(statisticTypes.updatedAt, statisticType.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }
        }
    }

    override fun delete(statisticTypeId: String) {
        database.useTransaction {
            database.delete(statisticTypes) {
                it.id eq statisticTypeId
            }
        }
    }
}
