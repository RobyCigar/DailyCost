package com.dcns.dailycost.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dcns.dailycost.MainActivity
import com.dcns.dailycost.R
import com.dcns.dailycost.data.NavigationActions
import com.dcns.dailycost.theme.DailyCostTheme

@Preview(showBackground = true, showSystemUi = true,
    device = "spec:width=360dp,height=700dp,dpi=320"
)
@Composable
private fun OnboardingScreenContentPreview() {
    DailyCostTheme {
        OnboardingScreenContent(
            progress = { 0.5f },
            bodyText = stringResource(id = R.string.you_can_see_where_the_money_goes),
            titleText = stringResource(id = R.string.you_can_see_where_the_money_goes),
            primaryButtonText = stringResource(id = R.string.you_can_see_where_the_money_goes),
            secondaryButtonText = stringResource(id = R.string.you_can_see_where_the_money_goes),
            onPrimaryButtonClicked = {},
            onSecondaryButtonClicked = {}
        )
    }
}

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    navigationActions: NavigationActions
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    val progress by animateFloatAsState(
        label = "progress",
        targetValue = state.currentPage / state.pageCount.toFloat(),
        animationSpec = tween(256)
    )

    BackHandler {
        if (state.currentPage != 0) {
            viewModel.onAction(OnboardingAction.UpdateCurrentPage(state.currentPage - 1))

            return@BackHandler
        }

        (context as MainActivity).finishAndRemoveTask()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        OnboardingScreenContent(
            progress = { progress },
            bodyText = stringResource(id = state.bodyText),
            titleText = stringResource(id = state.titleText),
            primaryButtonText = stringResource(id = state.primaryButtonText),
            secondaryButtonText = stringResource(id = state.secondaryButtonText),
            onPrimaryButtonClicked = {
                if (state.currentPage == state.pageCount) {
                    // Sign in

                    return@OnboardingScreenContent
                }

                viewModel.onAction(OnboardingAction.UpdateCurrentPage(state.currentPage + 1))
            },
            onSecondaryButtonClicked = {
                if (state.currentPage == state.pageCount) {
                    // Sign up

                    return@OnboardingScreenContent
                }

                viewModel.onAction(OnboardingAction.UpdateCurrentPage(3))
            },
            modifier = Modifier
                .fillMaxSize(0.96f)
        )
    }
}

@Composable
private fun OnboardingScreenContent(
    progress: () -> Float,
    bodyText: String,
    titleText: String,
    primaryButtonText: String,
    secondaryButtonText: String,
    modifier: Modifier = Modifier,
    onPrimaryButtonClicked: () -> Unit,
    onSecondaryButtonClicked: () -> Unit
) {

    val constraintSet = ConstraintSet {
        val (
            progressIndicator,
            image,
            titleTextRef,
            bodyTextRef,
            nextSkipButton,
        ) = createRefsFor(
            "progressIndicator",
            "image",
            "titleText",
            "bodyText",
            "nextSkipButton",
        )

        constrain(progressIndicator) {
            centerHorizontallyTo(parent)

            top.linkTo(parent.top)
            bottom.linkTo(image.top)
        }

        constrain(image) {
            centerHorizontallyTo(parent)

            top.linkTo(progressIndicator.bottom)
            bottom.linkTo(titleTextRef.top)
        }

        constrain(titleTextRef) {
            centerHorizontallyTo(parent)

            top.linkTo(image.bottom)
            bottom.linkTo(bodyTextRef.top)
        }

        constrain(bodyTextRef) {
            centerHorizontallyTo(parent)

            top.linkTo(titleTextRef.bottom)
            bottom.linkTo(nextSkipButton.top)
        }

        constrain(nextSkipButton) {
            centerHorizontallyTo(parent)

            top.linkTo(bodyTextRef.bottom)
            bottom.linkTo(parent.bottom)
        }
    }

    ConstraintLayout(
        constraintSet = constraintSet,
        modifier = modifier
            .fillMaxSize()
    ) {
        LinearProgressIndicator(
            progress = progress(),
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth()
                .height(8.dp)
                .layoutId("progressIndicator")
        )

        Image(
            painter = ColorPainter(Color.LightGray),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .layoutId("image")
        )

        Text(
            text = titleText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .layoutId("titleText")
        )

        Text(
            text = bodyText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .layoutId("bodyText")
        )

        Column(
            modifier = Modifier
                .layoutId("nextSkipButton")
        ) {
            Button(
                onClick = onPrimaryButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(primaryButtonText)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = onSecondaryButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(secondaryButtonText)
            }
        }
    }
}
