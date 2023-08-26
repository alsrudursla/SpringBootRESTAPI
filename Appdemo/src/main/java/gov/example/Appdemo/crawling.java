package gov.example.Appdemo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class crawling {
	static void data() throws SQLException {
		String hongikurl = "https://apl.hongik.ac.kr/lecture/dbms";
		Document doc = null;
		
		ArrayList<String> name_list = new ArrayList<String>();
		ArrayList<String> email_list = new ArrayList<String>();
		ArrayList<Integer> graduation_list = new ArrayList<Integer>();
		ArrayList<String> degree_list = new ArrayList<String>();
		
		try {
			doc = Jsoup.connect(hongikurl).get();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}		
		
		Elements ele = doc.select("div.jXK9ad-SmKAyb");
		for (Element ee : ele.select("h2")) //degree를 id로 접근
		{
			if (ee.attr("id").equals("h.cwxa41cyxn28_l")) //degree = phd 인 경우
			{
				for(Element e :ee.parent().select("ul:nth-of-type(1)").select("li"))
				{
					String[] arr = e.text().split(",");

					name_list.add(arr[0].trim());
					email_list.add(arr[1].trim());
					graduation_list.add(Integer.parseInt(arr[2].trim()));
					degree_list.add("phd");
				}
			}
			if (ee.attr("id").equals("h.xrleu3h82rn1_l")) //degree = master 인 경우
			{
				for(Element e :ee.parent().select("ul:nth-of-type(2)").select("li"))
				{
					String[] arr = e.text().split(",");
					
					name_list.add(arr[0].trim());
					email_list.add(arr[1].trim());
					graduation_list.add(Integer.parseInt(arr[2].trim()));
					degree_list.add("master");
				}
			}
			if (ee.attr("id").equals("h.kfl1x21a81ct_l")) //degree = undergrad 인 경우
			{
				for(Element e :ee.parent().select("ul:nth-of-type(3)").select("li"))
				{
					String[] arr = e.text().split(",");
					
					name_list.add(arr[0].trim());
					email_list.add(arr[1].trim());
					graduation_list.add(Integer.parseInt(arr[2].trim()));
					degree_list.add("undergrad");
				}
			}
		}
		
		try {
			Class.forName("org.postgresql.Driver");
		}
		catch(ClassNotFoundException e) {
			System.out.println("failed");
			e.printStackTrace();
			return;
		}
		System.out.println("success");
		
		Connection con = null;
		
		try {
			con = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/hongik","postgres","1234");
		}
		catch (SQLException e) {
			System.out.println("failed");
			e.printStackTrace();
			return;
		}
		
		if (con != null) {
			System.out.println(con);
			System.out.println("success");
		}
		else {
			System.out.println("failed");
		}
		
		//query code
		
		//있다고 가정한 경우 기존 table 삭제 (초기화)
		String sql = "DROP TABLE students";
		Statement stdelete = con.createStatement();
		stdelete.execute(sql);
		
		//table 만들기
		String sql2 = "CREATE TABLE students (";
		sql2+="name varchar(100), ";
		sql2+="email varchar(100) primary key, ";
		sql2+="graduation integer, ";
		sql2+="degree varchar(100))";
		Statement stcreate = con.createStatement();
		stcreate.execute(sql2);
		
		for(int i = 0; i< name_list.size();i++)
		{ //table에 크롤링한 정보 insert
			String sql3 = "insert into students(name,email,graduation,degree) values(?,?,?,?)";
			PreparedStatement pst = con.prepareStatement(sql3);
			pst.setString(1, name_list.get(i));
			pst.setString(2, email_list.get(i));
			pst.setInt(3, graduation_list.get(i));
			pst.setString(4, degree_list.get(i));
			pst.executeUpdate();
		}
		
		stdelete.close();
		stcreate.close();
		con.close();	
	}
}
