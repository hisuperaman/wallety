package com.hisuperaman.wallety.ui.screens.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.ui.components.InputConfirmationDialog
import com.hisuperaman.wallety.ui.components.MoneyText
import com.hisuperaman.wallety.ui.theme.AdaptiveRed
import com.hisuperaman.wallety.ui.viewmodel.AccountEvent
import com.hisuperaman.wallety.ui.viewmodel.AccountState
import com.hisuperaman.wallety.ui.viewmodel.AccountViewModel
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceCard(
    state: AccountState,
    modifier: Modifier = Modifier
) {
    val profit = "-12.4%"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_md)),
        modifier = modifier
            .padding(paddingValues = PaddingValues(top = dimensionResource(R.dimen.padding_md), start = dimensionResource(R.dimen.padding_regular), end = dimensionResource(R.dimen.padding_regular)))
    ) {
        Box(
            modifier = Modifier
                .padding(
                    paddingValues = PaddingValues(
                        top = 64.dp,
                        start = dimensionResource(R.dimen.padding_md),
                        end = dimensionResource(R.dimen.padding_md),
                        bottom = 32.dp
                    )
                )
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
            ) {
                MoneyText(
                    amount = state.account?.balance ?: 0,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = AdaptiveRed)) {
                            append("$profit ")
                        }
                        append(stringResource(R.string.last_month))
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.W400
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_sm)).alpha(0f)
                )
            }
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_sm))
                    .size(64.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

