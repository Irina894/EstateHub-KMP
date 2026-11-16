package ua.edu.chnu.estatehub.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.resources.*

internal fun photosOf(id: Long): List<DrawableResource> = when (id) {
    1L -> listOf(Res.drawable.listing_1, Res.drawable.listing_1_2, Res.drawable.listing_1_3)
    2L -> listOf(Res.drawable.listing_2, Res.drawable.listing_2_2, Res.drawable.listing_2_3)
    3L -> listOf(Res.drawable.listing_3, Res.drawable.listing_3_2, Res.drawable.listing_3_3)
    4L -> listOf(Res.drawable.listing_4, Res.drawable.listing_4_2, Res.drawable.listing_4_3)
    5L -> listOf(Res.drawable.listing_5, Res.drawable.listing_5_2, Res.drawable.listing_5_3)
    6L -> listOf(Res.drawable.listing_6, Res.drawable.listing_6_2, Res.drawable.listing_6_3)
    else -> emptyList()
}

/** Обкладинка для картки списку: перше фото або намальована ілюстрація. */
@Composable
fun ListingImage(listing: Listing, modifier: Modifier = Modifier.fillMaxSize()) {
    val photo = photosOf(listing.id).firstOrNull()
    if (photo != null) {
        Image(
            painter = painterResource(photo),
            contentDescription = listing.address,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        ListingArt(category = listing.category, seed = listing.id)
    }
}

/** Галерея для екрана деталей: свайп між фото, лічильник і крапки. */
@Composable
fun ListingGallery(listing: Listing, modifier: Modifier = Modifier.fillMaxSize()) {
    val photos = photosOf(listing.id)
    if (photos.size < 2) {
        ListingImage(listing, modifier)
        return
    }
    val pagerState = rememberPagerState(pageCount = { photos.size })
    Box(modifier = modifier) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            Image(
                painter = painterResource(photos[page]),
                contentDescription = listing.address,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${photos.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(photos.size) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == pagerState.currentPage) 9.dp else 7.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == pagerState.currentPage) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                )
            }
        }
    }
}
