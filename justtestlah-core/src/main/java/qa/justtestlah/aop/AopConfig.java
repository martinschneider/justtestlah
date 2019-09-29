package qa.justtestlah.aop;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP Logging Spring configuration. Enables entry-exit logging for all public methods in steps and
 * page objects and all methods.
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

  private static final String POINTCUT_TEMPLATE = "execution(public * __package__..*.*(..))";

  @Value("${pages.package}")
  private String pagesPackage;

  @Value("${steps.package}")
  private String stepsPackage;

  @Bean
  public EntryExitLoggingAspect entryExitLoggingAspect() {
    return new EntryExitLoggingAspect();
  }

  @Bean
  public AspectJExpressionPointcutAdvisor stepsLoggingAdvisor() {
    AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
    advisor.setExpression(POINTCUT_TEMPLATE.replaceFirst("__package__", stepsPackage));
    advisor.setAdvice(entryExitLoggingAspect());
    return advisor;
  }

  @Bean
  public AspectJExpressionPointcutAdvisor pagesLoggingAdvisor() {
    AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
    advisor.setExpression(POINTCUT_TEMPLATE.replaceFirst("__package__", pagesPackage));
    advisor.setAdvice(entryExitLoggingAspect());
    return advisor;
  }
}
