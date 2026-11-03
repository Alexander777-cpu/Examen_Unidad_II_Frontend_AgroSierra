package pe.upeu.agrosierra.presentation.labor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.upeu.agrosierra.domain.model.TipoLabor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioLaborScreen(
    viewModel: FormularioLaborViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onVolver()
        }
    }

    LaunchedEffect(uiState.errorGeneral) {
        uiState.errorGeneral?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.laborId == null) "Registrar Labor" else "Editar Labor")
                },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("← Cancelar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de TipoLabor
            ExposedDropdownMenuBox(
                expanded = menuExpanded,
                onExpandedChange = { menuExpanded = !menuExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.tipo.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Labor") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    TipoLabor.entries.forEach { tipoItem ->
                        DropdownMenuItem(
                            text = { Text(tipoItem.name) },
                            onClick = {
                                viewModel.onTipoChange(tipoItem)
                                menuExpanded = false
                            }
                        )
                    }
                }
            }

            // Campo Fecha
            OutlinedTextField(
                value = uiState.fecha,
                onValueChange = viewModel::onFechaChange,
                label = { Text("Fecha (YYYY-MM-DD)") },
                isError = uiState.erroresCampo.containsKey("fecha"),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
            uiState.erroresCampo["fecha"]?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Cantidad
            OutlinedTextField(
                value = uiState.cantidad,
                onValueChange = viewModel::onCantidadChange,
                label = { Text("Cantidad") },
                isError = uiState.erroresCampo.containsKey("cantidad"),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )
            uiState.erroresCampo["cantidad"]?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Responsable
            OutlinedTextField(
                value = uiState.responsable,
                onValueChange = viewModel::onResponsableChange,
                label = { Text("Responsable") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            // Campo Observación
            OutlinedTextField(
                value = uiState.observacion,
                onValueChange = viewModel::onObservacionChange,
                label = { Text("Observación") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.cargando
            ) {
                if (uiState.cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (uiState.laborId == null) "Registrar Labor" else "Guardar Cambios")
                }
            }
        }
    }
}
