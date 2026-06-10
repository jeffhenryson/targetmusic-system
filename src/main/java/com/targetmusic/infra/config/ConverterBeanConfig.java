package com.targetmusic.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.targetmusic.adapter.in.converter.PermissionDTOConverter;
import com.targetmusic.adapter.in.converter.RoleDTOConverter;
import com.targetmusic.adapter.in.converter.UserDTOConverter;
import com.targetmusic.adapter.out.persistence.converter.UserEntityConverter;

@Configuration
class ConverterBeanConfig {

    @Bean
    UserEntityConverter userEntityConverter() {
        return new UserEntityConverter();
    }

    @Bean
    UserDTOConverter userDTOConverter(@Value("${avatar.base-url:http://localhost:8080}") String avatarBaseUrl) {
        return new UserDTOConverter(avatarBaseUrl);
    }

    @Bean
    RoleDTOConverter roleDTOConverter() {
        return new RoleDTOConverter();
    }

    @Bean
    PermissionDTOConverter permissionDTOConverter() {
        return new PermissionDTOConverter();
    }
}
