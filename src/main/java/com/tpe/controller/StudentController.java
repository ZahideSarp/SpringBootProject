
package com.tpe.controller;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/students") // http://localhost:8080/students

public class StudentController {
    //!!! Logger objesi
    Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;//controller tarafı service classı ile gorusecek bir nevi ona bagımlı

    // !!! Butun ogrencileri getirelim
    @GetMapping // http://localhost:8080/students/ + GET
    @PreAuthorize("hasRole('ADMIN')")//admin yetkisi olan kullanıcılar erissin
    //Enum da ADMIN diye bir role yok ROLE_ADMIN var biz buraya neden sadece ADMIN yazdık?
    //Cunku hasRole methodu içerisine koydugumuz string'in onune kendisi koyuyor "ROLE_" u
    //DispatcherServlet, handler Mapping yardımı ile annotation'ı gorunce controller da bu methoda yonlendiriyor.
    public ResponseEntity<List<Student>> getAll() {//uzunlugu degisken oldugu icin esnek bir yapı kullanmalıyım
        //ResponceEntity<> -> 1-entity'i gonderiyor  ve 2-status kodları gonderiyor
        List<Student> students = studentService.getAll();
        //enjecte ettigim studentService classına gondermem lazım bunun icin enjecte ettigim field'i kullanarak bir method olusturuyorum
        //service de varmıs gibi daha sonra create edecegimiz
        //ve sevice class'ina gitti oradan repository ve db'ye gitti
        // ve bana responce olarak  list<Student> donecegi icin list<Student> yapıyorum.

        return ResponseEntity.ok(students); // List<Student> + HTTP-StatusCode(200)
        //.ok() aslında method chance gibi entitynin yanında status kodunu setleyerek on tarafa gonderiyor

        //student yoksa ici bos json gelir exc. atmaz.
    }

    // !!! Create new student
    @PostMapping // http://localhost:8080/students/ + POST + JSON
    //@@Valid annotationu gelen json datam studentla maplenecek ama student tablomdaki validetionlar ile de maplenmesini saglıyorum
    //@RequestBody:
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Map<String,String>> createStudent( @Valid @RequestBody Student student){
        studentService.createStudent(student);

        Map<String,String> map = new HashMap<>();
        map.put("message", "Student is created successfuly");
        map.put("status", "true");

        return new ResponseEntity<>(map, HttpStatus.CREATED); // 201
    }
        // return ResponseEntity.ok("Student is created succesfuly");

    //!!! Get a Student by ID via RequestParam --> http://localhost:8080/students/query?id=1


    @GetMapping("/query")
    public ResponseEntity<Student> getStudent(@RequestParam("id") Long id) {
        Student student = studentService.findStudent(id);
        return ResponseEntity.ok(student);
    }


    //!!! Get a Student by ID via PathVariable--> http://localhost:8080/students/1
    //best practice de tek data alacaksam pathVariable almam daha mantıklı ama
    // coklu data alacaksam requestParam daha anlasılır oldugu icin daha kullanıslı.
//butun controlleri service de yapiyorum
    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentWithPath(@PathVariable("id") Long id) {
        Student student = studentService.findStudent(id);
        return ResponseEntity.ok(student);

    }

    //!!! Delete Student with id
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable("id") Long id) {
        //tek data alacaksam @PathVariable Long id seklinde yazabilirim
        studentService.deleteStudent(id);

        //Controllerin bir gorevi daha var gelen responsu uygun formata cevirerek client tarafına gondermek
        Map<String, String> map = new HashMap<>();
        map.put("message", "Student is deleted successfuly");
        map.put("status", "true");

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    //!!! Update Student
//DTO-->Data Transfer Object
    //http://localhost:8080/students/1 --> endpoint(putMapping)+ id(pathVariable)+ JSON (request body)
    //varligi yoklugu sorgularken exception alma ihtimali unique degerler uzerinden olur diyebilir miyiz.
    @PutMapping("{id}")
    public ResponseEntity<Map<String, String>> updateStudent(
            @PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        studentService.updateStudent(id, studentDTO);

        Map<String, String> map = new HashMap<>();
        map.put("message", "Student is updated successfuly");
        map.put("status", "true");

        return new ResponseEntity<>(map, HttpStatus.OK);//return ResponseEntity.ok(map);-->aynı işlemi yapar
    }

    //!!! pageable
    //paging //client-side(performansli degildir)//server-side(parcalama service'de yapilir, ag yorulmaz)
    @GetMapping("/page") // http://localhost:8080/students/page
    //http://localhost:8080/students/page?page=1&size=2&sort=name&direction=ASC
    public ResponseEntity<Page<Student>> getAllWithPage(
            @RequestParam("page") int page, // kacinci sayfa gelsin
            @RequestParam("size") int size, // sayfa basi kac urun
            @RequestParam("sort") String prop, // hangi field a gore siralanacak
            @RequestParam("direction") Sort.Direction direction // siralama turu

    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<Student> studentPage = studentService.getAllWithPage(pageable);

        return ResponseEntity.ok(studentPage);

    }

    // !!! Get By LastName
    @GetMapping("/querylastname")
    public ResponseEntity<List<Student>> getStudentByLastName(@RequestParam("lastName") String lastName) {
        List<Student> list = studentService.findStudent(lastName);
        return ResponseEntity.ok(list);
    }


    //!!! get all Student By Grade(JPQL java persistens query language)-->100 alan ogrencileri getir gibi...
    @GetMapping("/grade/{grade}") // http://localhost:8080/students/grade/75  + GET
    public ResponseEntity<List<Student>> getStudentsEqualsGrade(@PathVariable("grade") Integer grade) {

        List<Student> list = studentService.findAllEqualsGrade(grade);
        //findBy..() -->methodları varsa getirir yoksa bos json getirir.

        return ResponseEntity.ok(list);
    }

    // !!! DB den direk DTO olarak datami almak istersem ??
    @GetMapping("/query/dto") // http://localhost:8080/students/query/dto?id=1   + GET
    public ResponseEntity<StudentDTO> getStudentDTO(@RequestParam("id") Long id) {
        StudentDTO studentDTO = studentService.findStudentDTOById(id);

        return ResponseEntity.ok(studentDTO);

    }

    // !!! view
    @GetMapping("/welcome") // http://localhost:8080/students/welcome  + GET
    public String welcome(HttpServletRequest request) {

        logger.warn("------------------- Welcome{}", request.getServletPath());
        return "Welcome to Student Controller";
    }



}

