package com.fedor.cs34.discord.bot;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TryStudentExample {

    public static void main(String[] args) throws Exception {
        String jdbcURL = "jdbc:h2:C:/Users/Fedor3/Documents/database/H2/try_student";
        String username = "sa";
        String password = "1234";

        Connection connection = DriverManager.getConnection(jdbcURL, username, password);
        System.out.println("Connected to H2 embedded database.");

        {
            Reader reader = Resources.getResourceAsReader("com/fedor/cs34/discord/bot/try_student.sql");

            ScriptRunner runner = new ScriptRunner(connection);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runner.runScript(reader);
            connection.commit();
            reader.close();
        }

        var studentDAO = new StudentDAO(connection);
        var natalia = new Student("Natalia");
        studentDAO.insert(natalia);
        var students = studentDAO.getAll();
        for (var student : students) {
            System.out.println(student);
        }

        connection.close();
    }
}

class Student {
    int id;
    final String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Student(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Student(id: %d, name: %s)", id, name);
    }
}

class StudentDAO {
    private final Connection connection;

    StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Student student) throws SQLException {
        var statement = connection.prepareStatement("insert into students (name) values(?)",
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, student.name);
        statement.executeUpdate();
        var keys = statement.getGeneratedKeys();
        keys.next();
        student.id = keys.getInt(1);
    }

    public List<Student> getAll() throws SQLException {
        var result = new ArrayList<Student>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM students");

        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var name = resultSet.getString("name");
            result.add(new Student(id, name));
        }
        return result;
    }
}