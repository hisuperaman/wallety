package com.hisuperaman.wallety.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.ui.components.ActionButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBar(
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_regular))
    ) {
        ActionButton(
            imageVector = Icons.Default.ArrowCircleDown,
            text = stringResource(R.string.deposit),
            onClick = { onDepositClick() },
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            imageVector = Icons.Default.ArrowCircleUp,
            text = stringResource(R.string.withdraw),
            onClick = { onWithdrawClick() },
            outline = true,
            modifier = Modifier.weight(1f)
        )
    }
}