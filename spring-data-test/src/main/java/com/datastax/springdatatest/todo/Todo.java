package com.datastax.springdatatest.todo;

import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = Todo.TABLENAME)
public class Todo {
    
    // Reuse constants
    public static final String TABLENAME        = "todos";
    public static final String COLUMN_UID       = "uid";
    public static final String COLUMN_TITLE     = "title";
    public static final String COLUMN_COMPLETED = "completed";
    public static final String COLUMN_OFFSET    = "offset";
    
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private UUID uid;
    private String title;
    private boolean completed = false;
    private int offset = 0;
    
    public Todo() {}
            
    public Todo(String title, int offset) {
        this(UUID.randomUUID(), title, false, offset);
    }
    
    public Todo(UUID uid, String title, boolean completed, int offset) {
        super();
        this.uid = uid;
        this.title = title;
        this.completed = completed;
        this.offset = offset;
    }
    
    /**
     * Getter accessor for attribute 'uid'.
     *
     * @return
     *       current value of 'uid'
     */
    public UUID getUid() {
        return uid;
    }
    /**
     * Setter accessor for attribute 'uid'.
     * @param uid
     * 		new value for 'uid '
     */
    public void setUid(UUID uid) {
        this.uid = uid;
    }
    /**
     * Getter accessor for attribute 'title'.
     *
     * @return
     *       current value of 'title'
     */
    public String getTitle() {
        return title;
    }
    /**
     * Setter accessor for attribute 'title'.
     * @param title
     * 		new value for 'title '
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Getter accessor for attribute 'completed'.
     *
     * @return
     *       current value of 'completed'
     */
    public boolean isCompleted() {
        return completed;
    }
    /**
     * Setter accessor for attribute 'completed'.
     * @param completed
     * 		new value for 'completed '
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    /**
     * Getter accessor for attribute 'offset'.
     *
     * @return
     *       current value of 'offset'
     */
    public int getOffset() {
        return offset;
    }
    /**
     * Setter accessor for attribute 'offset'.
     * @param offset
     * 		new value for 'offset '
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    
    
}
    
    
    
