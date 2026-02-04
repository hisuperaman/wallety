package com.hisuperaman.wallety.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType
import com.hisuperaman.wallety.ui.components.ActionButton
import com.hisuperaman.wallety.ui.components.AmountEntryModalSheet
import com.hisuperaman.wallety.ui.components.TransactionItem
import com.hisuperaman.wallety.ui.screens.home.components.ActionBar
import com.hisuperaman.wallety.ui.screens.home.components.BalanceCard
import com.hisuperaman.wallety.ui.viewmodel.AccountViewModel
import com.hisuperaman.wallety.ui.viewmodel.TransactionEvent
import com.hisuperaman.wallety.ui.viewmodel.TransactionViewModel


@Composable
fun TransactionHistory(
    transactions: List<Transaction>,
    onEvent: (TransactionEvent) -> Unit,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_regular))
        ) {
            Text(
                text = stringResource(R.string.transactions),
                style = MaterialTheme.typography.titleLarge,
            )
            ActionButton(
                onClick = onSeeMoreClick,
                text = stringResource(R.string.see_more),
                outline = true
            )
        }

        Column {
            transactions.forEach { t ->
                TransactionItem(
                    transaction = t,
                    onClick = {
                        onEvent(TransactionEvent.ShowDialog(t.type, transaction = t))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    accountViewModel: AccountViewModel,
    transactionViewModel: TransactionViewModel,
    onSeeMoreClick: () -> Unit
) {
    val accountState by accountViewModel.state.collectAsState()
    val transactionState by transactionViewModel.state.collectAsState()

    BalanceCard(
        state = accountState,
    )
    ActionBar(
        onDepositClick = { transactionViewModel.onEvent(TransactionEvent.ShowDialog(TransactionType.INCOME)) },
        onWithdrawClick = { transactionViewModel.onEvent(TransactionEvent.ShowDialog(TransactionType.EXPENSE)) }
    )
    TransactionHistory(
        transactions = transactionState.latestTransactions,
        onEvent = transactionViewModel::onEvent,
        onSeeMoreClick = onSeeMoreClick
    )

    AmountEntryModalSheet(
        transactionState = transactionState,
        transactionOnEvent = transactionViewModel::onEvent
    )
}
