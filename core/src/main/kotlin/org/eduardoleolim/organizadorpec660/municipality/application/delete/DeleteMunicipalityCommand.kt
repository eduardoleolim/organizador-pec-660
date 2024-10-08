package org.eduardoleolim.organizadorpec660.municipality.application.delete

import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class DeleteMunicipalityCommand(municipalityId: String) : Command<MunicipalityError, Unit> {
    private val municipalityId: String = municipalityId.trim()

    fun municipalityId(): String {
        return municipalityId
    }
}
