package sonnicon.eonbot.type

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotation for associating commands with their handlers.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])
@interface ExecutorFunc {
    String value()
}