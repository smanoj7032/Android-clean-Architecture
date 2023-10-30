package com.manoj.clean.ui.base

import android.os.Handler
import android.os.MessageQueue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manoj.data.util.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.android.HandlerDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel(
    dispatchers: DispatchersProvider
) : ViewModel() {

    private val io = dispatchers.getIO()

    /**
     * Use main if you need to perform a UI operation ASAP (as soon as possible) but not immediately; otherwise, use [mainImmediate].
     *
     * The Main dispatcher uses a [Handler] to post a [Runnable] to the [MessageQueue]. In other words, the operation or message
     * will be queued in the message queue and executed in the UI thread once the main looper dequeues it and reads it.
     *
     * Note: If the main dispatcher is called from the main thread, the operation will be performed immediately;
     * otherwise, it will be added to the queue as stated previously.
     *
     * Under the hood, coroutine checks If dispatch is required if [CoroutineDispatcher.isDispatchNeeded] is true it will call
     * the [CoroutineDispatcher.dispatch] method, which will result in posting a message to the handler queue. If dispatch isn't
     * required (if we're already in the UI thread or if we're using mainImmediate), it will immediately resume in the current thread.
     *
     * @see [MainCoroutineDispatcher], [HandlerDispatcher], [CoroutineDispatcher].
     * **/
    private val main = dispatchers.getMain()

    /**
     * Use mainImmediate if you need to perform an immediate operation in the UI; otherwise, use [main].
     *
     * The Main Immediate will return false when a coroutine calls [CoroutineDispatcher.isDispatchNeeded],
     * causing the coroutine to be resumed immediately in the current thread.
     *
     * @see [MainCoroutineDispatcher], [HandlerDispatcher], [CoroutineDispatcher].
     * **/
    private val mainImmediate = dispatchers.getMainImmediate()

    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        /** This function launches a background task using a coroutine on the IO (Input/Output) dispatcher.
        It's useful for tasks like network requests or disk I/O operations.
        It returns a Job, which allows you to control and monitor the task.*/
        return viewModelScope.launchOnIO(block)
    }

    protected fun launchOnMain(block: suspend CoroutineScope.() -> Unit): Job {
        /** This function launches a UI-related task using a coroutine on the Main dispatcher.
        It's for operations that directly affect the user interface, such as updating views.
        It returns a Job to manage and track the execution of the task.*/
        return viewModelScope.launchOnMain(block)
    }

    protected fun launchOnMainImmediate(block: suspend CoroutineScope.() -> Unit): Job {
        /** Similar to launchOnMain but with immediate (eager) execution.
        Use this when you need the task to run right away on the Main dispatcher.
        Returns a Job for task control and monitoring.*/
        return viewModelScope.launchOnMainImmediate(block)
    }

    protected fun CoroutineScope.launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        /**This function launches a background task using a coroutine on the IO dispatcher.
        It's helpful when you're working outside of a ViewModel and need to specify the dispatcher.
        Returns a Job to manage the task.*/
        return launch(io, block = block)
    }

    protected fun CoroutineScope.launchOnMain(block: suspend CoroutineScope.() -> Unit): Job {
        /**This function launches a UI-related task using a coroutine on the Main dispatcher.
        Useful for UI operations when you're not inside a ViewModel.
        Returns a Job for task control and monitoring.*/
        return launch(main, block = block)
    }

    protected fun CoroutineScope.launchOnMainImmediate(block: suspend CoroutineScope.() -> Unit): Job {
        /**Similar to launchOnMain but with immediate (eager) execution.
        Use this when you need the task to run immediately on the Main dispatcher.
        Returns a Job for controlling and monitoring the task.*/
        return launch(mainImmediate, block = block)
    }

    protected suspend fun <T> withContextIO(block: suspend CoroutineScope.() -> T): T {
        /**This function executes a block of code on the IO dispatcher and returns a result of type T.
        Useful for performing background operations within a coroutine context.*/
        return withContext(io, block)
    }

    protected suspend fun <T> withContextMain(block: suspend CoroutineScope.() -> T): T {
         /**This function executes a block of code on the Main dispatcher and returns a result of type T.
         Suitable for running UI-related code within a coroutine context.*/
        return withContext(main, block)
    }

    protected suspend fun <T> withContextMainImmediate(block: suspend CoroutineScope.() -> T): T {
         /**Similar to withContextMain but with immediate (eager) execution.
         Use this when you need the code to run immediately on the Main dispatcher.
         Returns a result of type T within a coroutine context.*/
        return withContext(mainImmediate, block)
    }
}