package org.eduardoleolim.organizadorpec660.federalEntity.application

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response

class FederalEntitiesResponse(
    val federalEntities: List<FederalEntityResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = federalEntities.size

    companion object {
        fun fromAggregate(federalEntities: List<FederalEntity>, total: Int, limit: Int?, offset: Int?) =
            FederalEntitiesResponse(federalEntities.map(FederalEntityResponse.Companion::fromAggregate), total, limit, offset)
    }

}
