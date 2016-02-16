package com.brahalla.oauthprovider.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  @Value("${oauthprovider.token.expiration}")
  private Integer tokenExpiration;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private ClientDetailsService clientDetailsService;

  @Autowired
  private JwtTokenStore jwtTokenStore;

  @Autowired
  private TokenEnhancer tokenEnhancer;

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
      .tokenServices(defaultTokenServices())
      .authenticationManager(this.authenticationManager);
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients
      .inMemory()
      //.jdbc(dataSource())
        .withClient("acme")
        .secret("acmesecret")
        .authorizedGrantTypes("authorization_code", "implicit", "refresh_token", "password")
        .scopes("openid");
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer
      .checkTokenAccess("isAuthenticated()")
      .tokenKeyAccess("permitAll()");

    oauthServer.allowFormAuthenticationForClients();
  }

  @Bean
  public AuthorizationServerTokenServices defaultTokenServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(this.jwtTokenStore);
    defaultTokenServices.setClientDetailsService(this.clientDetailsService);
    defaultTokenServices.setTokenEnhancer(this.tokenEnhancer);
    defaultTokenServices.setSupportRefreshToken(true);
    defaultTokenServices.setAccessTokenValiditySeconds(this.tokenExpiration);
    return defaultTokenServices;
  }

  /*@Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
      .driverClassName("org.h2.Driver")
      .url("jdbc:h2:~/test")
      .username("sa")
      .password("")
      .build();
  }*/

}