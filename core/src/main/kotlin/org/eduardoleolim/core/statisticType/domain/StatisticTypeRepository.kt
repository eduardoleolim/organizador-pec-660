package org.eduardoleolim.core.statisticType.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface StatisticTypeRepository {
    fun matching(criteria: Criteria): List<StatisticType>

    fun count(criteria: Criteria): Int

    fun save(statisticType: StatisticType)

    fun delete(statisticTypeId: StatisticTypeId)
}
