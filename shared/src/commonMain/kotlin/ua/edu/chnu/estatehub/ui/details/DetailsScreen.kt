package ua.edu.chnu.estatehub.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ua.edu.chnu.estatehub.domain.model.Listing
import ua.edu.chnu.estatehub.domain.model.Realtor
import ua.edu.chnu.estatehub.resources.*
import ua.edu.chnu.estatehub.ui.home.ListingArt
import ua.edu.chnu.estatehub.ui.home.formatThousands

@Composable
fun DetailsRoute(
    listingId: Long,
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(listingId) { viewModel.onIntent(DetailsIntent.Load(listingId)) }
    DetailsScreen(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@Composable
fun DetailsScreen(
    state: DetailsState,
    onIntent: (DetailsIntent) -> Unit,
    onBack: () -> Unit,
) {
    val listing = state.listing
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (listing == null) {
                if (state.loaded) {
                    Text(
                        text = stringResource(Res.string.details_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    )
                }
            } else {
                DetailsContent(listing)
            }

            // кнопки поверх зображення
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RoundIconButton(
                    icon = Res.drawable.ic_arrow_back,
                    description = stringResource(Res.string.details_back),
                    onClick = onBack,
                )
                if (listing != null) {
                    RoundIconButton(
                        icon = if (state.isFavorite) Res.drawable.ic_favorite else Res.drawable.ic_favorite_border,
                        description = stringResource(Res.string.tab_favorites),
                        tint = if (state.isFavorite) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { onIntent(DetailsIntent.ToggleFavorite) },
                    )
                }
            }

            val realtor = listing?.details?.realtor
            if (realtor != null) {
                ContactBar(
                    realtor = realtor,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: org.jetbrains.compose.resources.DrawableResource,
    description: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), CircleShape),
    ) {
        Icon(painter = painterResource(icon), contentDescription = description, tint = tint)
    }
}

@Composable
private fun DetailsContent(listing: Listing) {
    val d = listing.details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            ListingArt(category = listing.category, seed = listing.id)
        }

        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$" + formatThousands(listing.priceUsd),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = stringResource(Res.string.listing_per_sqm, "$" + formatThousands(listing.pricePerSqm)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    painter = painterResource(Res.drawable.ic_location),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = listing.address,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            StatsRow(listing)

            if (d.description.isNotBlank()) {
                SectionTitle(stringResource(Res.string.details_description))
                Card {
                    Text(
                        text = d.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            val features = buildFeatures(d)
            if (features.isNotEmpty()) {
                SectionTitle(stringResource(Res.string.details_features))
                Card {
                    Column {
                        features.forEachIndexed { index, (label, value) ->
                            if (index > 0) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            d.realtor?.let { realtor ->
                SectionTitle(stringResource(Res.string.details_realtor))
                Card {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = initials(realtor.name),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                        Spacer(Modifier.size(14.dp))
                        Column {
                            Text(realtor.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text(realtor.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // місце під нижню панель з кнопками
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun StatsRow(listing: Listing) {
    val stats = mutableListOf<Pair<String, String>>()
    listing.rooms?.let { stats += stringResource(Res.string.details_rooms) to it.toString() }
    stats += stringResource(Res.string.details_area) to stringResource(Res.string.listing_area, listing.areaM2)
    val floor = listing.floor
    val total = listing.totalFloors
    if (floor != null && total != null) {
        stats += stringResource(Res.string.details_floor) to stringResource(Res.string.details_floor_of, floor, total)
    } else if (total != null) {
        stats += stringResource(Res.string.details_floors) to total.toString()
    }
    listing.landAres?.let { stats += stringResource(Res.string.details_land) to stringResource(Res.string.listing_land, it) }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        stats.take(4).forEach { (label, value) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun buildFeatures(d: ua.edu.chnu.estatehub.domain.model.ListingDetails): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    if (d.buildingType.isNotBlank()) list += stringResource(Res.string.details_building_type) to d.buildingType
    if (d.heating.isNotBlank()) list += stringResource(Res.string.details_heating) to d.heating
    if (d.condition.isNotBlank()) list += stringResource(Res.string.details_condition) to d.condition
    d.yearBuilt?.let { list += stringResource(Res.string.details_year) to it.toString() }
    return list
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        content()
    }
}

@Composable
private fun ContactBar(realtor: Realtor, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val phone = realtor.phone.filter { it.isDigit() || it == '+' }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                // на пристроях без відповідного додатка openUri може кинути виняток
                onClick = { runCatching { uriHandler.openUri("sms:$phone") } },
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Icon(painterResource(Res.drawable.ic_chat), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text(stringResource(Res.string.details_write))
            }
            Button(
                onClick = { runCatching { uriHandler.openUri("tel:$phone") } },
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Icon(painterResource(Res.drawable.ic_call), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text(stringResource(Res.string.details_call))
            }
        }
    }
}

private fun initials(name: String): String =
    name.split(' ').filter { it.isNotBlank() }.take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
