package de.fynn.randomSitzordnung.database;

import de.fynn.randomSitzordnung.Student;
import de.fynn.randomSitzordnung.flags.Flag;
import de.fynn.randomSitzordnung.flags.FlagFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {

    private PreparedStatement preparedStatement;
    private Connection connection;

    private String select;
    private String from;
    private String where;

    public void connect(String... dbInfo) throws SQLException{
        connection = DriverManager.getConnection("jdbc:mysql://"+dbInfo[0]+":"+dbInfo[1]+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",dbInfo[2],dbInfo[3]);
        select = dbInfo[4];
        from = dbInfo[5];
        where = dbInfo[6];
    }

    public List<Student> getStudentsFromDB() throws SQLException{
        preparedStatement = connection.prepareStatement("SELECT "+select+" FROM "+from+(where.equals("")?"":" WHERE ")+where+";");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Student> students = new ArrayList<>();
        while(resultSet.next()){
            String line = resultSet.getString(1);
            Flag flag = null;
            if(line.contains("-r")){
                String[] strings = line.split("-r");
                flag = FlagFactory.createFlag("fixRow",strings[1]);
                line = strings[0];
            }else if(line.contains("-p")){
                String[] strings = line.split("-p");
                String[] values = strings[1].split(",");
                flag = FlagFactory.createFlag("fixPos",values[0],values[1]);
                line = strings[0];
            }
            students.add(flag==null?new Student(line):new Student(line,flag));
        }
        return students;
    }

}
