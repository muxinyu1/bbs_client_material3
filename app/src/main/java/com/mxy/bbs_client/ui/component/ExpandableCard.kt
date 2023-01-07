package com.mxy.bbs_client.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    modifier: Modifier,
    header: @Composable RowScope.() -> Unit,
    foldedContent: @Composable ColumnScope.() -> Unit,
    arrowColor: Color
) {
    var expand by remember { mutableStateOf(false) } // Expand State
    val rotationState by animateFloatAsState(if (expand) 180f else 0f) // Rotation State
    var stroke by remember { mutableStateOf(1) } // Stroke State
    Card(
        modifier = modifier
            .animateContentSize( // Animation
                animationSpec = tween(
                    durationMillis = 400, // Animation Speed
                    easing = LinearOutSlowInEasing // Animation Type
                )
            ).clickable {
                expand = !expand
                stroke = if (expand) 2 else 1
            },
        shape = RoundedCornerShape(8.dp), // Shape
        border = BorderStroke(stroke.dp, Color.Black), // Stroke Width and Color
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Control the header Alignment over here.
            ) {
                header()
                IconButton(
                    modifier = Modifier
                        .rotate(rotationState),
                    onClick = {
                        expand = !expand
                        stroke = if (expand) 2 else 1
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        tint = arrowColor, // Icon Color
                        contentDescription = "Drop Down Arrow"
                    )
                }
            }
            if (expand) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    foldedContent()
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ExpandableCardPreview() {
    ExpandableCard(
        modifier = Modifier,
        header = { Text(text = "头部") },
        foldedContent = { Text(text = "一些文本") },
        arrowColor = Color.Black
    )
}