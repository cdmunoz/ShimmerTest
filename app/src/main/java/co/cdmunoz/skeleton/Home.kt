package co.cdmunoz.skeleton

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.cdmunoz.skeleton.ui.theme.Amber600
import co.cdmunoz.skeleton.ui.theme.Purple100
import co.cdmunoz.skeleton.ui.theme.Purple200
import co.cdmunoz.skeleton.ui.theme.SkeletonTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home() {

    var weatherLoadingAnimated by remember { mutableStateOf(false) }
    var weatherLoadingAlpha by remember { mutableStateOf(false) }

    // Simulates loading weather data.
    suspend fun loadWeather() {
        if (!weatherLoadingAnimated) {
            weatherLoadingAnimated = true
            delay(5000L)
            weatherLoadingAnimated = false
        }
    }

    suspend fun loadWeatherAlpha() {
        if (!weatherLoadingAlpha) {
            weatherLoadingAlpha = true
            delay(5000L)
            weatherLoadingAlpha = false
        }
    }

    // Load the weather at the initial composition.
    LaunchedEffect(Unit) {
        loadWeather()
    }

    LaunchedEffect(Unit) {
        loadWeatherAlpha()
    }

    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = Purple100

    Scaffold(
        backgroundColor = backgroundColor
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
        ) {
            item { Header(title = stringResource(R.string.weather_animated)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    elevation = 2.dp
                ) {
                    if (weatherLoadingAnimated) {
                        ShimmerTransitionAnimRow(elementHeight = 100.dp)
                    } else {
                        WeatherRow(onRefresh = {
                            coroutineScope.launch {
                                loadWeather()
                            }
                        })
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item { Header(title = stringResource(R.string.weather_alpha)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    elevation = 2.dp
                ) {
                    if (weatherLoadingAlpha) {
                        ShimmerTransitionAlphaRow()
                    } else {
                        WeatherRow(onRefresh = {
                            coroutineScope.launch {
                                loadWeatherAlpha()
                            }
                        })
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun Header(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.h5.copy(color = Color.White)
    )
}

@Composable
private fun WeatherRow(
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Amber600)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = stringResource(R.string.temperature), fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.refresh)
            )
        }
    }
}

@Composable
private fun ShimmerTransitionAlphaRow() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color.LightGray.copy(alpha = alpha))
        )
    }
}

@Composable
fun ShimmerTransitionAnimRow(
    elementHeight: Dp,
    padding: Dp = 16.dp
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val elementWidthPx = with(LocalDensity.current) { (maxWidth - (padding * 2)).toPx() }
        val elementHeightPx = with(LocalDensity.current) { (elementHeight - padding).toPx() }
        val gradientWidth: Float = (0.2f * elementHeightPx)

        val infiniteTransition = rememberInfiniteTransition()
        val xElementShimmer = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (elementWidthPx + gradientWidth),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing,
                    delayMillis = 500
                ),
                repeatMode = RepeatMode.Restart
            )
        )
        val yElementShimmer = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (elementHeightPx + gradientWidth),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing,
                    delayMillis = 500
                ),
                repeatMode = RepeatMode.Restart
            )
        )

        val colors = listOf(
            Purple100.copy(alpha = .9f),
            Purple200.copy(alpha = .3f),
            Purple100.copy(alpha = .9f),
        )

        ShimmerElement(
            colors = colors,
            xShimmer = xElementShimmer.value,
            yShimmer = yElementShimmer.value,
            gradientWidth = gradientWidth,
        )
    }
}

@Preview
@Composable
private fun PreviewHome() {
    SkeletonTheme {
        Home()
    }
}