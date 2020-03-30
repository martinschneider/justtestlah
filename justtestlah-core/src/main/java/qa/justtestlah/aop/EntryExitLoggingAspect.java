package qa.justtestlah.aop;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.EntryExitLogging;
import qa.justtestlah.log.LogLevel;
import qa.justtestlah.log.TestLogWriter;

/** Aspect to create SLF4J logging entries when entering and exiting a method. */
@Aspect
@Component
public class EntryExitLoggingAspect implements MethodInterceptor {

  @Autowired private TestLogWriter logWriter;

  @Value("${entryexit.loglevel:INFO}")
  private String entryExitLogLevel;

  @Value("${summary.loglevel:OFF}")
  private String summaryLogLevel;

  @Around("@annotation(loggable)")
  public Object logAnnotatedMethods(final ProceedingJoinPoint joinPoint, EntryExitLogging loggable)
      throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    long start = entry(method, joinPoint.getArgs(), loggable.entryExitLogLevel());
    Object returnValue = joinPoint.proceed();
    long finish = System.currentTimeMillis();
    exit(method, finish - start, loggable.entryExitLogLevel(), loggable.summaryLogLevel());
    return returnValue;
  }

  /** this is the entry point for advices configured in {@link qa.justtestlah.aop.AopConfig} */
  public Object invoke(final MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();
    long start = entry(method, invocation.getArguments(), getLogLevel(entryExitLogLevel));
    Object returnValue = invocation.proceed();
    long finish = System.currentTimeMillis();
    exit(method, finish - start, getLogLevel(entryExitLogLevel), getLogLevel(summaryLogLevel));
    return returnValue;
  }

  // log when entering a method
  private long entry(Method method, Object[] methodArguments, int logLevel) {
    StringBuilder logMessage = new StringBuilder();
    logMessage.append("Entering ");
    Class<?> clazz = method.getDeclaringClass();
    logMessage.append(clazz.getSimpleName());
    logMessage.append(":");
    logMessage.append(method.getName());
    if (methodArguments.length > 0) {
      logMessage.append(" [");
      for (int i = 0; i < methodArguments.length; i++) {
        logMessage.append(method.getParameters()[i].getName());
        logMessage.append("=");
        logMessage.append(methodArguments[i]);
        if (i != methodArguments.length - 1) {
          logMessage.append(",");
        }
      }
      logMessage.append("]");
    }
    logWriter.log(logLevel, TestLogWriter.ENTRY_EXIT_INDENTATION, logMessage.toString());
    return System.currentTimeMillis();
  }

  // log before exiting a method
  private void exit(Method method, long duration, int entryExitLogLevel, int summaryLogLevel) {
    StringBuilder logMessage = new StringBuilder();
    logMessage.append("Exiting ");
    Class<?> clazz = method.getDeclaringClass();
    logMessage.append(clazz.getSimpleName());
    logMessage.append(":");
    logMessage.append(method.getName());
    logMessage.append(" after ");
    logMessage.append(duration);
    logMessage.append(" ms");

    // exit logging
    logWriter.log(entryExitLogLevel, TestLogWriter.ENTRY_EXIT_INDENTATION, logMessage.toString());

    logMessage = new StringBuilder();
    logMessage.append(clazz.getSimpleName());
    logMessage.append(":");
    logMessage.append(method.getName());
    logMessage.append(" took ");
    logMessage.append(duration);
    logMessage.append(" ms");

    // summary logging
    logWriter.log(summaryLogLevel, TestLogWriter.ENTRY_EXIT_INDENTATION, logMessage.toString());
  }

  private int getLogLevel(String parameter) {
    switch (parameter.toLowerCase()) {
      case "off":
        return LogLevel.OFF;
      case "trace":
        return LogLevel.TRACE;
      case "debug":
        return LogLevel.DEBUG;
      case "info":
        return LogLevel.INFO;
      case "warn":
        return LogLevel.WARN;
      case "error":
        return LogLevel.ERROR;
      default:
        return LogLevel.DEBUG;
    }
  }
}
