package com.tpe.security.service;

import com.tpe.domain.Role;
import com.tpe.domain.User;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // !!! bu calssda 1.amacim : Security katmanina User objelerimi verip UserDetails
    // turune cevrilmesini saglamak kisaca kendi Userlarimi security katmanina tanitmis
    // olacagiz
    // 2.amacimiz : Role bilgilerini Granted Auth. a cevirmek

    @Autowired
    UserRepository userRepository;

    //loadUserByUsername() methodu turetilen bir method degil ama ilerde
    // gorecegiz benim username'mim aslında bu username'in emaili gibi degişiklikler yapacagız
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //user'a ulaşmak icin normalde best practice service aracılıgıyla db'ye gitmemiz lazim
        //Ama simdi UserRepository'i enjecte edecegiz bu class'a ve oradan direk db ye gidecegiz
        // Cunku bunun cok fazla end pointleri yokconfilikt olma ihtimali yok yada BusinnesLogic yapcak bir durum yok.
        User user = userRepository.findByUserName(username).orElseThrow(() ->
                new ResourceNotFoundException("user not found with username : " + username));
        //gelen user null ise diye de bir kontrol yapacagız
        if (user != null) {
            //eger null degil ise user'i userDetails formatına cevirecek.
            return new org.springframework.security.core.userdetails.User(//bizim olusturdugumuz user degil de sprinFrameWork'ın
            //user'ini belirtmek için path'ini yazdık
                    user.getUserName(),
                    user.getPassword(),
                    buildGrantedAuthority(user.getRole())
                    //Rolleri biz biliyoruz ama springSecurity Rolleri GrantedAuthority olarak biliyor
                    // bu yuzden grantedAutherity'e cevirip oyle veriyoruz Rol'leri
            );
        } else {
            throw new UsernameNotFoundException("User not found get username" + username);
        }

    }
    //birden fazla role gelecgi icin List kullandık
    //(final Set<Role> roles) bu methodu cagırırken role degişkeninin dolmus olması lazım bu yuzden final keyword'u kullandık(extra guvenlik)
    //parametre olarak set aldık ama neden List'e attık: parametrede set olarak aldıgımız için yani uniq aldıgımız için neye attıgımız önemli degil.

    private static List<SimpleGrantedAuthority> buildGrantedAuthority(final Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
            //role.getName().name()-->getName deyince enum classIn ismi gelicek ama ben bunu istemiyorum
            //role_admin bilgisi gelsin istiyorum.bu yuzden .name() kullanıyorum.

        }

        return authorities;
    }
}
