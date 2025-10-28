import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BotonCargando(
    cargando: Boolean,
    textoNormal: String,
    textoCargando: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!cargando) {
        androidx.compose.material3.Button(
            onClick = onClick,
            enabled = true,
            modifier = modifier
        ) {
            Text(textoNormal)
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = textoCargando,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}