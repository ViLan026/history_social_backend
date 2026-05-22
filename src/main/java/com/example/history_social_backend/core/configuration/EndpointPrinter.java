// package com.example.history_social_backend.core.configuration;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// @Configuration
// public class EndpointPrinter {

//     @Bean
//     CommandLineRunner printEndpoints(RequestMappingHandlerMapping mapping) {
//         return args -> {

//             System.out.println("\n========= API LIST =========");

//             mapping.getHandlerMethods().forEach((info, method) -> {

//                 var paths = info.getPatternValues();
//                 var methodsSet = info.getMethodsCondition().getMethods();

//                 methodsSet.forEach(httpMethod -> {
//                     paths.forEach(path -> {
//                         System.out.println(httpMethod + " " + path);
//                     });
//                 });
//             });

//             System.out.println("============================\n");
//         };
//     }
// }