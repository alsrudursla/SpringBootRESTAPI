package gov.example.Appdemo;

import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppdemoApplication {

	public static void main(String[] args) throws SQLException {
		
		//데이터베이스 만들기
		crawling c = new crawling();
		c.data();

		//만든 데이터베이스 기반 질의 처리
		SpringApplication.run(AppdemoApplication.class, args);
	}

}
