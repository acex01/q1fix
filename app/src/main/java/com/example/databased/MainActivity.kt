package com.example.databased

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.databased.ui.theme.DatabasedTheme

class MainActivity : ComponentActivity() {
    val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DatabasedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    fun MainContent() {
        val words = remember { mutableStateListOf<Word>() }
        wordViewModel.allWords.observe(this) { words.clear(); words.addAll(it) }
        var showCourses by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {
            AddWordSection { word -> words.add(word); wordViewModel.insert(word) }
            ToggleButton(showCourses) { showCourses = it }
            if (showCourses) CourseList(words)
        }
    }

    @Composable
    fun AddWordSection(onSubmit: (Word) -> Unit) {
        var wordName by remember { mutableStateOf("") }
        var year by remember { mutableStateOf("") }

        OutlinedTextField(
            value = wordName,
            onValueChange = { wordName = it },
            label = { Text("Course Code") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = year,
            onValueChange = { year = it },
            label = { Text("Course Year") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        SubmitButton(wordName) {
            onSubmit(Word(name = wordName, year=year))
            wordName = ""
        }
    }

    @Composable
    fun SubmitButton(wordName: String, onSubmit: (String) -> Unit) {
        Button(
            onClick = { onSubmit(wordName) },
            enabled = wordName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Course")
        }
    }

    @Composable
    fun ToggleButton(showCourses: Boolean, onToggle: (Boolean) -> Unit) {
        Button(
            onClick = { onToggle(!showCourses) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showCourses) "Hide All Courses" else "Show Added Courses")
        }
    }

    @Composable
    fun CourseList(words: List<Word>) {
        LazyColumn {
            items(words) { WordItem(it) }
            if (words.isEmpty()) {
                item { Text("No Courses available") }
            }
        }
    }

    @Composable
    fun WordItem(word: Word) {
        Card(modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
        ) {
            Text(
                text = "Course Code: ${word.name}",
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text= "Course Year: ${word.year}",
                modifier= Modifier.padding(16.dp)
            )
        }
    }
}
