package com.zaviyanllc.calcworker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaviyanllc.calcworker.data.AiRepository
import com.zaviyanllc.calcworker.data.CalcRegistry
import com.zaviyanllc.calcworker.ui.components.Pressable
import com.zaviyanllc.calcworker.ui.theme.Brand
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScreen(ai: AiRepository, onOpenCalc: (String) -> Unit, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    data class ChatMsg(val role: String, val text: String, val offline: Boolean = false)
    val messages = remember { mutableStateListOf<ChatMsg>() }
    var input by remember { mutableStateOf("") }
    var thinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMsg("ai", ai.offlineAnswer("hello")))
        }
    }
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    fun send() {
        val q = input.trim()
        if (q.isEmpty() || thinking) return
        input = ""
        messages.add(ChatMsg("user", q))
        thinking = true
        scope.launch {
            val history = messages.filter { it.role != "sys" }.map { AiRepository.Msg(it.role, it.text) }
            val ans = ai.ask(q, history)
            thinking = false
            when (ans) {
                is AiRepository.Answer.Online -> messages.add(ChatMsg("ai", ans.text))
                is AiRepository.Answer.Offline -> messages.add(ChatMsg("ai", ans.text, offline = true))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CalcWorker AI") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    AssistChip(
                        onClick = {},
                        label = { Text("CalcWorker AI by Zaviyan") },
                        leadingIcon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
                items(messages) { m ->
                    val isUser = m.role == "user"
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                if (m.offline) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(14.dp), tint = Brand.Gold)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Offline answer", style = MaterialTheme.typography.labelSmall, color = Brand.Gold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                                Text(m.text, style = MaterialTheme.typography.bodyMedium)
                                // tappable calculator recommendations inside offline answers
                                if (!isUser) {
                                    CalcRegistry.matchCalculators(m.text, 3).takeIf { it.isNotEmpty() }?.let { recs ->
                                        Spacer(Modifier.height(8.dp))
                                        recs.forEach { e ->
                                            Pressable(onClick = { onOpenCalc(e.slug) }) {
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                                ) {
                                                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Filled.Calculate, contentDescription = null, tint = Brand.Sky, modifier = Modifier.size(18.dp))
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(e.title, style = MaterialTheme.typography.labelLarge, color = Brand.Sky)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (thinking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Thinking…", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask anything…") },
                    shape = RoundedCornerShape(28.dp),
                    singleLine = false,
                    maxLines = 4
                )
                FilledIconButton(
                    onClick = { send() },
                    modifier = Modifier.size(52.dp),
                    enabled = !thinking
                ) { Icon(Icons.Filled.Send, contentDescription = "Send") }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}
