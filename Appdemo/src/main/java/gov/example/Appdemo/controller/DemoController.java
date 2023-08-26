package gov.example.Appdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@Controller
@ResponseBody
public class DemoController
{
	//jdbc 연결
	public static Connection getCon() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/hongik","postgres","1234");
		return con;
	}
	
	//(1) 특정 이름을 가진 학생의 학위 유형 질의
	@GetMapping("/students/degree") //GET Request
	public String namedegree(@RequestParam(value="name") String name) throws SQLException, ClassNotFoundException
	{
		Connection con = getCon();
		String sql = "select S.degree from students S where S.name = ?"; // table 내에서 이름 찾기
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, name);
		ResultSet rs = pst.executeQuery();
		ArrayList<String> degree_list = new ArrayList<String>();

		while (rs.next())
		{
			degree_list.add(rs.getString("degree"));
		}
		
		if (degree_list.size() > 1) // 동명의 학생이 여러 명 존재하는 경우
		{
			return "There are multiple students with the same name. Please provide an email address instead.";
		}
		else if (degree_list.size() == 0) // 학생이 존재하지 않은 경우
		{
			return "No such student";
		}
		
		System.out.println("Argument received : " + name);
		return "< "+name+ " > : < "+degree_list.get(0)+" >";
		
	}
	
	//(2) 특정 이름을 가진 학생의 이메일 질의
	@GetMapping("/students/email") //GET Request
	public String nameemail(@RequestParam(value="name") String name) throws ClassNotFoundException, SQLException
	{
		Connection con = getCon();
		String sql = "select S.email from students S where S.name = ?"; //table에서 이름 조회
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, name);
		ResultSet rs = pst.executeQuery();
		ArrayList<String> email_list = new ArrayList<String>();

		while (rs.next())
		{
			email_list.add(rs.getString("email"));
		}
		
		if (email_list.size() > 1) //동명의 학생이 여러 명 존재하는 경우
		{
			return "There are multiple students with the same name. Please contact the administrator by phone.";
		}
		else if (email_list.size() == 0) //학생이 존재하지 않은 경우
		{
			return "No such student";
		}
		System.out.println("Argument received : " + name);
		return "< "+name+ " > : < "+email_list.get(0)+" >";
	}
	
	//(3) 학위별 학생의 수 반환
	@GetMapping("/students/stat") //GET Request
	public String degreecount(@RequestParam(value="degree") String degree) throws SQLException, ClassNotFoundException
	{
		Connection con = getCon();
		String sql = "select count(S.degree) from students S where S.degree = ?";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, degree);
		ResultSet rs = pst.executeQuery();
		int count = 0;
		while (rs.next())
		{
			count = rs.getInt(1);
		}
		System.out.println("Argument received : " + degree);
		return "Number of "+degree+"'s student : "+count;
	}

	//(4) 신규 학생의 등록
	@PutMapping("/students/register") //PUT Request
	public String newstudent(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "email") String email,
			@RequestParam(value = "graduation") int graduation,
			@RequestParam(value = "degree") String degree) throws ClassNotFoundException, SQLException
	{
		Connection con = getCon();
		
		String sql = "select email from students"; //table에서 primary key인 email 조회
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);
		ArrayList<String> ori_email = new ArrayList<String>();
		int cnt = 0;
		while (rs.next())
		{
			String tmp = rs.getString("email");
			if(tmp.equals(email))
			{
				cnt++; //신규 등록하려고 하는 email과 같은 email이 table에 있을 때
			}
			ori_email.add(tmp);
		}
		
		if (cnt == 0) //insert가 정상적으로 이루어지는 경우
		{
			String sql2 = "insert into students (name,email,graduation,degree) values (?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql2);
			pst.setString(1, name);
			pst.setString(2, email);
			pst.setInt(3, graduation);
			pst.setString(4, degree);
			pst.executeUpdate();
			
			System.out.println("Argument received : " + name);
			return "Registration successful";
		}
		else //동일인을 다시 등록하는 경우
		{
			return "Already registered";
		}
	}
}


