package org.eduardoleolim.organizadorpec660.core.agency.application

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response

class AgenciesResponse(
    val agencies: List<AgencyResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = agencies.size
}
