package com.hisuperaman.wallety.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toString
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.model.TransactionSummary
import com.hisuperaman.wallety.ui.components.AmountEntryModalSheet
import com.hisuperaman.wallety.ui.components.FilterBar
import com.hisuperaman.wallety.ui.components.MoneyText
import com.hisuperaman.wallety.ui.components.SelectMenu
import com.hisuperaman.wallety.ui.components.TransactionItem
import com.hisuperaman.wallety.ui.components.TransactionTypeFilter
import com.hisuperaman.wallety.ui.viewmodel.TransactionEvent
import com.hisuperaman.wallety.ui.viewmodel.TransactionState
import com.hisuperaman.wallety.ui.viewmodel.TransactionViewModel


@Composable
fun MoneySummaryCard(
    label: String,
    amount: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(paddingValues = PaddingValues(horizontal = 4.dp, vertical = 12.dp))
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            MoneyText(
                amount = amount,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun MoneySummaryBar(
    summary: TransactionSummary,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_regular))
    ) {
        MoneySummaryCard(
            label = "Day",
            amount = summary.day,
            modifier = Modifier.weight(1f)
        )
        MoneySummaryCard(
            label = "Week",
            amount = summary.week,
            modifier = Modifier.weight(1f)
        )
        MoneySummaryCard(
            label = "Month",
            amount = summary.month,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TransactionsScreen(
    transactionViewModel: TransactionViewModel
) {
    val transactionState by transactionViewModel.state.collectAsState()

    FilterBar(
        state = transactionState,
        onEvent = transactionViewModel::onEvent,
        modifier = Modifier.padding(top = 12.dp)
    )

    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item{
            MoneySummaryBar(
                summary = transactionState.summary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(
            items = transactionState.transactions,
            key = {it.id}
        ) {
            TransactionItem(
                transaction = it,
                onClick = {
                    transactionViewModel.onEvent(TransactionEvent.ShowDialog(it.type, transaction = it))
                }
            )
        }
    }

    AmountEntryModalSheet(
        transactionState = transactionState,
        transactionOnEvent = transactionViewModel::onEvent
    )
}
