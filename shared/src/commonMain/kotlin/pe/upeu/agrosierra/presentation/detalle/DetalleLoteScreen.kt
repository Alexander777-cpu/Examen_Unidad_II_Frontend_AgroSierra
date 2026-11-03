package pe.upeu.agrosierra.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.agrosierra.domain.model.Labor
import pe.upeu.agrosierra.domain.model.Lote
import pe.upeu.agrosierra.presentation.padron.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLoteScreen(
    viewModel: DetalleLoteViewModel,
    onVolver: () -> Unit,
    onRegistrarLabor: (Long) -> Unit,
    onEditarLabor: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val puedeCrearLabor = (uiState.sesion?.puede("LABOR_CREAR") == true) && (uiState.lote?.campaniaCerrada == false)
    val puedeCerrarCampania = (uiState.sesion?.puede("CAMPANIA_CERRAR") == true) && (uiState.lote?.campaniaCerrada == false)
    val puedeEditarLabor = (uiState.sesion?.puede("LABOR_EDITAR") == true) && (uiState.lote?.campaniaCerrada == false)
    val puedeEliminarLabor = (uiState.sesion?.puede("LABOR_ELIMINAR") == true) && (uiState.lote?.campaniaCerrada == false)

    LaunchedEffect(uiState.mensajeExito, uiState.error) {
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.lote?.nombre ?: "Detalle de Lote") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (puedeCerrarCampania) {
                        IconButton(onClick = viewModel::cerrarCampania) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Cerrar Campaña",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (puedeCrearLabor) {
                FloatingActionButton(
                    onClick = { uiState.lote?.let { onRegistrarLabor(it.id) } }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Registrar Labor"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.cargando && uiState.lote == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.lote != null -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LoteCabeceraCard(lote = uiState.lote!!)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Historial de Labores (${uiState.labores.size})",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        if (uiState.labores.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay labores registradas en este lote",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.labores) { labor ->
                                    LaborCard(
                                        labor = labor,
                                        puedeEditar = puedeEditarLabor,
                                        puedeEliminar = puedeEliminarLabor,
                                        onEditar = { onEditarLabor(labor.loteId, labor.id) },
                                        onEliminar = { viewModel.eliminarLabor(labor.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se pudo cargar la información del lote")
                    }
                }
            }
        }
    }
}

@Composable
fun LoteCabeceraCard(lote: Lote) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${lote.codigo} - ${lote.nombre}",
                    style = MaterialTheme.typography.titleLarge
                )
                StatusChip(campaniaCerrada = lote.campaniaCerrada)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Cultivo: ${lote.cultivo}", modifier = Modifier.weight(1f))
                Text("Valle: ${lote.valle}", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Área: ${lote.hectareas} ha", modifier = Modifier.weight(1f))
                Text("Dosis Máx: ${lote.dosisMaximaPorHectarea} /ha", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LaborCard(
    labor: Labor,
    puedeEditar: Boolean,
    puedeEliminar: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = labor.tipo.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = labor.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Cantidad: ${labor.cantidad}", style = MaterialTheme.typography.bodyMedium)

            if (labor.responsable.isNotBlank()) {
                Text("Responsable: ${labor.responsable}", style = MaterialTheme.typography.bodySmall)
            }

            if (labor.observacion.isNotBlank()) {
                Text("Obs: ${labor.observacion}", style = MaterialTheme.typography.bodySmall)
            }

            if (puedeEditar || puedeEliminar) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (puedeEditar) {
                        IconButton(onClick = onEditar) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Labor"
                            )
                        }
                    }
                    if (puedeEliminar) {
                        IconButton(onClick = onEliminar) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Labor",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
