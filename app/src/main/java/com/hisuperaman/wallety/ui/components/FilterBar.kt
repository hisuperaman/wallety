package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.ui.viewmodel.TransactionEvent
import com.hisuperaman.wallety.ui.viewmodel.TransactionState
import kotlin.collections.map


@Composable
fun MonthSelectMenu(
    state: TransactionState,
    onEvent: (TransactionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        1 to "January",
        2 to "February",
        3 to "March",
        4 to "April",
        5 to "May",
        6 to "June",
        7 to "July",
        8 to "August",
        9 to "September",
        10 to "October",
        11 to "November",
        12 to "December"
    )

    SelectMenu(
        options = options.map { it.second },
        selected = options.first {it.first==state.filterMonth}.second,
        onSelect = {selected ->
            val monthNumber = options.first {it.second==selected}.first
            onEvent(TransactionEvent.SetFilterMonth(monthNumber))
        },
        textStyle = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}

@Composable
fun YearSelectMenu(
    state: TransactionState,
    onEvent: (TransactionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    SelectMenu(
        options = (state.minYear..state.maxYear).map { it.toString() },
        selected = state.filterYear.toString(),
        onSelect = {selected ->
            onEvent(TransactionEvent.SetFilterYear(selected.toInt()))
        },
        textStyle = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}

@Composable
fun FilterBar(
    state: TransactionState,
    onEvent: (TransactionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .height(100.dp)
            .padding(horizontal = dimensionResource(R.dimen.padding_regular))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
                .weight(0.6f)
        ) {
            YearSelectMenu(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            )
            MonthSelectMenu(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            )
        }
        TransactionTypeFilter(
            selected = state.filterType,
            onSelect = {
                onEvent(TransactionEvent.SetFilterType(it))
            },
            modifier = Modifier
                .weight(0.4f)
        )
    }
}