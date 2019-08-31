package com.platform.job;

import java.util.Arrays;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages={"com.platform.job"})
public class JobApplication 
{
    public static void main( String[] args )
    {
    	boolean hasProfile = Arrays.stream(args).anyMatch(arg -> arg.contains("config.server.url"));
        String[] cArgs=args;
        if(!hasProfile){
            cArgs=new String[args.length+2];
            cArgs[0]="--spring.profiles.active=local";
            cArgs[1]="--spring.cloud.config.enabled=false";
            System.arraycopy(args,0,cArgs,2,args.length);
        }
        new SpringApplicationBuilder(JobApplication.class).web(WebApplicationType.SERVLET).run(cArgs);
    }
}
