package org.eduardoleolim.organizadorpec660.core.federalEntity.domain

import java.util.*

sealed class FederalEntityError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidFederalEntityIdError(val id: String, override val cause: Throwable?) :
    FederalEntityError("The id <$id> is not a valid federal entity id", cause)

class InvalidFederalEntityKeyCodeError(val keyCode: String) :
    FederalEntityError("The key code <$keyCode> is not a valid federal entity key code")

class InvalidFederalEntityNameError(val name: String) :
    FederalEntityError("The name <$name> is not a valid federal entity name")

class InvalidFederalEntityUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    FederalEntityError("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class FederalEntityNotFoundError(val id: String) :
    FederalEntityError("The federal entity with id <$id> was not found")

class FederalEntityHasMunicipalitiesError : FederalEntityError("The federal entity has municipalities")

class FederalEntityAlreadyExistsError(val keyCode: String) :
    FederalEntityError("The federal entity with key code <$keyCode> already exists")
