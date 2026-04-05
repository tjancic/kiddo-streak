package com.kiddostreak.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme

@Stable
val CuratedEmojis = listOf(
    "\uD83D\uDD25", // fire
    "\uD83D\uDCDA", // books
    "\uD83C\uDFC3", // running
    "\uD83D\uDCA7", // droplet (water)
    "\uD83E\uDDD8", // meditation
    "\uD83C\uDFB5", // music
    "\uD83D\uDCBB", // laptop (code)
    "\uD83C\uDFE8", // guitar (practice)
    "\u2705",       // check mark
    "\uD83C\uDF1F", // star
    "\uD83D\uDCAA", // flexed biceps
    "\uD83E\uDD57", // salad (healthy eating)
    "\uD83D\uDCA4", // sleep
    "\uD83D\uDCDD", // memo (journaling)
    "\uD83C\uDFA8", // art palette
    "\uD83D\uDEB4", // biking
    "\uD83E\uDDF9", // broom (cleaning)
    "\uD83D\uDC36", // dog (pet care)
    "\uD83C\uDF31", // seedling (growth)
    "\uD83D\uDE4F", // praying/grateful
    "\uD83D\uDCF5", // no phone (screen-free)
    "\u2615",       // coffee/tea
    "\uD83E\uDDE0", // brain (learning)
    "\uD83D\uDCF8", // camera (photography)
    "\uD83C\uDFCA", // swimming
    "\uD83E\uDD4A", // boxing glove
    "\uD83D\uDCD6", // open book (reading)
    "\uD83C\uDF4E", // apple (nutrition)
    "\uD83C\uDFAF", // direct hit (goals)
    "\u270D\uFE0F", // writing hand
)

@Composable
fun EmojiPicker(
    selectedEmoji: String?,
    onEmojiSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    emojis: List<String> = CuratedEmojis,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(emojis, key = { it }) { emoji ->
            val isSelected = emoji == selectedEmoji
            val shape = RoundedCornerShape(12.dp)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(shape)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = shape,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .clickable { onEmojiSelected(emoji) }
                    .semantics {
                        role = Role.RadioButton
                        selected = isSelected
                        contentDescription = "Emoji $emoji"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmojiPickerPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        EmojiPicker(
            selectedEmoji = "\uD83D\uDD25",
            onEmojiSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmojiPickerNoneSelectedPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        EmojiPicker(
            selectedEmoji = null,
            onEmojiSelected = {},
        )
    }
}
