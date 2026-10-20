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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.resources.Res
import ua.edu.chnu.estatehub.resources.onboarding_get_started
import ua.edu.chnu.estatehub.resources.onboarding_next
import ua.edu.chnu.estatehub.resources.onboarding_skip
import kotlin.math.absoluteValue

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val log = remember { Logger.withTag("Onboarding") }
    val isLastPage = pagerState.currentPage == pages.lastIndex

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            log.i { "Page ${page + 1} of ${pages.size}" }
        }
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
                if (!isLastPage) {
                    TextButton(onClick = {
                        log.i { "Onboarding skipped on page ${pagerState.currentPage + 1}" }
                        onFinished()
                    }) {
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
                )
            }

            PagerIndicator(pagerState = pagerState)

            Button(
                onClick = {
                    if (isLastPage) {
                        log.i { "Onboarding completed" }
                        onFinished()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
            ) {
                Text(
                    text = stringResource(
                        if (isLastPage) Res.string.onboarding_get_started else Res.string.onboarding_next
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    pagerState: PagerState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
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
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = (-16).dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
            )
            Icon(
                painter = painterResource(page.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(112.dp),
            )
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 480.dp),
        )
    }
}

@Composable
private fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val active = MaterialTheme.colorScheme.primary
    val inactive = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = modifier
            .wrapContentHeight()
            .padding(vertical = 24.dp),
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