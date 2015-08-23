import java.util.List;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.transaction.annotation.Transactional;

public class LoadCommand implements Command {
  private static final Logger log = LoggerFactory.getLogger(DumpCommand.class);

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  @Transactional
  public void execute(final String[] args) throws Exception {
    namedParameterJdbcTemplate.update("INSERT2 INTO DATA(DepCode, DepJob, Description) VALUES ('1', '2', '3')",
      Collections.<String,String>emptyMap());
  }
}