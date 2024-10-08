package org.eduardoleolim.organizadorpec660.shared.infrastructure.bus

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.infrastructure.bus.KtormAgencyCommandHandlers
import org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.bus.KtormFederalEntityCommandHandlers
import org.eduardoleolim.organizadorpec660.instrument.infrastructure.bus.KtormInstrumentCommandHandlers
import org.eduardoleolim.organizadorpec660.municipality.infrastructure.bus.KtormMunicipalityCommandHandlers
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.*
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.infrastructure.bus.KtormStatisticTypeCommandHandlers
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormCommandBus(database: Database, instrumentsPath: String) : CommandBus {
    private val context = KtormAppKoinContext(database, instrumentsPath)

    private val commandHandlers = HashMap<KClass<out Command<*, *>>, CommandHandler<*, *, out Command<*, *>>>().apply {
        putAll(KtormAgencyCommandHandlers(context).handlers)
        putAll(KtormFederalEntityCommandHandlers(context).handlers)
        putAll(KtormInstrumentCommandHandlers(context).handlers)
        putAll(KtormMunicipalityCommandHandlers(context).handlers)
        putAll(KtormStatisticTypeCommandHandlers(context).handlers)
    }

    override fun <L, R> dispatch(command: Command<L, R>): Either<L, R> {
        try {
            commandHandlers[command::class]?.let {
                @Suppress("UNCHECKED_CAST")
                it as CommandHandler<L, R, Command<L, R>>
                return it.handle(command)
            } ?: throw CommandNotRegisteredError(command::class)
        } catch (e: Exception) {
            throw CommandHandlerExecutionError(e)
        }
    }
}
