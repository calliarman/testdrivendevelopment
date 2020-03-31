package order.dao.impl;

import java.io.FileReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.h2.Driver;
import org.h2.tools.RunScript;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseDBUnitTestForJPADao {

	protected static EntityManagerFactory EMF = null;
	protected static IDatabaseConnection CONN = null;
	
	protected EntityManager entityManager = null;
	
	@BeforeClass
	public static void setupTestClass() throws Exception {
		
		Properties dbProps = new Properties();
		dbProps.put("user", DBInfo.USER);
		dbProps.put("password", DBInfo.PASSWORD);
		
		Connection jdbcConn = Driver.load().connect(DBInfo.URL, dbProps);
		CONN = new DatabaseConnection(jdbcConn);
		RunScript.execute(CONN.getConnection(), new FileReader("tabledef/b2csite.ddl.sql"));
		
		final Map<Object, Object> props = new HashMap<>();
		props.put("javax.persistence.jdbc.url", DBInfo.URL);
		//props.put("hibernate.hbm2ddl.auto", "create-drop");
		EMF = Persistence
				.createEntityManagerFactory("orderPersistenceUnit", props);
		
		
	}
	
	@AfterClass
	public static void teardownTestClass() throws Exception {
		
		try {
			if (EMF != null) {
				if (EMF.isOpen()) {
					EMF.close();
				}
				EMF = null;
			}
		}
		finally {
			if (CONN != null) {
				CONN.close();
				CONN = null;
			}
		}
	}
	
	@Before
	public void baseSetup() throws Exception {
		entityManager = EMF.createEntityManager();
	}
	
	@After
	public void baseTeardown() throws Exception {
		if (entityManager != null) {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
			entityManager = null;
		}
	}
}
