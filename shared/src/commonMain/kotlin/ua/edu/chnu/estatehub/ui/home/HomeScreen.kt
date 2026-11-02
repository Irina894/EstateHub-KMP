package ua.edu.chnu.estatehub.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.ListingCategory
import ua.edu.chnu.estatehub.navigation.Route
import ua.edu.chnu.estatehub.resources.*
import ua.edu.chnu.estatehub.ui.settings.SettingsRoute
import ua.edu.chnu.estatehub.ui.settings.SettingsViewModel

/** Головний екран: нижня навігація (Пошук / Обрані / Налаштування). Вкладка зберігається у ViewModel. */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigate: (Route) -> Unit,
) {
    val state by homeViewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TabItem(
                    selected = state.tab == HomeTab.SEARCH,
                    label = stringResource(Res.string.tab_search),
                    icon = Res.drawable.ic_search,
                    selectedIcon = Res.drawable.ic_search,
                    onClick = { homeViewModel.onIntent(HomeIntent.SelectTab(HomeTab.SEARCH)) },
                )
                TabItem(
                    selected = state.tab == HomeTab.FAVORITES,
                    label = stringResource(Res.string.tab_favorites),
                    icon = Res.drawable.ic_favorite_border,
                    selectedIcon = Res.drawable.ic_favorite,
                    onClick = { homeViewModel.onIntent(HomeIntent.SelectTab(HomeTab.FAVORITES)) },
                )
                TabItem(
                    selected = state.tab == HomeTab.SETTINGS,
                    label = stringResource(Res.string.tab_settings),
                    icon = Res.drawable.ic_tune,
                    selectedIcon = Res.drawable.ic_tune,
                    onClick = { homeViewModel.onIntent(HomeIntent.SelectTab(HomeTab.SETTINGS)) },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state.tab) {
                HomeTab.SEARCH -> SearchTab(state = state, onIntent = homeViewModel::onIntent)
                HomeTab.FAVORITES -> FavoritesTab(state = state, onIntent = homeViewModel::onIntent)
                HomeTab.SETTINGS -> SettingsRoute(viewModel = settingsViewModel, onNavigate = onNavigate)
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TabItem(
    selected: Boolean,
    label: String,
    icon: DrawableResource,
    selectedIcon: DrawableResource,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(if (selected) selectedIcon else icon),
                contentDescription = null,
            )
        },
        label = { Text(label) },
    )
}

@Composable
private fun SearchTab(state: HomeState, onIntent: (HomeIntent) -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = stringResource(Res.string.home_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onIntent(HomeIntent.QueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(Res.string.home_search_hint)) },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
                    singleLine = true,
                    shape = CircleShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                )
                CategoryChips(selected = state.category, onSelect = { onIntent(HomeIntent.CategorySelected(it)) })
            }
        }

        if (state.listings.isEmpty()) {
            item {
                EmptyHint(stringResource(Res.string.home_empty))
            }
        }

        items(state.listings, key = { it.id }) { listing ->
            ListingCard(
                listing = listing,
                isFavorite = listing.id in state.favoriteIds,
                onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(listing.id)) },
            )
        }
    }
}

@Composable
private fun FavoritesTab(state: HomeState, onIntent: (HomeIntent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Text(
                text = stringResource(Res.string.tab_favorites),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        if (state.favorites.isEmpty()) {
            item { EmptyHint(stringResource(Res.string.favorites_empty)) }
        }
        items(state.favorites, key = { it.id }) { listing ->
            ListingCard(
                listing = listing,
                isFavorite = true,
                onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(listing.id)) },
            )
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
    )
}

@Composable
private fun CategoryChips(selected: ListingCategory?, onSelect: (ListingCategory?) -> Unit) {
    val entries = listOf<Pair<ListingCategory?, String>>(
        null to stringResource(Res.string.category_all),
        ListingCategory.APARTMENT to stringResource(Res.string.category_apartments),
        ListingCategory.HOUSE to stringResource(Res.string.category_houses),
        ListingCategory.LAND to stringResource(Res.string.category_land),
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(entries) { (category, label) ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun ListingCard(
    listing: Listing,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    Card(
        modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(184.dp)) {
            ListingArt(category = listing.category, seed = listing.id)
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), CircleShape),
            ) {
                Icon(
                    painter = painterResource(
                        if (isFavorite) Res.drawable.ic_favorite else Res.drawable.ic_favorite_border
                    ),
                    contentDescription = stringResource(Res.string.tab_favorites),
                    tint = if (isFavorite) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "$" + formatThousands(listing.priceUsd),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(Res.string.listing_per_sqm, "$" + formatThousands(listing.pricePerSqm)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = listing.address,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = listingMeta(listing),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun listingMeta(listing: Listing): String {
    val parts = mutableListOf<String>()
    listing.rooms?.let { parts += stringResource(Res.string.listing_rooms, it) }
    parts += stringResource(Res.string.listing_area, listing.areaM2)
    val floor = listing.floor
    val totalFloors = listing.totalFloors
    if (floor != null) {
        parts += stringResource(Res.string.listing_floor, floor)
    } else if (totalFloors != null) {
        parts += stringResource(Res.string.listing_floors, totalFloors)
    }
    listing.landAres?.let { parts += stringResource(Res.string.listing_land, it) }
    return parts.joinToString(" · ")
}

/** 85000 -> "85 000" (нерозривний пробіл). */
internal fun formatThousands(value: Int): String {
    val digits = value.toString()
    return buildString {
        digits.forEachIndexed { index, ch ->
            if (index > 0 && (digits.length - index) % 3 == 0) append(' ')
            append(ch)
        }
    }
}
