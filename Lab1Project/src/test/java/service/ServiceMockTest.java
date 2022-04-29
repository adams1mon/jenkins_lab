package service;

import domain.Homework;
import domain.Student;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ServiceMockTest {

    private static Service service;
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    @Mock private StudentXMLRepository studentXmlRepo;
    @Mock private HomeworkXMLRepository homeworkXmlRepo;
    @Mock private GradeXMLRepository gradeXmlRepo;

    private ArrayList<Student> students;

    @BeforeEach
    private void createMock() {
        // init mock objects in this class
        MockitoAnnotations.initMocks(this);
        students = new ArrayList<>();
        students.add(new Student("1", "Gary", 245));
        students.add(new Student("2", "Mary", 456));
        students.add(new Student("3", "Diana", 456));

        service = new Service(studentXmlRepo, homeworkXmlRepo, gradeXmlRepo);
    }

    @Test
    void findAllStudents() {
        when(studentXmlRepo.findAll()).thenReturn(students);
        Iterable<Student> studentsIterable = service.findAllStudents();

        // verify that the 'findAllStudents' method called findAll on studentXmlRepo
        Mockito.verify(studentXmlRepo).findAll();

        // verify the content + length of the lists
        Iterator<Student> studentIterator = studentsIterable.iterator();
        Iterator<Student> mockStudentIterator = students.iterator();

        while (studentIterator.hasNext() && mockStudentIterator.hasNext()) {
           Student st1 = studentIterator.next();
           Student st2 = mockStudentIterator.next();
           assertEquals(st1, st2);
        }

        // if one list is empty, both should be empty --> have the same length
        assertEquals(studentIterator.hasNext(), mockStudentIterator.hasNext());
    }

    @Test
    void deleteStudent_shouldReturn0() {
        // simulate failing delete
        when(studentXmlRepo.delete(anyString())).thenReturn(null);
        assertTrue(0 == service.deleteStudent(anyString()));
    }

    @Test
    void updateStudent_shouldReturn1() {
        // simulate successful update
        Student student = students.get(0);
        student.setName("UPDATED");
        student.setGroup(5555);

        // returning null means successful update
        when(studentXmlRepo.update(student)).thenReturn(null);

        int updated = service.updateStudent(student.getID(), student.getName(), student.getGroup());
        assertEquals(1, updated);
    }

    @Test
    void saveHomework_shouldReturn0() {
        // simulate failing save (entity with given id already exists, entity gets returned)
        Homework homework = new Homework("1", "desc", 3, 4);
        when(homeworkXmlRepo.save(homework)).thenReturn(homework);

        int result = service.saveHomework(homework.getID(),
                homework.getDescription(),
                homework.getDeadline(),
                homework.getStartline());

        assertEquals(0, result);
    }
}