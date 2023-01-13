package com.livk.autoconfigure.qrcode;

import com.livk.auto.service.annotation.SpringAutoService;
import com.livk.autoconfigure.qrcode.resolver.QRCodeMethodReturnValueHandler;
import com.livk.autoconfigure.qrcode.resolver.ReactiveQRCodeMethodReturnValueHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * <p>
 * QRCodeAutoConfiguration
 * </p>
 *
 * @author livk
 */
@AutoConfiguration
@SpringAutoService
@ConditionalOnClass(name = "com.google.zxing.common.BitMatrix")
public class QRCodeAutoConfiguration {

    /**
     * The type Web mvc qr code auto configuration.
     */
    @AutoConfiguration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public static class WebMvcQRCodeAutoConfiguration implements WebMvcConfigurer {

        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
            handlers.add(new QRCodeMethodReturnValueHandler());
        }
    }

    /**
     * The type Web flux qr code auto configuration.
     */
    @AutoConfiguration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public static class WebFluxQRCodeAutoConfiguration implements WebFluxConfigurer {

        /**
         * Reactive qr code method return value handler reactive qr code method return value handler.
         *
         * @return the reactive qr code method return value handler
         */
        @Bean
        public ReactiveQRCodeMethodReturnValueHandler reactiveQRCodeMethodReturnValueHandler() {
            return new ReactiveQRCodeMethodReturnValueHandler();
        }
    }
}
