package org.eduardoleolim.organizadorpec660.municipality.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.create.CreateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.delete.DeleteMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.update.UpdateMunicipalityCommand
import org.eduardoleolim.organizadorpec660.municipality.data.EmptyMunicipalityDataException
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus


@OptIn(FlowPreview::class)
class MunicipalityScreenModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var screenState by mutableStateOf(MunicipalityScreenState())
        private set

    var searchParameters = MutableStateFlow(MunicipalitySearchParameters())
        private set

    var municipalities by mutableStateOf(MunicipalitiesResponse(emptyList(), 0, null, null))
        private set

    var federalEntities by mutableStateOf(emptyList<FederalEntityResponse>())
        private set

    var formState by mutableStateOf<MunicipalityFormState>(MunicipalityFormState.Idle)
        private set

    var deleteState by mutableStateOf<MunicipalityDeleteState>(MunicipalityDeleteState.Idle)
        private set

    init {
        screenModelScope.launch {
            searchParameters
                .debounce(500)
                .collectLatest {
                    searchMunicipalities(it)
                }
        }
    }

    fun showFormModal(municipality: MunicipalityResponse?) {
        screenState = screenState.copy(
            selectedMunicipality = municipality,
            showFormModal = true,
            showDeleteModal = false
        )
    }

    fun showDeleteModal(municipality: MunicipalityResponse) {
        screenState = screenState.copy(
            selectedMunicipality = municipality,
            showFormModal = false,
            showDeleteModal = true
        )
    }

    fun resetScreen() {
        screenState = MunicipalityScreenState()
        searchParameters.value = MunicipalitySearchParameters().also {
            searchMunicipalities(it)
        }
        searchAllFederalEntities()
    }

    fun resetForm() {
        formState = MunicipalityFormState.Idle
    }

    fun resetDeleteModal() {
        deleteState = MunicipalityDeleteState.Idle
    }

    fun searchMunicipalities(
        search: String = searchParameters.value.search,
        federalEntity: FederalEntityResponse? = searchParameters.value.federalEntity,
        orders: List<HashMap<String, String>> = searchParameters.value.orders,
        limit: Int? = searchParameters.value.limit,
        offset: Int? = searchParameters.value.offset
    ) {
        searchParameters.value = MunicipalitySearchParameters(search, federalEntity, orders, limit, offset)
    }

    private fun searchMunicipalities(parameters: MunicipalitySearchParameters) {
        val (search, federalEntity, orders, limit, offset) = parameters
        screenModelScope.launch(dispatcher) {
            try {
                val query =
                    SearchMunicipalitiesByTermQuery(federalEntity?.id, search, orders.toTypedArray(), limit, offset)
                municipalities = queryBus.ask(query)
            } catch (e: Exception) {
                municipalities = MunicipalitiesResponse(emptyList(), 0, null, null)
            }
        }
    }

    fun searchAllFederalEntities() {
        screenModelScope.launch(dispatcher) {
            try {
                val query = SearchFederalEntitiesByTermQuery()
                federalEntities = queryBus.ask<FederalEntitiesResponse>(query).federalEntities
            } catch (e: Exception) {
                federalEntities = emptyList()
            }
        }
    }

    fun createMunicipality(keyCode: String, name: String, federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            formState = MunicipalityFormState.InProgress
            delay(500)

            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                formState =
                    MunicipalityFormState.Error(
                        EmptyMunicipalityDataException(
                            isFederalEntityEmpty,
                            isKeyCodeEmpty,
                            isNameEmpty
                        )
                    )
                return@launch
            }

            try {
                commandBus.dispatch(CreateMunicipalityCommand(keyCode, name, federalEntityId!!)).fold(
                    ifRight = {
                        formState = MunicipalityFormState.SuccessCreate
                    },
                    ifLeft = {
                        formState = MunicipalityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = MunicipalityFormState.Error(e.cause!!)
            }
        }
    }

    fun editMunicipality(municipalityId: String, keyCode: String, name: String, federalEntityId: String?) {
        screenModelScope.launch(dispatcher) {
            formState = MunicipalityFormState.InProgress
            delay(500)

            val isFederalEntityEmpty = federalEntityId.isNullOrBlank()
            val isKeyCodeEmpty = keyCode.isEmpty()
            val isNameEmpty = name.isEmpty()

            if (isFederalEntityEmpty || isKeyCodeEmpty || isNameEmpty) {
                formState =
                    MunicipalityFormState.Error(
                        EmptyMunicipalityDataException(
                            isFederalEntityEmpty,
                            isKeyCodeEmpty,
                            isNameEmpty
                        )
                    )
                return@launch
            }

            try {
                commandBus.dispatch(UpdateMunicipalityCommand(municipalityId, keyCode, name, federalEntityId!!)).fold(
                    ifRight = {
                        formState = MunicipalityFormState.SuccessEdit
                    },
                    ifLeft = {
                        formState = MunicipalityFormState.Error(it)
                    }
                )
            } catch (e: Exception) {
                formState = MunicipalityFormState.Error(e.cause!!)
            }
        }
    }

    fun deleteMunicipality(municipalityId: String) {
        screenModelScope.launch(dispatcher) {
            deleteState = MunicipalityDeleteState.InProgress
            delay(500)

            try {
                commandBus.dispatch(DeleteMunicipalityCommand(municipalityId)).fold(
                    ifRight = {
                        deleteState = MunicipalityDeleteState.Success
                    },
                    ifLeft = {
                        deleteState = MunicipalityDeleteState.Error(it)
                    }
                )

            } catch (e: Exception) {
                deleteState = MunicipalityDeleteState.Error(e.cause!!)
            }
        }
    }
}
