package com.amir.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfiguration {

	private final RSAPublicKey publicKey;
	private final RSAPrivateKey privateKey;
	private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
	private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
	private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;
	
	public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
								 CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint,
								 CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048); // The generated key will have size of 2048 bits
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		this.publicKey = (RSAPublicKey) keyPair.getPublic();
		this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
		this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
		this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;
	}

	@Value("${api.endpoint.base-url}")
	private String baseUrl;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.err.println("securityFilterChain");
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, this.baseUrl + "/artifacts/**").permitAll()
						.requestMatchers(HttpMethod.GET, this.baseUrl + "/users/**").hasAuthority("ROLE_admin")
						.requestMatchers(HttpMethod.POST, this.baseUrl + "/users").hasAuthority("ROLE_admin")
						.requestMatchers(HttpMethod.PUT, this.baseUrl + "/users/**").hasAuthority("ROLE_admin")
						.requestMatchers(HttpMethod.DELETE, this.baseUrl + "/users/**").hasAuthority("ROLE_admin")
						.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
						// Disallow everything else.
						.anyRequest().authenticated() // Always a good idea to put this as last
						)
				.headers(headers -> headers.frameOptions(o -> o.disable())) // for h2 console access
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(this.customBasicAuthenticationEntryPoint)) // this will show exception in response in case of wrong username/password
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
												.authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint)
												.accessDeniedHandler(customBearerTokenAccessDeniedHandler))
				// don't keep a session for any request, this will save memory
				.sessionManagement(sessionManagment -> sessionManagment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	
	@Bean
	public JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey
				.Builder(this.publicKey)
				.privateKey(this.privateKey)
				.build();
		JWKSource<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwkSet);
	}
	
	@Bean
	public JwtDecoder jwtDecoder() {
		 return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
	}
	
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // default e.g. SCOPE_ROLE_admin
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		
		return jwtAuthenticationConverter;
	}
}
