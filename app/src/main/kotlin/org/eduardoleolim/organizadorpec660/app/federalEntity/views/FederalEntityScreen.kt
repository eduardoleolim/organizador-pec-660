package org.eduardoleolim.organizadorpec660.app.federalEntity.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.eduardoleolim.organizadorpec660.app.federalEntity.model.FederalEntityScreenModel
import org.eduardoleolim.organizadorpec660.app.generated.resources.Res
import org.eduardoleolim.organizadorpec660.app.generated.resources.federal_entities
import org.eduardoleolim.organizadorpec660.app.shared.composables.reset
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.jetbrains.compose.resources.stringResource

class FederalEntityScreen(private val queryBus: QueryBus, private val commandBus: CommandBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FederalEntityScreenModel(queryBus, commandBus) }
        var showDeleteModal by remember { mutableStateOf(false) }
        var showFormModal by remember { mutableStateOf(false) }
        var showImportExportModal by remember { mutableStateOf(false) }
        var showImportModal by remember { mutableStateOf(false) }
        var showExportModal by remember { mutableStateOf(false) }
        var selectedFederalEntity by remember { mutableStateOf<FederalEntityResponse?>(null) }
        val pageSizes = remember { listOf(10, 25, 50, 100) }
        val state = rememberPaginatedDataTableState(pageSizes.first())
        var searchValue by remember { mutableStateOf("") }
        val resetScreen = remember {
            fun() {
                searchValue = ""
                state.reset(pageSizes.first())
                screenModel.searchFederalEntities(searchValue, null, state.pageSize, state.pageIndex * state.pageSize)
                showDeleteModal = false
                showFormModal = false
                showImportExportModal = false
                showImportModal = false
                showExportModal = false
                selectedFederalEntity = null
            }
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.federal_entities),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.weight(1.0f)
                )

                SmallFloatingActionButton(
                    onClick = {
                        showImportExportModal = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Filled.ImportExport,
                        contentDescription = "Import/Export federal entities"
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        selectedFederalEntity = null
                        showFormModal = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add federal entity"
                    )
                }
            }

            FederalEntitiesTable(
                modifier = Modifier.fillMaxSize(),
                value = searchValue,
                onValueChange = { searchValue = it },
                pageSizes = pageSizes,
                state = state,
                data = screenModel.federalEntities,
                onSearch = { search, pageIndex, pageSize, orderBy, isAscending ->
                    val orders = orderBy?.let {
                        val orderType = if (isAscending) "ASC" else "DESC"
                        arrayOf(hashMapOf("orderBy" to orderBy, "orderType" to orderType))
                    }

                    screenModel.searchFederalEntities(search, orders, pageSize, pageIndex * pageSize)
                },
                onDeleteRequest = { federalEntity ->
                    selectedFederalEntity = federalEntity
                    showDeleteModal = true
                },
                onEditRequest = { federalEntity ->
                    selectedFederalEntity = federalEntity
                    showFormModal = true
                }
            )

            when {
                showFormModal -> {
                    screenModel.resetFormModal()
                    FederalEntityFormModal(
                        screenModel = screenModel,
                        federalEntity = selectedFederalEntity,
                        onDismissRequest = { resetScreen() },
                        onSuccess = { resetScreen() }
                    )
                }

                showDeleteModal && selectedFederalEntity != null -> {
                    screenModel.resetDeleteModal()
                    FederalEntityDeleteModal(
                        screenModel = screenModel,
                        federalEntity = selectedFederalEntity!!,
                        onSuccess = { resetScreen() },
                        onDismissRequest = { resetScreen() }
                    )
                }

                showImportExportModal -> {
                    FederalEntityImportExportModal(
                        onImportClick = {
                            showImportExportModal = false
                            showImportModal = true
                        },
                        onExportClick = {
                            showImportExportModal = false
                            showExportModal = true
                        },
                        onDismissRequest = { resetScreen() }
                    )
                }

                showImportModal -> {
                    screenModel.resetImportModal()
                    FederalEntityImportModal(
                        screenModel = screenModel,
                        onSuccessImport = { resetScreen() },
                        onDismissRequest = { resetScreen() }
                    )
                }
            }
        }
    }
}
