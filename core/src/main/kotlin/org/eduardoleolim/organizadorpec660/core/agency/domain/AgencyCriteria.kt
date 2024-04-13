package org.eduardoleolim.organizadorpec660.core.agency.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.AndFilters
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Orders
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.SingleFilter

enum class AgencyFields(val value: String) {
    Id("id"),
    Name("name"),
    Consecutive("consecutive"),
    MunicipalityId("municipality.id"),
    CreatedAt("createdAt"),
    UpdatedAt("updatedAt")
}


object AgencyCriteria {
    fun idCriteria(id: String) = Criteria(SingleFilter.equal(AgencyFields.Id.value, id), Orders.none(), 1, null)

    fun anotherConsecutive(consecutive: Int, municipalityOwnerId: String) = Criteria(
        AndFilters(
            listOf(
                SingleFilter.equal(AgencyFields.Consecutive.value, consecutive.toString()),
                SingleFilter.equal(AgencyFields.MunicipalityId.value, municipalityOwnerId)
            )
        ),
        Orders.none(),
        1,
        null
    )
}
