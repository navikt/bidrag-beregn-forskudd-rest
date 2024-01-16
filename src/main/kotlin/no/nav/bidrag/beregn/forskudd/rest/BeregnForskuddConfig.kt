package no.nav.bidrag.beregn.forskudd.rest

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.util.StdDateFormat
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.examples.Example
import no.nav.bidrag.beregn.forskudd.BeregnForskuddApi
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.DefaultCorsFilter
import no.nav.bidrag.commons.web.UserMdcFilter
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
@OpenAPIDefinition(
    info = Info(title = "bidrag-beregn-forskudd-rest", version = "v1"),
    security = [SecurityRequirement(name = "bearer-key")],
)
@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearer-key",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
)
@Import(CorrelationIdFilter::class, UserMdcFilter::class, DefaultCorsFilter::class, BeregnForskuddApi::class)
class BeregnForskuddConfig {

    @Bean
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        return Jackson2ObjectMapperBuilder()
            .dateFormat(StdDateFormat())
            .failOnUnknownProperties(false)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @Bean
    fun openApiCustomiser(examples: Collection<Example>): OpenApiCustomizer {
        return OpenApiCustomizer { openAPI ->
            examples.forEach { example ->
                openAPI.components.addExamples(example.description, example)
            }
        }
    }
}
