package org.eduardoleolim.organizadorpec660.core.instrument.application.save

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class UpdateInstrumentAsNotSavedCommandHandler(private val instrumentSiresoSaver: InstrumentSiresoSaver) :
    CommandHandler<InstrumentError, Unit, UpdateInstrumentAsNotSavedCommand> {
    override fun handle(command: UpdateInstrumentAsNotSavedCommand): Either<InstrumentError, Unit> {
        return instrumentSiresoSaver.unsaveInSIRESO(command.instrumentId())
    }
}
