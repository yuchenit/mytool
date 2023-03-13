package com.sifar.test.component.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    /**
     * 全局参数(如header中的token)
     *
     * @return List<Parameter>
     */
    private List<Parameter> parameter() {
        List<Parameter> params = new ArrayList<>();
        params.add(new ParameterBuilder().name("userToken")
                .description("请求令牌")
                .modelRef(new ModelRef("string"))
                .parameterType("query")
                .required(false).build());

        params.add(new ParameterBuilder().name("uid")
                .description("用户id")
                .modelRef(new ModelRef("int"))
                .parameterType("query")
                .required(false).build());
        return params;
    }

    @Value("${spring.cloud.swagger2.enable:false}")
    boolean swaggerSwitch;

    //配置Swagger信息=apiInfo
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("SiFar - Test服务接口文档")
                .version("v1.0")
                .build();
    }


    //配置了Swagger的Docket的bean实例
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(swaggerSwitch)//enable是否启动Swagger，如果为False，则Swagger不能再浏览器中访问
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sifar.test.controller"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(parameter());
    }
}













































//    //配置了Swagger的Docket的bean实例
//    @Bean
//    public Docket docket(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .enable(true)//enable是否启动Swagger，如果为False，则Swagger不能再浏览器中访问
//                .select()
//                //RequestHandlerSelectors，配置要扫描接口的方式
//                //basePackage:指定要扫描的包
//                //any():扫描全部
//                //none():不扫描
//                //withClassAnnotation：扫描类上的注解，参数是一个注解的反射对象
//                //withMethodAnnotation：扫描方法上的注解
//                .apis(RequestHandlerSelectors.basePackage("com.sifar.center.controller"))
//                //paths()。过滤什么路径
////                .paths(PathSelectors.ant("/sifar/**"))
//                .build();
//    }