package jerry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("jerry").password(passwordEncoder().encode("derschneeman")).roles("ADMIN")
                .and().withUser("robot").password(passwordEncoder().encode("robot")).roles("USER")
                .and().withUser("alex").password(passwordEncoder().encode("alex")).roles("USER")
                .and().withUser("richi").password(passwordEncoder().encode("richi")).roles("USER")
                .and().withUser("michi").password(passwordEncoder().encode("michi")).roles("USER")
                .and().withUser("sissi").password(passwordEncoder().encode("sissi")).roles("USER")
                .and().withUser("christian").password(passwordEncoder().encode("christian")).roles("USER");
        ;
//.and().withUser()
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/webSocket").permitAll()
                .antMatchers("/masterSocket").permitAll()
                .antMatchers("/api/setting/**").permitAll()

                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .rememberMe().key("sooscretyoubiteyourtongeandyesthetypowasintent")
                .tokenValiditySeconds(Integer.MAX_VALUE)
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
