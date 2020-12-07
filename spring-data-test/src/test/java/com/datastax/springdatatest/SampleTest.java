package com.datastax.springdatatest;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.springdatatest.conf.SpringDataConfig;
import com.datastax.springdatatest.todo.Todo;
import com.datastax.springdatatest.todo.TodoRepository;

@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={SpringDataConfig.class, TodoRepository.class})
@TestPropertySource(locations="/application.properties")
public class SampleTest {

    @Autowired
    private CqlSession cqlSession;
    
    @Autowired
    private CqlTemplate cqlTemplate;
    
    @Autowired
    private TodoRepository todoRepository;
    
    @Test
    public void test() {
        Assertions.assertFalse(cqlSession.isClosed());
        /* Cql, no prepare statements.*/
        String cql = "INSERT INTO todos (uid, title,completed,offset) VALUES (UUID(), 'This is a task without prepared statement', true, 1)";
        cqlTemplate.execute(cql);
        todoRepository.save_prepared(new Todo("this is a task with old prepared statement", 1));
        todoRepository.save(new Todo("this is a task with new preparedStatement", 2));
        List<Todo> listTodo = todoRepository.findAll();
        for (Todo todo : listTodo) {
           System.out.println(todo.getTitle());
        }
    }
}
