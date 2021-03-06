package com.brahalla.oauthprovider.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
public class RememberMeConfiguration {

  @Value("${oauthprovider.remember-me.key}")
  private String rememberMeKey;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private UserDetailsService userDetailsService;

  @Bean
  public PersistentTokenRepository tokenRepository() {
    final JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(this.dataSource);
    tokenRepository.setCreateTableOnStartup(true);
    return tokenRepository;
  }

  @Bean
  public RememberMeServices rememberMeServices() {
    final PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
      this.rememberMeKey, this.userDetailsService, tokenRepository()
    );
    rememberMeServices.setAlwaysRemember(true);
    return rememberMeServices;
  }

  @Bean
  public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
    final RememberMeAuthenticationProvider rememberMeAuthenticationProvider = new RememberMeAuthenticationProvider(
      this.rememberMeKey
    );
    return rememberMeAuthenticationProvider;
  }

}
