package com.vectorr22.run.presentation.active_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.core.presentation.designsystem.RuniqueTheme
import com.plcoding.core.presentation.ui.formatted
import com.plcoding.core.presentation.ui.formattedToKm
import com.plcoding.core.presentation.ui.toFormattedPace
import com.plcoding.run.domain.RunData
import com.plcoding.run.presentation.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RunDataCard(
    modifier: Modifier = Modifier,
    runData: RunData,
    elapsedTime: Duration
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunDataItem(
            title = stringResource(id = R.string.duration),
            value = elapsedTime.formatted(),
            valueFontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RunDataItem(
                title = stringResource(id = R.string.distance),
                value = (runData.distanceMeters / 1000.0).formattedToKm(),
                modifier = Modifier
                    .defaultMinSize(minWidth = 75.dp)
            )
            RunDataItem(
                title = stringResource(id = R.string.pace),
                value = elapsedTime.toFormattedPace(
                    distanceKm = (runData.distanceMeters / 1000.0)
                ),
                modifier = Modifier
                    .defaultMinSize(minWidth = 75.dp)
            )
        }

    }

}

@Composable
private fun RunDataItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueFontSize: TextUnit = 16.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = valueFontSize,
            color = MaterialTheme.colorScheme.onSurface
        )

    }
}

@Preview
@Composable
private fun RunDataCardPreview() {
    RuniqueTheme {
        RunDataCard(
            runData = RunData(
                distanceMeters = 3502,
                pace = 3.minutes
            ), elapsedTime = 10.minutes
        )
    }
}