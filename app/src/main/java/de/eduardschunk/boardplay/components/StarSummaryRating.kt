package de.eduardschunk.boardplay.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.eduardschunk.boardplay.R

@Composable
fun StarSummaryRating(rating: Double, maxRating: Int = 5) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(maxRating) { index ->
            when {
                rating >= index + 1 -> {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_rate_24),
                        contentDescription = null,
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(32.dp)
                    )
                }
                rating > index -> { // Halber Stern
                    Icon(
                        painter = painterResource(R.drawable.outline_star_rate_half_24),
                        contentDescription = null,
                        tint = colorResource(R.color.black),
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(32.dp)
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(R.drawable.outline_star_rate_24),
                        contentDescription = null,
                        tint = colorResource(R.color.black),
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StarSummaryRatingPreview() {
    StarSummaryRating(rating = 3.5)
}