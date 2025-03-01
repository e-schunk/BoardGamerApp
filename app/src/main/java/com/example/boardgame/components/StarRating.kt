package com.example.boardgame.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.boardgame.R

@Composable
fun StarRating(rating: Int, maxRating: Int = 5, onRatingChanged: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.StarRate else Icons.Outlined.StarRate,
                contentDescription = null,
                tint = colorResource(R.color.green),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(48.dp)
                    .clickable { onRatingChanged(if (index + 1 == rating) 0 else index + 1) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StarRatingPreview() {
    StarRating(rating = 3, onRatingChanged = {})
}