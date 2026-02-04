package com.hisuperaman.wallety.ui.screens.analytics

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.getDailyExpenses
import com.hisuperaman.wallety.data.getDaysInMonth
import com.hisuperaman.wallety.data.model.TransactionType
import com.hisuperaman.wallety.ui.components.FilterBar
import com.hisuperaman.wallety.ui.theme.SoftBlue
import com.hisuperaman.wallety.ui.theme.SoftGreen
import com.hisuperaman.wallety.ui.theme.SoftPeach
import com.hisuperaman.wallety.ui.viewmodel.TransactionViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import java.util.Calendar


@Composable
fun AnalyticsChart(
    heading: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (isVisible) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
        ) {
            Text(
                text = heading,
                style = MaterialTheme.typography.titleLarge,
            )
            content()
        }
    }
}


@Composable
fun AnalyticsScreen(
    transactionViewModel: TransactionViewModel
) {
    val transactionState by transactionViewModel.state.collectAsState()
    val calendar = Calendar.getInstance()
    val daysInMonth = getDaysInMonth(transactionState.filterYear, transactionState.filterMonth)
    val weeksInMonth = (daysInMonth / 7) + 1

    val weeklyIncomeExpense = (1..weeksInMonth).map { weekIndex ->
        val weekTransactions = transactionState.transactions.filter { tx ->
            calendar.timeInMillis = tx.date
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val weekOfMonth = ((dayOfMonth - 1) / 7) + 1
            weekOfMonth == weekIndex
        }

        val income = weekTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.toDouble() }

        val expense = weekTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.toDouble() }

        Bars(
            label = "Week $weekIndex",
            values = listOf(
                Bars.Data(
                    label = "Income",
                    value = income,
                    color = SolidColor(SoftGreen)
                ),
                Bars.Data(
                    label = "Expense",
                    value = expense,
                    color = SolidColor(SoftPeach)
                )
            )
        )
    }

    var categoryData by remember(transactionState.transactions) {
        mutableStateOf(
            transactionState.transactions
                .filter { it.type == TransactionType.EXPENSE } // or INCOME
                .groupBy { it.category }
                .map { (category, txs) ->
                    Pie(
                        label = category.label, // or category.title
                        data = txs.sumOf { it.amount.toDouble() },
                        color = category.color, // or pick a color map
                        selectedColor = category.color
                    )
                }
        )
    }

    val barCategoryData by remember(transactionState.transactions) {
        mutableStateOf(
            transactionState.transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .map { (category, txs) ->
                    Bars(
                        label = category.label,
                        values = listOf(
                            Bars.Data(
                                label = category.label,
                                value = txs.sumOf { it.amount }.toDouble(),
                                color = SolidColor(category.color)
                            )
                        )
                    )
                }
        )
    }

    val dailyExpensesData = getDailyExpenses(transactionState.transactions, transactionState.filterYear, transactionState.filterMonth)

    FilterBar(
        state = transactionState,
        onEvent = transactionViewModel::onEvent,
        modifier = Modifier.padding(top = 12.dp)
    )

    Column (
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp, start = dimensionResource(R.dimen.padding_regular), end = dimensionResource(R.dimen.padding_regular))
    ) {
        AnalyticsChart(heading = "Income vs Expenses", isVisible = transactionState.filterType == null && weeklyIncomeExpense.isNotEmpty()) {
            ColumnChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 22.dp),
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                labelHelperProperties = LabelHelperProperties(
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                data = weeklyIncomeExpense,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                    spacing = 3.dp,
                    thickness = 20.dp
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        AnalyticsChart(heading = "Top Spending Categories", isVisible = transactionState.filterType != TransactionType.INCOME && barCategoryData.isNotEmpty()) {
            RowChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 22.dp),
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                labelHelperProperties = LabelHelperProperties(
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                indicatorProperties = VerticalIndicatorProperties(
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                data = barCategoryData,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                    spacing = 3.dp,
                    thickness = 20.dp
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            )
        }
        AnalyticsChart(heading = "Spending by Category", isVisible = transactionState.filterType != TransactionType.INCOME && categoryData.isNotEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                PieChart(
                    modifier = Modifier.size(200.dp),
                    data = categoryData,
                    onPieClick = {
                        val pieIndex = categoryData.indexOf(it)
                        categoryData = categoryData.mapIndexed { mapIndex, pie ->
                            if (mapIndex == pieIndex) {
                                pie.copy(selected = !pie.selected) // toggle
                            } else {
                                pie.copy(selected = false)
                            }
                        }
                    },
                    selectedScale = 1.2f,
                    scaleAnimEnterSpec = spring<Float>(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    colorAnimEnterSpec = tween(300),
                    colorAnimExitSpec = tween(300),
                    scaleAnimExitSpec = tween(300),
                    spaceDegreeAnimExitSpec = tween(300),
                    style = Pie.Style.Fill
                )
            }
        }

        AnalyticsChart(heading = "Daily Expenses Trend", isVisible = transactionState.filterType != TransactionType.INCOME && dailyExpensesData.isNotEmpty()) {
            LineChart(
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                labelHelperProperties = LabelHelperProperties(
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 22.dp),
                data = remember(transactionState.transactions, transactionState.filterYear, transactionState.filterYear) {
                    listOf(
                        Line(
                            label = "Expenses",
                            values = dailyExpensesData,
                            color = SolidColor(Color(0xFF23af92)),
                            firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                            gradientAnimationDelay = 1000,
                            drawStyle = DrawStyle.Stroke(width = 2.dp),
                        )
                    )
                },
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
            )
        }


    }
}
