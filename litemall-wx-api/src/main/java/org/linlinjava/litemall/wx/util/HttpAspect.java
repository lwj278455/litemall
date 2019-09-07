package org.linlinjava.litemall.wx.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author : 邹智敏
 * @data : 2018/3/30
 * @Description :  切面答应日志
 **/
@Aspect
@Component
public class HttpAspect {

    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("execution(public * org.linlinjava.litemall.wx.web.*.*(..)))")
    public void valid() {}
    @Pointcut("execution(* org.linlinjava.litemall.db.dao.*.*(..))")
    private void pointCutMethod() {
    }

    /**
     * 前置通知
     */
    @Before("valid()")
    public void doBefore(JoinPoint joinPoint) {

        logger.info("<=========================================开始请求====================================>");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        //url
        logger.info("[请\t求\t路\t径]:["+request.getRequestURL()+"]");

        //method
        logger.info("[请\t求\t方\t法]:["+request.getMethod()+"]");

        //ip
        logger.info("[远\t程\t地\t址]:["+request.getRemoteAddr()+"]");

        //类方法
        logger.info("[请\t求\t方\t法]:["+joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()+"]");

        //对象携带参数
        Object[] args = joinPoint.getArgs();

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                logger.info("[方\t法\t参\t数]:{}", Arrays.asList(args[i]));
            }
        }
    }

    /**
     * 后置通知
     */
    @After("valid()")
    public void doAfter() {
        logger.info("<=========================================结束请求====================================>");
    }

    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.nanoTime();
        Object obj = pjp.proceed();
        long end = System.nanoTime();

        logger.info("调用Mapper方法：{}，参数：{}，执行耗时：{}纳秒，耗时：{}毫秒",
                pjp.getSignature().toString(), Arrays.toString(pjp.getArgs()),
                (end - begin), (end - begin) / 1000000);
        return obj;
    }

    /**
     * 通知后返回的数据
     * @param object
     */
    @AfterReturning(returning = "object", pointcut = "valid()")
    public void doAfterReturning(Object object) {
        try{
            logger.info("[返\t回\t参\t数]:"+object);
        }catch (NullPointerException e){
            logger.info("[异\t常\t信\t息]["+e.getMessage()+"]");
        }
    }
}
