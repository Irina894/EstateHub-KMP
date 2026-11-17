package ua.edu.chnu.estatehub.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.core.mvi.CollectEffects
import ua.edu.chnu.estatehub.navigation.Route
import ua.edu.chnu.estatehub.resources.Res
import ua.edu.chnu.estatehub.resources.onboarding_get_started
import ua.edu.chnu.estatehub.resources.onboarding_next
import ua.edu.chnu.estatehub.resources.onboarding_skip
import ua.edu.chnu.estatehub.ui.adaptive.AdaptiveInfo
import ua.edu.chnu.estatehub.ui.adaptive.rememberAdaptiveInfo
import kotlin.math.absoluteValue

/** Обгортка MVI: підписується на стан і ефекти ViewModel. */
@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    onNavigate: (Route) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is OnboardingEffect.Navigate -> onNavigate(effect.route)
        }
    }
    OnboardingScreen(state = state, onIntent = viewModel::onIntent)
}

/** Stateless-екран: лише малює state і передає intent-и. */
@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = onboardingPages
    val adaptive = rememberAdaptiveInfo()
    val pagerState = rememberPagerState(initialPage = state.page, pageCount = { pages.size })

    // State -> Pager: кнопка "Далі" змінює state.page, pager плавно прокручується
    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) pagerState.animateScrollToPage(state.page)
    }
    // Pager -> State: свайп користувача повідомляє ViewModel про нову сторінку
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { onIntent(OnboardingIntent.PageChanged(it)) }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!state.isLastPage) {
                    TextButton(onClick = { onIntent(OnboardingIntent.SkipClicked) }) {
                        Text(stringResource(Res.string.onboarding_skip))
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    pageIndex = page,
                    pagerState = pagerState,
                    adaptive = adaptive,
                )
            }

            PagerIndicator(pagerState = pagerState, compact = adaptive.isHeightCompact)

            Button(
                onClick = { onIntent(OnboardingIntent.NextClicked) },
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
            ) {
                Text(
                    text = stringResource(
                        if (state.isLastPage) Res.string.onboarding_get_started else Res.string.onboarding_next
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(Modifier.height(if (adaptive.isHeightCompact) 12.dp else 32.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    pagerState: PagerState,
    adaptive: AdaptiveInfo,
) {
    if (adaptive.useTwoPane) {
        // Широкий або низький екран: ілюстрація ліворуч, текст праворуч
        val size = if (adaptive.isHeightCompact) 180.dp else 280.dp
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                PageIllustration(page, pageIndex, pagerState, size)
            }
            Column(
                modifier = Modifier.weight(1f).widthIn(max = 480.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                PageTexts(page, TextAlign.Start)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            PageIllustration(page, pageIndex, pagerState, 240.dp)
            Spacer(Modifier.height(40.dp))
            PageTexts(page, TextAlign.Center)
        }
    }
}

@Composable
private fun PageIllustration(
    page: OnboardingPage,
    pageIndex: Int,
    pagerState: PagerState,
    size: Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                val offset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction)
                    .absoluteValue
                    .coerceIn(0f, 1f)
                alpha = lerp(start = 0.3f, stop = 1f, fraction = 1f - offset)
                val scale = lerp(start = 0.8f, stop = 1f, fraction = 1f - offset)
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = 16.dp)
                .size(size * 0.2f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 16.dp, y = (-16).dp)
                .size(size * 0.12f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
        )
        Icon(
            painter = painterResource(page.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(size * 0.47f),
        )
    }
}

@Composable
private fun PageTexts(page: OnboardingPage, align: TextAlign) {
    Text(
        text = stringResource(page.title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = align,
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = stringResource(page.description),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align,
        modifier = Modifier.widthIn(max = 480.dp),
    )
}

@Composable
private fun PagerIndicator(
    pagerState: PagerState,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val active = MaterialTheme.colorScheme.primary
    val inactive = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = modifier
            .wrapContentHeight()
            .padding(vertical = if (compact) 10.dp else 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pagerState.pageCount) { index ->
            val distance = ((pagerState.currentPage - index) + pagerState.currentPageOffsetFraction)
                .absoluteValue
                .coerceIn(0f, 1f)
            val selection = 1f - distance
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(lerp(start = 8f, stop = 28f, fraction = selection).dp)
                    .clip(CircleShape)
                    .background(lerpColor(inactive, active, selection)),
            )
        }
    }
}