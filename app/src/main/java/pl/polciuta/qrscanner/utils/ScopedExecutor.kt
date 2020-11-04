package pl.polciuta.qrscanner.utils

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean


class ScopedExecutor(private val executor: Executor) : Executor {

    private val shutdown = AtomicBoolean()

    override fun execute(command: Runnable) {
        // Return early if this object has been shut down.
        if (shutdown.get()) {
            return
        }
        executor.execute execution@ {
            // Check again in case it has been shut down in the mean time.
            if (shutdown.get()) {
                return@execution
            }
            command.run()
        }
    }

    fun shutdown() {
        shutdown.set(true)
    }

}