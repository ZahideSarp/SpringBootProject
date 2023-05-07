package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;//service class'ı repository ile iletisime gececek bir nevi bagimli

    public List<Student> getAll() {
        return studentRepository.findAll();
        //findAll()'u repository'e git bu methodu calıstır gel diyorum.
        // findAll()'u donen degeri list data turunde student'ları bulup getirecek.Hazır method
        //findAll()'u olmasaydı biz kendimiz repository'de bir method yazacaktık ve o methodu buraya cagıracaktık
        //ama buna gerek kalmadı cunku burası springJpa'dan extends edildi.hazır java methodlarını kullanabiliyorum bu yuzden

    }
    public void createStudent(Student student){
        if(studentRepository.existsByEmail(student.getEmail())){
            throw new ConflictException("Email is alredy exist !!");
        }
        studentRepository.save(student);
    }

    public Student findStudent(Long id){//businnes logic katı kontrollerimi burada gerceklestiriyorum
       //id uniq oldugu icin var mı yokmu diye kontrol ediyoruz.
      //  findById() optional'dir.eger varsa Student objesini dondurur yoksa bos obje dondurur yani exception alamamızın onune gecer
        //sonra da method chance yapip orElseThrow() methodu ile custom exception olusturuyoruz ve exception atıyoruz
        return studentRepository.findById(id).orElseThrow(///findByID studentRepository'i injection'indan aliyoruz.
                ()->new ResourceNotFoundException("Student not found with id:" +id));
    }


    public void deleteStudent(Long id) {
        Student student=findStudent(id);//findStudent() methodu yukarı da varsa id ye gore bulup veriyor yoksa exception ayıtordu
        //tekrar kontrol etmek yerine direk o methodu kullanıyorum
        studentRepository.delete(student);
    }

    public void updateStudent(Long id, StudentDTO studentDTO) {
        // email DB de var mi ??
        boolean emailExist =  studentRepository.existsByEmail(studentDTO.getEmail());

        //istenilen id de Student var mi ???
        Student student = findStudent(id);

        if(emailExist && ! studentDTO.getEmail().equals(student.getEmail())) {
            throw new ConflictException("Email is already exist");
        }
        /*
               senaryo 1 : kendi email mrc , mrc girdim         ( update olur )
            ** senaryo 2 : kendi email mrc, ahmt girdim ve DB de zaten var     ( exception )
               senaryo 3 : kendi email mrc, mhmt ve DB de yok     (update)
         */

        student.setName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setGrade(studentDTO.getGrade());
        student.setEmail(studentDTO.getEmail());
        student.setPhoneNumber(studentDTO.getPhoneNumber());

        studentRepository.save(student);

    }

    public Page<Student> getAllWithPage(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public List<Student> findStudent(String lastName){
        return studentRepository.findByLastName(lastName);
    }

    public List<Student> findAllEqualsGrade(Integer grade) {

        return  studentRepository.findAllEqualsGrade(grade);
    }

    public StudentDTO findStudentDTOById(Long id) {

        return studentRepository.findStudentDTOById(id).orElseThrow(()->
                new ResourceNotFoundException("Student not found with id : " + id));
    }


}
