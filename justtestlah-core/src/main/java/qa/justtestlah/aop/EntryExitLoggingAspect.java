package qa.justtestlah.aop;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.EntryExitLogging;

/** Aspect to create SLF4J logging entries when entering and exiting a method. */
@Aspect
@Component
public class EntryExitLoggingAspect implements MethodInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger("entryExit");

  @Value("${entryexit.loglevel:DEBUG}")
  private String entryExitLogLevel;

  @Value("${summary.loglevel:INFO}")
  private String summaryLogLevel;

  @Around("@annotation(loggable)")
  public Object logAnnotatedMethods(final ProceedingJoinPoint joinPoint, EntryExitLogging loggable)
      throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    long start = entry(method, joinPoint.getArgs());
    Object returnValue = joinPoint.proceed();
    long finish = System.currentTimeMillis();
    exit(method, finish - start);
    return returnValue;
  }

  /** this is the entry point for advices configured in {@link qa.justtestlah.aop.AopConfig} */
  public Object invoke(final MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();
    long start = entry(method, invocation.getArguments());
    Object returnValue = invocation.proceed();
    long finish = System.currentTimeMillis();
    exit(method, finish - start);
    return returnValue;
  }

  // log when entering a method
  private long entry(Method method, Object[] methodArguments) {
    StringBuilder logMessage = new StringBuilder("Entering ");
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
    logMessage(LOG, getLogLevel(entryExitLogLevel), logMessage.toString());
    return System.currentTimeMillis();
  }

  // log before exiting a method
  private void exit(Method method, long duration) {
    StringBuilder logMessage = new StringBuilder("Exiting ");
    Class<?> clazz = method.getDeclaringClass();
    logMessage.append(clazz.getSimpleName());
    logMessage.append(":");
    logMessage.append(method.getName());
    logMessage.append(" after ");
    logMessage.append(duration);
    logMessage.append(" ms");

    // exit logging
    logMessage(LOG, getLogLevel(entryExitLogLevel), logMessage.toString());

    logMessage = new StringBuilder(clazz.getSimpleName());
    logMessage.append(":");
    logMessage.append(method.getName());
    logMessage.append(" took ");
    logMessage.append(duration);
    logMessage.append(" ms");

    // summary logging
    logMessage(LOG, getLogLevel(summaryLogLevel), logMessage.toString());
  }

  private void logMessage(Logger logger, int logLevel, String message) {
    switch (logLevel) {
      case EntryExitLogging.OFF:
        break;
      case EntryExitLogging.TRACE:
        logger.trace(message);
        break;
      case EntryExitLogging.DEBUG:
        logger.debug(message);
        break;
      case EntryExitLogging.INFO:
        logger.info(message);
        break;
      case EntryExitLogging.WARN:
        logger.warn(message);
        break;
      case EntryExitLogging.ERROR:
        logger.error(message);
        break;
      default:
        logger.debug(message);
    }
  }

  private int getLogLevel(String parameter) {
    switch (parameter.toLowerCase()) {
      case "off":
        return EntryExitLogging.OFF;
      case "trace":
        return EntryExitLogging.TRACE;
      case "debug":
        return EntryExitLogging.DEBUG;
      case "info":
        return EntryExitLogging.INFO;
      case "warn":
        return EntryExitLogging.WARN;
      case "error":
        return EntryExitLogging.ERROR;
      default:
        return EntryExitLogging.DEBUG;
    }
  }
}
