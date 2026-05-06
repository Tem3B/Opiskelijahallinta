package com.example.application;

import com.example.application.data.StudentsRepository;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.ApplicationDataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.autoconfigure.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("styles.css")
@NpmPackage(value = "@fontsource/arvo", version = "4.5.0")
@EnableConfigurationProperties(SqlInitializationProperties.class)
@Theme(value = "opiskelijahallintajrjestelm", variant = Lumo.DARK)
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationDataSourceScriptDatabaseInitializer customInitializer(DataSource dataSource,
            SqlInitializationProperties properties, StudentsRepository repository) {
        // Only run schema.sql/data.sql when the DB is empty
        return new ApplicationDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                return (repository.count() == 0L) && super.initializeDatabase();
            }
        };
    }

    @Bean
    org.springframework.boot.CommandLineRunner ensureTeacherDepartmentColumn(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                if (!columnExists(connection, "TEACHERS", "DEPARTMENT")) {
                    statement.execute("alter table teachers add column department varchar(255)");
                }
                statement.execute("update teachers set department = 'General' where department is null or department = ''");
            }
        };
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }
}
