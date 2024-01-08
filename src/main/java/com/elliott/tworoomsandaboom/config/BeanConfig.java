package com.elliott.tworoomsandaboom.config;

import com.elliott.tworoomsandaboom.db.DatabaseConnectionManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig
{
    @Bean
    public DatabaseConnectionManager databaseConnectionManager()
    {
        return new DatabaseConnectionManager("localhost", "3306", "two_rooms_and_a_boom", "root");
    }
}
