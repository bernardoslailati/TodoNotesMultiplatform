package presentation.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.MongoDb
import domain.RequestState
import domain.TaskAction
import domain.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<RequestState<List<ToDoTask>>>
typealias Tasks = MutableState<RequestState<List<ToDoTask>>>

class HomeViewModel(private val mongoDb: MongoDb): ScreenModel {
    private var _activeTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val activeTasks: Tasks = _activeTasks

    private var _completedTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val completedTasks: Tasks = _completedTasks

    init {
        _activeTasks.value = RequestState.Loading
        _completedTasks.value = RequestState.Loading

        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDb.readActiveTasks().collectLatest {
                _activeTasks.value = it
            }
        }
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDb.readCompletedTasks().collectLatest {
                _completedTasks.value = it
            }
        }
    }

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Delete -> deleteTask(action.task)
            is TaskAction.SetCompleted -> setCompletedTask(action.task, action.completed)
            is TaskAction.SetFavorite -> setFavoriteTask(action.task, action.favorite)
            else -> {}
        }
    }

    private fun deleteTask(task: ToDoTask) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDb.deleteTask(task)
        }
    }

    private fun setCompletedTask(task: ToDoTask, completed: Boolean) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDb.setCompleted(task, completed)
        }
    }

    private fun setFavoriteTask(task: ToDoTask, isFavorite: Boolean) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDb.setFavorite(task, isFavorite)
        }
    }

}