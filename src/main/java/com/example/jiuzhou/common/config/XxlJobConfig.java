package com.example.jiuzhou.common.config;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author Appartion
 * @data 2022/12/27
 * 一入代码深似海，从此生活是路人
 */

@Configuration
public class XxlJobConfig {


    @Value("${xxl.job.admin.addresses:http://127.0.0.1:28080/xxl-job-admin}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken:4df2a350}")
    private String accessToken;

    @Value("${xxl.job.executor.appname:jiuzhou}")
    private String appname;

//    @Value("${xxl.job.executor.address}")
    private String address;

//    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port:9989}")
    private int port;

    @Value("${xxl.job.executor.logpath:/Users/wojiushiwo/spring/my-xxl-job/logs/jobhandler}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays:30}")
    private int logRetentionDays;


//    @Bean
//    public XxlJobSpringExecutor xxlJobExecutor() {
//
//        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
//        xxlJobSpringExecutor.setAppname(appname);
//        xxlJobSpringExecutor.setAddress(address);
//        xxlJobSpringExecutor.setIp(ip);
//        xxlJobSpringExecutor.setPort(port);
//        xxlJobSpringExecutor.setAccessToken(accessToken);
//        xxlJobSpringExecutor.setLogPath(logPath);
//        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
//        return xxlJobSpringExecutor;
//    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     *
     *      1、引入依赖：
     *          <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-commons</artifactId>
     *             <version>${version}</version>
     *         </dependency>
     *
     *      2、配置文件，或者容器启动变量
     *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     *
     *      3、获取IP
     *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */


}
