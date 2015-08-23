import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class Printer  {
  private static final Logger log = LoggerFactory.getLogger(Printer.class);

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  public void printout() {
    System.out.println("Resulting SELECT ID, DepCode, DepJob, Description FROM Data = ");
    namedParameterJdbcTemplate.query("SELECT ID, DepCode, DepJob, Description FROM Data",
      Collections.<String,String>emptyMap(),
      (ResultSet rs) -> { //RowCallbackHandler
        System.out.println(rs.getString(1) + " / " + rs.getString(2) + " / " + rs.getString(3) + " / " + rs.getString(4));
      });
  }
}