package com.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DataBaseConnector {
    private String username= "SYSDBA";
    private String password = "1331491212yGo$homi";
    private String url = "jdbc:oracle:thin:@localhost:1521:ORCLCDB";
    private Connection connection;


    private DataBaseConnectionObject connect(){
        DataBaseConnectionObject connectionObject = new DataBaseConnectionObject();
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, username, password);
            if(connection != null){
                connectionObject.success = true;
            }
        } catch (Exception e){
            connectionObject.success = false;
            connectionObject.message = e.getMessage();
        }
        return connectionObject;
    }

    private DataBaseConnectionObject disconnect(){
        DataBaseConnectionObject connectionObject = new DataBaseConnectionObject();
        try{
            connection.close();
            connectionObject.success = true;
        }catch (Exception e){
            connectionObject.success = false;
            connectionObject.message = e.getMessage();
        }
        return connectionObject;
    }

    public String get(String sql){
        Map<String ,Object> result = new HashMap<>();
        DataBaseConnectionObject connectionObject = connect();
        try{
            if (connectionObject.success){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()){
                    for(int col = 1; col <= columnCount; col++){
                        result.put(metaData.getColumnName(col), resultSet.getString(col));
                    }
                }
                DataBaseConnectionObject disconnectObject = disconnect();
            }
        }catch (Exception e){
            result.put("error", e.getMessage());
        }
        return result.toString();
    }

    public Map<String, Object> patch(String sql){
        Map<String ,Object> result = new HashMap<>();

        try{
            connect();
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(sql);
            if(affectedRows > 0){
                result.put("success", true);
                result.put("code", "success");
            }

        }catch(Exception e){
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    public Boolean insert(String sql){
        boolean success = false;
        try{
            DataBaseConnectionObject connectionObject = connect();
            success = connectionObject.success;
            System.out.println(connectionObject.message);
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(sql);
            if(rowsAffected > 0){
                success = true;
            }
        }catch(Exception ignored){
        }
        return success;
    }

    public String createAccountNumber(){
        int accountNumberOffSet = 3;
        int accountNumberLength = 16;
        String sql = "get last account number";
        String result = this.get(sql);
        Gson gson = new Gson();
        Map<String, Object> res = gson.fromJson(result, new TypeToken<HashMap<String, Object>>() {}.getType());
        double lastAccountNumber = Double.parseDouble( (String) res.get("accountNumber")) + accountNumberOffSet;
        return  String.valueOf(lastAccountNumber).substring(0, accountNumberLength);
    }
}
