
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

object ExecutorMockFactory {
    private const val CORE_POOL_SIZE = 5
    private const val MAX_POOL_SIZE = 8
    private const val ASYNC_PREFIX = "async-"
    private const val WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN = true

    fun get(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = CORE_POOL_SIZE
        executor.maxPoolSize = MAX_POOL_SIZE
        executor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN)
        executor.setThreadNamePrefix(ASYNC_PREFIX)
        executor.initialize()
        return executor
    }
}
