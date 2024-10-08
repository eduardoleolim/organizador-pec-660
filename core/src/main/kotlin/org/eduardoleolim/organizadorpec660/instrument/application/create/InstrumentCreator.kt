package org.eduardoleolim.organizadorpec660.instrument.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository
import java.util.*

class InstrumentCreator(
    private val instrumentRepository: InstrumentRepository,
    private val agencyRepository: AgencyRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun create(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String,
        file: ByteArray
    ): Either<InstrumentError, UUID> {
        if (existsAgency(agencyId).not())
            return Either.Left(AgencyNotFoundError(agencyId))

        if (existsStatisticType(statisticTypeId).not())
            return Either.Left(StatisticTypeNotFoundError(statisticTypeId))

        if (existsMunicipality(municipalityId).not())
            return Either.Left(MunicipalityNotFoundError(municipalityId))

        val existsInstrument = existsInstrument(
            statisticYear,
            statisticMonth,
            agencyId,
            statisticTypeId,
            municipalityId
        )

        if (existsInstrument)
            return Either.Left(
                InstrumentAlreadyExistsError(
                    statisticYear,
                    statisticMonth,
                    agencyId,
                    statisticTypeId,
                    municipalityId
                )
            )

        val instrumentFile = InstrumentFile.create(file)
        val instrument = Instrument.create(
            statisticYear,
            statisticMonth,
            instrumentFile.id().toString(),
            agencyId,
            statisticTypeId,
            municipalityId
        )

        instrumentRepository.save(instrument, instrumentFile)
        return Either.Right(instrument.id())
    }

    private fun existsAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencyRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) =
        StatisticTypeCriteria.idCriteria(statisticTypeId).let {
            statisticTypeRepository.count(it) > 0
        }

    private fun existsMunicipality(municipalityId: String) =
        MunicipalityCriteria.idCriteria(municipalityId).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsInstrument(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = InstrumentCriteria.otherInstrumentCriteria(
        statisticYear,
        statisticMonth,
        agencyId,
        statisticTypeId,
        municipalityId
    ).let {
        instrumentRepository.count(it) > 0
    }
}
