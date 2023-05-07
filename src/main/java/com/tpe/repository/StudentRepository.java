package com.tpe.repository;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Opsiyonel-->Spring boot JpaRepository'i extends ettiyse bu Repository'dir diye anlıyor yazmasakta olur ama kod okunurlugundan dolayı yazıyoruz
public interface StudentRepository extends JpaRepository<Student, Long> {//generik yapı entity class'ının ismini istiyor ve
    //uniq olan yani @Id annotation'i ile annote ettigimiz  field'in data type'ini istiyor

    //Neden interface olması lazım ?
    //Spring data JPA'nın ozelligini tasiyabilmesi icin JPARepository ya da  CRUDRepository ya da pagingRepository gibi
    //Spring data JPA dan gelen bu interface'leri extends etmesi lazim
    // bu sekilde extends ettigimiz de hazır birsuru kodlar gelicek
    // habernate de diyelim ki 20 tane kod gelicekse bundan 100 tane hazır kod(hazır method ) gelicek
    //conroller- Service-Repository katmanları arasında en kod yazacagimiz yer Repository katmanı nedeni ise
    //JPA repository yani SpringJPA'dan gelen bu hazır interface'leri kullanıyor olmamız
    boolean existsByEmail(String email);

    List<Student> findByLastName(String lastName);
    //finBy,GetBy gibi methodlar turetebilir formatta kurulmus
    //JPQL
    @Query("SELECT s FROM Student s WHERE s.grade=:pGrade")
    List<Student> findAllEqualsGrade(@Param("pGrade") Integer grade);
    //biz bu methodu calıstırdıgımız da ınteger grade degerini alıyor ve student entitiy classında ki grade
    // ile parametredeki grade'i alıyor query'e yerlestiriyor
    //"@Param("pGrade")" verdigimiz isim ile "s.grade=:pGrade" isim aynı olmalı
    //SQL
    //sql de de Student'i java da oldugu gibi kullandık
    //"s.grade=:pGrade" s.garde'in degeri bir variable'dan gelsin dersem ":" kullanılmalı "s.grade=75" deseydim gerek olmayacaktı.
    @Query(value ="SELECT * FROM Student s WHERE s.grade=:pGrade" , nativeQuery = true)
    List<Student> findAllEqualsGradeWithSQL(@Param("pGrade") Integer grade);

    //JPQL mucizesi ile pojo'yu DTO ya ceviriyor.
    //parametre ile verdiğimiz id ile ilk önce tablodan Student geliyor
    // sonra StudentDTO classındaki parametreli constructorı kullanarak
    // gelen Studentı vererek StudentDTO'ya çevirmiş mi oluyoruz
    @Query("SELECT new com.tpe.dto.StudentDTO(s) FROM Student s WHERE s.id=:id")
    Optional<StudentDTO> findStudentDTOById(@Param("id")Long id);
}






















