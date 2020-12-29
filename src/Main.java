package com.company;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static String sqlInsert = "insert into Teacher values(?,?)";
    static String sqlUpdate = "update Teacher set salary=? where name=?";
    static String sqlDelete = "delete from Teacher where name=?";
    static String sqlTrigger1 = "CREATE TRIGGER Insert_Sal" +
            " BEFORE INSERT ON Teacher" +
            " FOR EACH ROW" +
            " BEGIN" +
            " IF(NEW.salary<4000) THEN " +
            " INSERT INTO sallog(name,old,new) " +
            " VALUES(NEW.name,NEW.salary,4000);" +
            " SET NEW.salary=4000;" +
            " END IF;" +
            " END;";
    static String sqlTrigger2 = "CREATE TRIGGER Update_Sal" +
            " BEFORE UPDATE ON Teacher" +
            " FOR EACH ROW" +
            " BEGIN" +
            " IF(NEW.salary<4000) THEN " +
            " INSERT INTO sallog(name,old,new) " +
            " VALUES(NEW.name,NEW.salary,4000);" +
            " SET NEW.salary=4000;" +
            " END IF;" +
            " END;";
    static String url = "jdbc:mysql://127.0.0.1:3306/mydata?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";
    static Connection con;
    public static void main(String[] args ){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接数据库
            con= DriverManager.getConnection(url, "root", "");
            System.out.println("连接数据库成功");
            //第一次运行时生成表Teacher,生成成功后不必运行
            firstCreateTable();
            //设置触发器 也是只设置一次即可
            setTrigger(sqlTrigger1);
            setTrigger(sqlTrigger2);
            //开始交互
            run();
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        catch (SQLException e) { e.printStackTrace(); }
    }
    //第一次运行时生成表Teacher和salLog,生成成功后不必运行
    public static void firstCreateTable() throws SQLException {
        System.out.println("正在创建表 Teacher...");
        if(creatTableTeacher())System.out.println("创建成功!");
        else System.out.println("创建失败!");
        System.out.println("正在创建表 salLog...");
        if(creatTableSalLog())System.out.println("创建成功!");
        else System.out.println("创建失败!");
    }
    //创建表 Teacher
    public static boolean creatTableTeacher() throws SQLException {
        Statement st=con.createStatement();
        String sqlCreatTableTeacher = "CREATE TABLE Teacher("
                + "name varchar(10) not null,"
                + "salary int(4) not null"
                + ")charset=utf8;";
        if(0==st.executeLargeUpdate(sqlCreatTableTeacher))return true;
        return false;
    }
    //创建表 salLog
    public static boolean creatTableSalLog() throws SQLException {
        Statement st=con.createStatement();
        String sqlCreatTableTeacher = "CREATE TABLE salLog("
                + "name varchar(10) not null,"
                + "old int(4) not null,"
                + "new int(4) not null"
                + ")charset=utf8;";
        if(0==st.executeLargeUpdate(sqlCreatTableTeacher))return true;
        return false;
    }
    //执行SQL插入操作
    public static boolean insert(Teacher teacher) throws SQLException {
        PreparedStatement pst = con.prepareStatement(sqlInsert);//用来执行SQL语句查询,对sql语句进行预编译处理
        pst.setString(1,teacher.getName());
        pst.setInt(2,teacher.getSalary());
        if(pst.executeUpdate()>0)return true;
        return false;
    }
    //执行SQL更新操作
    public static boolean update(Teacher teacher) throws SQLException {
        PreparedStatement pst = con.prepareStatement(sqlUpdate);//用来执行SQL语句查询,对sql语句进行预编译处理
        pst.setString(2,teacher.getName());
        pst.setInt(1,teacher.getSalary());
        if(pst.executeUpdate()>0)return true;
        return false;
    }
    //执行SQL删除操作
    public static boolean delete(String str) throws SQLException {
        PreparedStatement pst = con.prepareStatement(sqlDelete);//用来执行SQL语句查询,对sql语句进行预编译处理
        pst.setString(1,str);
        if(pst.executeUpdate()>0)return true;
        return false;
    }
    //执行SQL查询操作 返回结果列表
    public static ArrayList<Teacher> search(String sql) throws SQLException {
        ArrayList<Teacher> list = new ArrayList<>();
        PreparedStatement pst  = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while(rs.next()) list.add(new Teacher(rs.getString(1),rs.getInt(2)));
        return list;
    }
    //设置触发器
    public static void setTrigger(String sql) throws SQLException {
        PreparedStatement  pst = con.prepareStatement(sql);
        pst.execute();
    }
    //交互入口
    public static void run() throws SQLException {
        System.out.println("请输入操作值 0:INSERT 1:UPDATE 2:DELETE 3:查看表 else:QUIT");
        Scanner input=new Scanner(System.in);
        Teacher teacher = new Teacher();
        ArrayList<Teacher> list;
        boolean flag=true;
        while(flag){
            switch (input.nextInt()){
                case 0:
                    System.out.print("\n请输入姓名: ");
                    teacher.setName(input.next());
                    System.out.print("\n请输入工资: ");
                    teacher.setSalary(input.nextInt());
                    if(insert(teacher))System.out.println("插入成功");
                    else System.out.println("插入失败");
                    break;
                case 1:
                    System.out.print("\n请输入姓名: ");
                    teacher.setName(input.next());
                    System.out.print("\n请输入工资: ");
                    teacher.setSalary(input.nextInt());
                    if(update(teacher))System.out.println("更新成功");
                    else System.out.println("更新失败");
                    break;
                case 2:
                    System.out.print("\n请输入姓名: ");
                    if(delete(input.next()))System.out.println("删除成功");
                    else System.out.println("删除失败");
                    break;
                case 3:
                    list = search("select * from Teacher");
                    System.out.println("Teacher表如下:");
                    for(int i=0;i<list.size();++i)System.out.println(list.get(i).getName()+" "+list.get(i).getSalary());
                    list = search("select * from salLog");
                    System.out.println("salLog表如下:");
                    for(int i=0;i<list.size();++i)System.out.println(list.get(i).getName()+" "+list.get(i).getSalary()+" 4000");
                    break;
                default: flag=false;break;
            }
        }
    }
}
