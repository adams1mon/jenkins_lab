package service;

import domain.Grade;
import domain.Homework;
import domain.Student;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.GradeValidator;
import validation.HomeworkValidator;
import validation.StudentValidator;
import validation.Validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private static final String STUD_TEST_XML = "students_test.xml";
    private static final String HW_TEST_XML = "homeworks_test.xml";

    private static Service service;
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    private void addStudentsToXML() {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student("1", "Rick", 533));
        students.add(new Student("2", "Johnny", 432));
        students.add(new Student("4", "Eve", 633));

        try {
            DocumentBuilder studentXMLBuilder = dbf.newDocumentBuilder();
            Document doc = studentXMLBuilder.newDocument();

            Element rootElement = doc.createElement("Entities");
            doc.appendChild(rootElement);

            for (Student student : students) {
                Element studentElement = doc.createElement("student");
                studentElement.setAttribute("ID", student.getID());

                Element name = doc.createElement("Name");
                name.appendChild(doc.createTextNode(student.getName()));
                Element group = doc.createElement("Group");
                group.appendChild(doc.createTextNode(String.valueOf(student.getGroup())));

                studentElement.appendChild(name);
                studentElement.appendChild(group);
                rootElement.appendChild(studentElement);
            }

            // write to xml file
            TransformerFactory transformerFactory =  TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result =  new StreamResult(new File(STUD_TEST_XML));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHomeworksToXML() {
        ArrayList<Homework> hws = new ArrayList<>();
        hws.add(new Homework("1", "math", 9, 2));
        hws.add(new Homework("2", "linear algebra", 10, 2));
        hws.add(new Homework("3", "gui", 2, 1));

        try {
            DocumentBuilder studentXMLBuilder = dbf.newDocumentBuilder();
            Document doc = studentXMLBuilder.newDocument();

            Element rootElement = doc.createElement("Entities");
            doc.appendChild(rootElement);

            for(Homework homework : hws) {
                Element homeworkElement = doc.createElement("homework");
                homeworkElement.setAttribute("ID", homework.getID());

                Element desc = doc.createElement("Description");
                desc.appendChild(doc.createTextNode(homework.getDescription()));
                Element deadline = doc.createElement("Deadline");
                deadline.appendChild(doc.createTextNode(String.valueOf(homework.getDeadline())));
                Element startline = doc.createElement("Startline");
                startline.appendChild(doc.createTextNode(String.valueOf(homework.getStartline())));

                homeworkElement.appendChild(desc);
                homeworkElement.appendChild(deadline);
                homeworkElement.appendChild(startline);
                rootElement.appendChild(homeworkElement);
            }

            // write to xml file
            TransformerFactory transformerFactory =  TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result =  new StreamResult(new File(HW_TEST_XML));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }


    private static ArrayList<Student> getStudentsFromTestXML() {
        ArrayList<Student> students = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(STUD_TEST_XML)) {

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fis);

            // students from the xml
            NodeList student_nodes = doc.getElementsByTagName("student");

            for (int i = 0; i < student_nodes.getLength(); ++i) {
                Node student_node = student_nodes.item(i);

                if (student_node.getNodeType() == Node.ELEMENT_NODE) {
                    Element student_element = (Element) student_node;

                    // id attribute
                    String id = student_element.getAttribute("ID");

                    // Name + Group
                    String name = student_element.getElementsByTagName("Name").item(0).getTextContent();
                    int group = Integer.parseInt(student_element.getElementsByTagName("Group").item(0).getTextContent());

                    // add to the array
                    students.add(new Student(id, name, group));
                }
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
        return students;
    }

    private static ArrayList<Homework> getHomeworksFromTestXML() {
        ArrayList<Homework> homeworks = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(HW_TEST_XML)) {

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fis);

            // homeworks from the xml
            NodeList hw_nodes = doc.getElementsByTagName("homework");

            for (int i = 0; i < hw_nodes.getLength(); ++i) {
                Node hw_node = hw_nodes.item(i);

                if (hw_node.getNodeType() == Node.ELEMENT_NODE) {
                    Element hw_element = (Element) hw_node;

                    // id attribute
                    String id = hw_element.getAttribute("ID");

                    // Description + Deadline + Startline
                    String desc = hw_element.getElementsByTagName("Description").item(0).getTextContent();
                    int deadline = Integer.parseInt(hw_element.getElementsByTagName("Deadline").item(0).getTextContent());
                    int startline = Integer.parseInt(hw_element.getElementsByTagName("Startline").item(0).getTextContent());

                    // add to the array
                    homeworks.add(new Homework(id, desc, deadline, startline));
                }
            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
        return homeworks;
    }

    @BeforeEach
    private void setup(){

        addStudentsToXML();
        addHomeworksToXML();

        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(studentValidator, STUD_TEST_XML);
        HomeworkXMLRepository fileRepository2 = new HomeworkXMLRepository(homeworkValidator, HW_TEST_XML);
        GradeXMLRepository fileRepository3 = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @AfterEach
    private void deleteTestStudentsXML() {
        File studXml = new File(STUD_TEST_XML);
        File hwXml = new File(HW_TEST_XML);
        studXml.delete();
        hwXml.delete();
    }

    @Test
    void findAllStudents() {
        Collection<Student> testStudents = getStudentsFromTestXML();
        Iterable<Student> students = service.findAllStudents();

        Iterator<Student> studentIterator = students.iterator();
        Iterator<Student> testStudentIterator = testStudents.iterator();
        while (studentIterator.hasNext() && testStudentIterator.hasNext()) {

            Student student = studentIterator.next();
            Student testStudent = testStudentIterator.next();

            assertEquals(student, testStudent);
        }
    }

    private static Stream<Arguments> studentsToSaveGenerator() {
        return Stream.of(
                Arguments.of(new Student("55", "Kate", 546), true),
                Arguments.of(new Student("999", "Andrew", 555), true),
                // shouldn't save students with empty arguments
                Arguments.of(new Student("12", "", 4546), false),
                Arguments.of(new Student("", "Betty", 4546), false),
                Arguments.of(new Student(null, "Null", 234), false)
        );
    }

    @ParameterizedTest
    @MethodSource("studentsToSaveGenerator")
    void saveStudent(Student student, Boolean expected) {
        System.out.println(student);
        int saved = service.saveStudent(student.getID(), student.getName(), student.getGroup());
        int expectedSaved = expected ? 1 : 0;
        assertEquals(expectedSaved, saved);
        Collection<Student> students = getStudentsFromTestXML();
        assertEquals(expected, students.contains(student));
    }

    private static Stream<Arguments> existingStudentsToDeleteGenerator() {
        return Stream.of(
                // just the 'id' matters when saving, deleting, updating
                Arguments.of(new Student("2", "aaa", 0), false)
        );
    }

    @ParameterizedTest
    @MethodSource("existingStudentsToDeleteGenerator")
    void deleteStudents_ShouldDeleteExistingStudents(Student student, Boolean expected) {
        int deleted = service.deleteStudent(student.getID());
        assertTrue(deleted == 1);
        Collection<Student> students = getStudentsFromTestXML();
        assertFalse(students.contains(student));
    }

    @Test
    void updateStudent_ShouldUpdateExistingStudent() {
        ArrayList<Student> students = getStudentsFromTestXML();
        Student student = students.get(0);
        System.out.println(student);

        student.setName("UPDATED");
        student.setGroup(5555);

        int updated = service.updateStudent(student.getID(), student.getName(), student.getGroup());
        assertTrue(updated == 1);

        students = getStudentsFromTestXML();
        assertTrue(students.contains(student));
    }

    @Test
    void deleteHomework_ShouldDeleteExistingHomework() {
        ArrayList<Homework> homeworks = getHomeworksFromTestXML();
        Homework homework = homeworks.get(0);
        int deleted = service.deleteHomework(homework.getID());
        assertTrue(deleted == 1);

        homeworks = getHomeworksFromTestXML();
        assertFalse(homeworks.contains(homework));
    }
}