package org.eduardoleolim.organizadorpec660.core.statisticType.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.InstrumentTypesOfStatisticTypes
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypes
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormStatisticTypeRepository(private val database: Database) : StatisticTypeRepository {
    private val statisticTypes = StatisticTypes("st")
    private val instrumentTypesOfStatisticTypes = InstrumentTypesOfStatisticTypes("st_it")

    override fun matching(criteria: Criteria): List<StatisticType> {
        TODO("Not yet implemented")
    }

    override fun count(criteria: Criteria): Int {
        TODO("Not yet implemented")
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

            database.delete(instrumentTypesOfStatisticTypes) {
                it.statisticTypeId eq statisticType.id().toString()
            }

            database.batchInsert(instrumentTypesOfStatisticTypes) {
                statisticType.instrumentTypeIds().forEach { instrumentTypeId ->
                    item {
                        set(it.statisticTypeId, statisticType.id().toString())
                        set(it.instrumentTypeId, instrumentTypeId.toString())
                    }
                }
            }
        }
    }

    override fun delete(statisticTypeId: String) {
        database.useTransaction {
            database.delete(instrumentTypesOfStatisticTypes) {
                it.statisticTypeId eq statisticTypeId
            }

            database.delete(statisticTypes) {
                it.id eq statisticTypeId
            }
        }
    }
}
