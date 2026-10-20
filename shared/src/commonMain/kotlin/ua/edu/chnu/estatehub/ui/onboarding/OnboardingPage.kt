package ua.edu.chnu.estatehub.ui.onboarding

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import ua.edu.chnu.estatehub.resources.Res
import ua.edu.chnu.estatehub.resources.ic_onboarding_chat
import ua.edu.chnu.estatehub.resources.ic_onboarding_event
import ua.edu.chnu.estatehub.resources.ic_onboarding_favorite
import ua.edu.chnu.estatehub.resources.ic_onboarding_search
import ua.edu.chnu.estatehub.resources.onboarding_desc_1
import ua.edu.chnu.estatehub.resources.onboarding_desc_2
import ua.edu.chnu.estatehub.resources.onboarding_desc_3
import ua.edu.chnu.estatehub.resources.onboarding_desc_4
import ua.edu.chnu.estatehub.resources.onboarding_title_1
import ua.edu.chnu.estatehub.resources.onboarding_title_2
import ua.edu.chnu.estatehub.resources.onboarding_title_3
import ua.edu.chnu.estatehub.resources.onboarding_title_4

data class OnboardingPage(
    val icon: DrawableResource,
    val title: StringResource,
    val description: StringResource,
)

val onboardingPages: List<OnboardingPage> = listOf(
    OnboardingPage(Res.drawable.ic_onboarding_search, Res.string.onboarding_title_1, Res.string.onboarding_desc_1),
    OnboardingPage(Res.drawable.ic_onboarding_favorite, Res.string.onboarding_title_2, Res.string.onboarding_desc_2),
    OnboardingPage(Res.drawable.ic_onboarding_event, Res.string.onboarding_title_3, Res.string.onboarding_desc_3),
    OnboardingPage(Res.drawable.ic_onboarding_chat, Res.string.onboarding_title_4, Res.string.onboarding_desc_4),
)