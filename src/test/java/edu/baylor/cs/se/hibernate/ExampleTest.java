package edu.baylor.cs.se.hibernate;

import edu.baylor.cs.se.hibernate.model.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExampleTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    //simple test
    public void demoTest(){
        Teacher teacher = new Teacher();
        teacher.setEmail("email@email.com");
        teacher.setFirstName("John");
        teacher.setLastName("Roe");
        entityManager.persist(teacher);
        Teacher dbTeacher = (Teacher)entityManager.getEntityManager().createQuery("SELECT t FROM Teacher t WHERE t.firstName LIKE 'John' ").getResultList().get(0);
        assertThat(teacher.getFirstName()).isEqualToIgnoringCase(dbTeacher.getFirstName());
    }

    @Test
    //tests that email validation works
    public void anotherDemoTest(){
        Teacher teacher = new Teacher();
        teacher.setEmail("hahaWrongEmail");
        teacher.setFirstName("John");
        teacher.setLastName("Roe");
        assertThatThrownBy(() -> { entityManager.persist(teacher); }).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("must contain valid email address");
    }

    @Test
    //tests that telephone number validation works
    public void yetAnotherDemoTest(){
        // dummy test
        // test number is neither a sequence of digits nor a number in the format (xxx) xxx-xxxx
        Teacher teacher = new Teacher();
        teacher.setEmail("test@email.com");
        teacher.setFirstName("John");
        teacher.setLastName("Roe");
        teacher.setTelephoneNumber("hahaWrongNumber");
        assertThatThrownBy(() -> { entityManager.persist(teacher); }).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("number must be at least 10 digits or in the format (xxx) xxx-xxxx");

        // test appropriate length if only numbers
        // test number does not have appropriate length
        Teacher teacher2 = new Teacher();
        teacher2.setEmail("test@email.com");
        teacher2.setFirstName("John");
        teacher2.setLastName("Roe");
        teacher2.setTelephoneNumber("1234");
        assertThatThrownBy(() -> { entityManager.persist(teacher2); }).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("number must be at least 10 digits or in the format (xxx) xxx-xxxx");

        // test appropriate US format (xxx) xxx-xxxx
        // test number is missing the parenthesis
        Teacher teacher3 = new Teacher();
        teacher3.setEmail("test@email.com");
        teacher3.setFirstName("John");
        teacher3.setLastName("Roe");
        teacher3.setTelephoneNumber("123 456-7890");
        assertThatThrownBy(() -> { entityManager.persist(teacher3); }).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("number must be at least 10 digits or in the format (xxx) xxx-xxxx");
    }
}
