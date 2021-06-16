package com.platform.job.util;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.impl.triggers.CronTriggerImpl;

public class ValidExpression {
	public static void main(String[] args) throws ParseException {
		System.out.println(isValidExpressionStr("0 0 0 1 * ? *"));
		System.out.println(isCanDoExpression("0 0 0 1 * ? *"));
	}
	
	//校验定时任务格式
	public static boolean isValidExpressionStr(String expressionStr){
        if(null == expressionStr){
            return false;
        }
        return CronExpression.isValidExpression(expressionStr);
    }
	
	//校验表达式是否能触发
	 public static boolean isCanDoExpression(String expressionStr) throws ParseException{
        //先判断表达式格式是否正确
        if(!isValidExpressionStr(expressionStr)){
            return false;
        }
        CronTriggerImpl triggerImpl=new CronTriggerImpl();
        triggerImpl.setCronExpression(expressionStr);
        Date date=triggerImpl.computeFirstFireTime(null);
        return date!=null&&date.after(new Date());
	}
}
