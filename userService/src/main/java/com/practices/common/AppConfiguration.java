package com.practices.common;

import com.practices.dto.UserRequest;
import com.practices.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(UserRequest.class, User.class)
                .addMappings(mapper -> mapper.skip(User::setId));
        return modelMapper;
    }

}