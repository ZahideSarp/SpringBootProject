package com.tpe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name= "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25, nullable = false)
    private String firstName;

    @Column(length = 25, nullable = false)
    private String lastName;

    @Column(length = 25, nullable = false, unique = true)//uniq olmalı
    private String userName;

    @Column(length = 255, nullable = false)//bazı hash algoritmalarının yuksek çıkma ihtimaline karsı
    private String password;

    //!!! user ile rolu bagladık
    @JoinTable(name="tbl_user_role",//yeni bir tablo olusm
            joinColumns = @JoinColumn(name="user_id"),//bu taraftan gelecek olan datanın tutulması için olusacak kolonun ismi ası lazım
            inverseJoinColumns = @JoinColumn(name="role_id"))//rolden  tarafından gelecek olan datanın tutulması için olusacak kolonun ismi ası lazım

    @ManyToMany(fetch = FetchType.EAGER)//userı her cektigim de rolleri bana gelmesi lazım.useri biz bunun için olusturuyoruz zaten
    //eger lazy dersek 2 tane query atmak zorunda kalacak ama eager'a cekersen tek query de halledecek
    private Set<Role> role = new HashSet<>();
    // Admin ; Student -> biri 2 kere ADMIN de girse alır.Listte uniqlik yoktur bu yuzde set kullandık


    //!!! user ile studenti baglamamız lazım
    @JsonIgnore
    @OneToOne(mappedBy = "user")//ilişki sahibi student olsun diyorum
    private Student student;

//user'in bu uygulama icerisinde birebir eslesmesini istiyoruz.
    // (yani bir kullanıcınin LMs siteminde bir kaydı olabilir bir email ile bir tane ogrenci acabiliriz sadece.)
}
