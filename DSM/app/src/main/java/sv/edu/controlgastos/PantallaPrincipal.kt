package sv.edu.controlgastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// --- CHANGE 1: Update the function to accept ThemeViewModel ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(themeViewModel: ThemeViewModel) {
    // --- CHANGE 2: Get the dark mode state from the ViewModel ---
    val isDarkMode by themeViewModel.isDarkMode

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    // Your existing state variables remain unchanged
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var monto by remember { mutableStateOf(TextFieldValue("")) }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(TextFieldValue("")) }
    var listaGastos by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var filtroMes by remember { mutableStateOf("") }
    var filtroCategoria by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var mostrarHistorial by remember { mutableStateOf(false) }

    val categorias = listOf("Comida", "Transporte", "Servicios", "Entretenimiento", "Otros")

    // Your existing functions (cargarGastos, validarFecha, etc.) remain unchanged
    fun cargarGastos() {
        if (userId != null) {
            db.collection("gastos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    listaGastos = result.documents.mapNotNull { it.data }
                }
        }
    }

    fun validarFecha(fechaTexto: String): Boolean {
        val regex = Regex("""^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\d{4}$""")
        if (!regex.matches(fechaTexto)) return false
        val partes = fechaTexto.split("/")
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()
        val calendario = Calendar.getInstance()
        val anioActual = calendario.get(Calendar.YEAR)
        if (anio > anioActual || mes < 1 || mes > 12 || dia < 1 || dia > 31) return false
        return true
    }

    fun agregarGasto() {
        errorMensaje = null
        val montoDouble = monto.text.toDoubleOrNull()
        when {
            nombre.text.isBlank() -> errorMensaje = "Por favor ingrese un nombre para el gasto."
            montoDouble == null -> errorMensaje = "Por favor ingrese un monto válido."
            montoDouble <= 0 -> errorMensaje = "El monto debe ser mayor que 0."
            categoria.isBlank() -> errorMensaje = "Seleccione una categoría."
            fecha.text.isBlank() -> errorMensaje = "Ingrese la fecha del gasto."
            !validarFecha(fecha.text) -> errorMensaje = "Ingrese una fecha válida (dd/mm/aaaa) y no mayor al año actual."
        }
        if (errorMensaje != null) return
        val gasto = hashMapOf("nombre" to nombre.text, "monto" to montoDouble, "categoria" to categoria, "fecha" to fecha.text, "userId" to userId)
        db.collection("gastos").add(gasto).addOnSuccessListener {
            nombre = TextFieldValue("")
            monto = TextFieldValue("")
            categoria = ""
            fecha = TextFieldValue("")
            cargarGastos()
        }.addOnFailureListener { errorMensaje = "Error al guardar el gasto. Intente nuevamente." }
    }

    fun calcularTotal(): Double {
        val filtrados = listaGastos.filter {
            (filtroMes.isBlank() || (it["fecha"] as String).contains(filtroMes, ignoreCase = true)) &&
                    (filtroCategoria.isBlank() || (it["categoria"] as String).equals(filtroCategoria, ignoreCase = true))
        }
        return filtrados.sumOf { (it["monto"] as? Double) ?: 0.0 }
    }

    LaunchedEffect(Unit) { cargarGastos() }

    // --- CHANGE 3: Wrap your entire screen content in a Scaffold ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Gastos") },
                actions = {
                    // --- CHANGE 4: Add the theme toggle button ---
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.Bedtime,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // --- CHANGE 5: Move your original Column inside a LazyColumn for scrolling ---
        // and apply the padding from the Scaffold.
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)) {

            // Your entire UI is placed inside the items of the LazyColumn
            item {
                Text("Registrar Gasto", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top=16.dp))

                if (errorMensaje != null) {
                    Text(
                        text = errorMensaje!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del gasto") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categorias.forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = {
                                categoria = opcion
                                expanded = false
                            })
                        }
                    }
                }

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (dd/mm/aaaa)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                Button(
                    onClick = { agregarGasto() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) { Text("Guardar Gasto") }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Filtrar gastos", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = filtroMes,
                    onValueChange = { filtroMes = it },
                    label = { Text("Filtrar por mes (ej: 10/2025)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                var expandedFiltro by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandedFiltro, onExpandedChange = { expandedFiltro = !expandedFiltro }) {
                    OutlinedTextField(
                        value = filtroCategoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por categoría") },
                        modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp)
                    )
                    ExposedDropdownMenu(expanded = expandedFiltro, onDismissRequest = { expandedFiltro = false }) {
                        (listOf("") + categorias).forEach { opcion -> // Corrected loop
                            DropdownMenuItem(text = { Text(if (opcion.isBlank()) "Todas" else opcion) }, onClick = {
                                filtroCategoria = opcion
                                expandedFiltro = false
                            })
                        }
                    }
                }

                Button(
                    onClick = {
                        filtroMes = ""
                        filtroCategoria = ""
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) { Text("Limpiar filtros") }

                val total = calcularTotal()
                Text(
                    "Total filtrado: $${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.titleMedium
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Button(
                    onClick = { mostrarHistorial = !mostrarHistorial },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) { Text(if (mostrarHistorial) "Ocultar historial" else "Ver historial") }
            }

            // The history list is now part of the same scrollable column
            if (mostrarHistorial) {
                item {
                    Text("Historial de gastos", style = MaterialTheme.typography.titleLarge)
                }
                items(listaGastos) { gasto ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Nombre: ${gasto["nombre"]}")
                            Text("Monto: $${gasto["monto"]}")
                            Text("Categoría: ${gasto["categoria"]}")
                            Text("Fecha: ${gasto["fecha"]}")
                        }
                    }
                }
            }
        }
    }
}


// --- CHANGE 6: Add a Preview that works with the new structure ---
@Preview(showBackground = true)
@Composable
fun GastosScreenPreview() {
    // For the preview, we create a dummy ViewModel instance
    // You might need to wrap this in your theme for the preview to look right
    // ControlgastosTheme { GastosScreen(themeViewModel = ThemeViewModel()) }
    GastosScreen(themeViewModel = ThemeViewModel())
}
