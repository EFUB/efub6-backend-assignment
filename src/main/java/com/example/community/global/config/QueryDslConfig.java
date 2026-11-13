package com.example.community.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// JPAQueryFactory를 스프링 빈으로 등록하기 위한 설정 클래스
@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {
    private final EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // QueryDSL은 쿼리를 조립만 하고 실행은 EM에게 위임하므로,
        // 실행을 맡길 EM(프록시)을 넣어줌
        // 이후 @Transactional 안에서 호출되면 그 트랜잭션의 EM으로 실행됨
        return new JPAQueryFactory(entityManager);
    }
}
