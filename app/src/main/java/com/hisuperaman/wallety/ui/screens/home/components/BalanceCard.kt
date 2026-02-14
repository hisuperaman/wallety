package com.hisuperaman.wallety.ui.screens.home.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.getFormattedExpensePercent
import com.hisuperaman.wallety.ui.components.MoneyText
import com.hisuperaman.wallety.ui.theme.AdaptiveGreen
import com.hisuperaman.wallety.ui.theme.AdaptiveRed
import com.hisuperaman.wallety.ui.viewmodel.AccountState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceCard(
    state: AccountState,
    expensePercentChange: Double,
    modifier: Modifier = Modifier
) {
    val color = if (expensePercentChange > 0) AdaptiveRed else AdaptiveGreen

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
                        withStyle(style = SpanStyle(color = color)) {
                            append("${getFormattedExpensePercent(expensePercentChange)} ")
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

