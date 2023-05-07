package com.tpe.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Security katmanina bu clasimin konfigurasyon classi oldugunu soylutorum
@EnableWebSecurity//Spring Security işleminin bu class tarafından yönetileceğini belirtiyoruz.
@EnableGlobalMethodSecurity(prePostEnabled = true) // merhod seviyede yetkilendirme(çalısacagımızı) yapacagimi soyluyorum
//Yani soyle demis oluyorum controllers'daki su methodu admin yapabilsin su methodu student yapabilsi
// suna da teacher yapabilsin demiş oluyorum.
//Mesela       getAll()  -->Admin , getById() -->Admin,  createStudent() -->Admin,Student yapabilir.
// Biz bunu @EnableGlobalMethodSecurity annotation'ı ile yapıyoruz.
//(prePostEnabled = true) ne demek? Methodun uzerine admin yapabilicegi ile alakalı annotation'ım var hasRoy diye onu kullanacagımızı soyluyoruz.
//

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // !!! bu classda amacimiz : AuthManager, Provider , PassEncoder larimi olusturup birbirleriyle
    // tanistirmak

    @Autowired
    private UserDetailsService userDetailsService;
//csrf : Diyelim ki mobil bankacılıga girdim o arada mail geldi ve mail sayfasına gectim ama arka planda
//mobil bankacılık açık zararlı bir yazılım sayfama ulasıp istemedigim seyler yapabilir
//iste csrf koruması bunu engelliyor yani sayfalar acık bile olsa sayfalar arası bilgi akısını engellemiş oluyor.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().  // csrf korumasini disable yapiyoruz
                authorizeHttpRequests().  // gelen butun rwquestleri yetkilimi diye kontrol edecegiz
                antMatchers("/",
                "index.html",
                "/register",
                "/css/*",
                "/js/*").permitAll(). // bu end-pointleri yetkili mi diye kontrol etme
//                and().
//                authorizeRequests().antMatchers("/students/**").hasRole("ADMIN").
                //"/student" endpoiınti ile gelen butun endpointleri sadece ADMIN rolu olanlar gercekleştirebilsin
                anyRequest(). // muaf tutulan end-pointler disinda gelen herhangi bir requesti
                authenticated(). // yetkili mi diye kontrol et
                and().
                httpBasic(); // bunu yaparkende Basic Authentication kullanilacagimızı belirtiyoruz
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);//4ile 31 arasında bir sayı girebiliyorum 4 en az sifreleme mekanizmasi
        // daha basit tutuldugu icin cok kısa surer , 31 en fazla koruma saglıyor oda en yuksek seviyede koruma saglıyor
        //Burda da şu problem oluyor her seferinde password encode edilecegi için bu işlem uzuyor .performans dusuyor
        // 4,5,6 tavsiye edilmiyor ortalama bir deger girilmeli.

    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder()); // encoder ile tanistirdim
        authProvider.setUserDetailsService(userDetailsService); // Service katimi tanistirmis oldum

        return authProvider;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(authProvider());
    }
}






















