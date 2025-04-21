package com.example.composepdp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.ui.Modifier
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.composepdp.state.UiState
import com.example.composepdp.ui.theme.ComposePdpTheme
import com.example.composepdp.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composepdp.viewmodel.model.DishItem

private const val DURATION_ANIMATION = 1000
private const val SHIMMER_OFFSET = 300f
private const val SKELETON_LINES_COUNT = 10

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePdpTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen(viewModel: MainViewModel = viewModel()) {
        val uiState by viewModel.uiState

        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(title = stringResource(id = R.string.app_bar_title))

            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    when {
                        initialState is UiState.Loading && targetState is UiState.Success ->
                            slideInVertically { it } + fadeIn() togetherWith fadeOut()

                        initialState is UiState.Success && targetState is UiState.Error ->
                            slideInHorizontally { -it } + fadeIn() togetherWith fadeOut()

                        initialState is UiState.Error && targetState is UiState.Loading ->
                            scaleIn() + fadeIn() togetherWith fadeOut()

                        else -> fadeIn() togetherWith fadeOut()
                    }
                }
            ) { state ->
                when (state) {
                    is UiState.Loading -> LoadingScreen()
                    is UiState.Success -> SuccessScreen(viewModel, state.dishes)
                    is UiState.Error -> ErrorScreen { viewModel.loadData() }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar(title: String) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.primary_color)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun SuccessScreen(viewModel: MainViewModel, items: List<DishItem>) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items,
                    key = { it.id }
                ) { item ->
                    ItemCard(
                        item = item,
                        onItemClick = { viewModel.toggleSelection(it) },
                        onItemLongClick = { viewModel.removeItem(it) }
                    )
                }
            }

            RefreshButton { viewModel.loadData() }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ItemCard(
        item: DishItem,
        onItemClick: (DishItem) -> Unit,
        onItemLongClick: (DishItem) -> Unit
    ) {
        val isSelected = item.isSelected

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium),
                    vertical = dimensionResource(id = R.dimen.padding_small)
                )
                .border(
                    dimensionResource(id = R.dimen.border_size),
                    if (isSelected.value) colorResource(id = R.color.selected_border_color) else Color.Transparent,
                    RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))
                )
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
                .background(if (isSelected.value) colorResource(id = R.color.selected_background_color) else colorResource(id = R.color.default_background_color))
                .combinedClickable(
                    onClick = { onItemClick(item) },
                    onLongClick = { onItemLongClick(item) }
                )
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(text = item.name, fontSize = dimensionResource(id = R.dimen.text_size_medium).value.sp, color = Color.White)
        }
    }

    @Composable
    fun LoadingScreen() {
        val skeletonWidths = remember {
            List(SKELETON_LINES_COUNT) {
                listOf(0.4f, 0.6f, 0.8f, 0.9f).random()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.Top
        ) {
            skeletonWidths.forEach { widthFraction ->
                SkeletonLine(widthFraction)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer)))
            }
        }
    }

    @Composable
    fun SkeletonLine(widthFraction: Float) {
        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .height(dimensionResource(id = R.dimen.skeleton_line_height))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
                .shimmerEffect()
        )
    }

    @Composable
    fun Modifier.shimmerEffect(): Modifier {
        val transition = rememberInfiniteTransition()
        val shimmerTranslate by transition.animateFloat(
            initialValue = -SHIMMER_OFFSET,
            targetValue = SHIMMER_OFFSET,
            animationSpec = infiniteRepeatable(
                animation = tween(DURATION_ANIMATION, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        return drawWithCache {
            val brush = Brush.linearGradient(
                colors = listOf(
                    Color.Gray.copy(alpha = 0.3f),
                    Color.LightGray.copy(alpha = 0.5f),
                    Color.Gray.copy(alpha = 0.3f)
                ),
                start = Offset(shimmerTranslate, 0f),
                end = Offset(shimmerTranslate + SHIMMER_OFFSET, 0f)
            )
            onDrawBehind {
                drawRect(brush = brush)
            }
        }
    }

    @Composable
    fun ErrorScreen(onRetry: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ErrorImage()
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
            RetryButton(onRetry)
        }
    }

    @Composable
    fun ErrorImage() {
        Image(
            painter = painterResource(id = R.drawable.error_image),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(id = R.dimen.error_image_size)),
            contentScale = ContentScale.Fit
        )
    }

    @Composable
    fun RetryButton(onRetry: () -> Unit) {
        Button(onClick = onRetry) {
            Text(text = stringResource(id = R.string.retry_button))
        }
    }

    @Composable
    fun RefreshButton(onRefresh: () -> Unit) {
        Button(
            onClick = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.padding_medium),
                    end = dimensionResource(id = R.dimen.padding_medium),
                    bottom = dimensionResource(id = R.dimen.padding_medium)
                )
        ) {
            Text(text = stringResource(id = R.string.refresh_button))
        }
    }
}