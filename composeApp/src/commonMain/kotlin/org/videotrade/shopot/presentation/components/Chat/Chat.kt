package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel


@Composable
fun Chat(
    viewModel: ChatViewModel, modifier: Modifier
) {
    val messagesState = viewModel.messages.collectAsState(initial = listOf()).value
    
    val listState = rememberLazyListState()

//    LaunchedEffect(messagesState.size) {
//        if (messagesState.isNotEmpty()) {
//            listState.animateScrollToItem(messagesState.lastIndex)
//        }
//    }
    
    LazyColumn(
        state = listState,
        reverseLayout = true, // Makes items start from the bottom
        modifier = modifier
    ) {
        itemsIndexed(messagesState) { index, message ->
            MessageBox(message)
        }
    }
    
    
}

@Composable
fun MessageBox(message: MessageItem) {
    Box(
//        contentAlignment = if (true) Alignment.CenterStart else Alignment.CenterEnd,
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        
        Surface(
            modifier = Modifier
                .wrapContentSize(),
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}


