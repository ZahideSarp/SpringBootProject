package com.tpe.domain;

import com.tpe.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tbl_role")
public class Role {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Enumerated(EnumType.STRING)//su name isim li fieldi enumType dan al demiş oluyorum
    //Normalde boyle bırakırsam ROLE_STUDENT 0 diye ROLE_ADMIN ise 1 ide kaydedilir id olarak
    //boyle bu durumda 0 ın ROLE_STUDENT oldugunu bilmem gerekirdi simdi 2 tane var ama burada
    // 10 tane role olsaydı tutmak zor olacaktı bu yuzden String olarak tut diyorum .

    @Column(length = 30, nullable = false)//roleBase calısıyorum mutlaka karsılıgı olması lazım
    // zaten rolu olmayan kullanıcı security acısından bir hiçtir hiç bir işlem yapamaz .

    private UserRole name;

    @Override
    public String toString() {
        return "Role{" +
                "name=" + name +
                '}';
    }
}
