package it.polimi.tiw.projects.dao;
/*
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import it.polimi.tiw.projects.beans.RegisterBean;
import it.polimi.tiw.projects.utils.ConnectionHandler;

public class RegisterDAO {
    public String registerUser(RegisterBean registerBean)
    {
        String fullName = registerBean.getFullName();
        String email = registerBean.getEmail();
        String userName = registerBean.getUserName();
        String password = registerBean.getPassword();

        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = ConnectionHandler.createConnection();
            String query = "insert into users(SlNo,fullName,Email,userName,password) values (NULL,?,?,?,?)"; //Insert user details into the table 'USERS'
            preparedStatement = con.prepareStatement(query); //Making use of prepared statements here to insert bunch of data
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, userName);
            preparedStatement.setString(4, password);

            int i= preparedStatement.executeUpdate();

            if (i!=0)  //Just to ensure data has been inserted into the database
                return "SUCCESS";
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return "Oops.. Something went wrong there..!";  // On failure, send a message from here.
    }
}
*/