import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
    load(args[0]);
  }

  private class Record {
    private final NatKey key;
    public NatKey getKey() { return key; }
    private final String description;
    public String getDescription() { return description; }

    public Record(final NatKey key, final String description) {
      this.key = key;
      this.description = description;
    }
  }

  private void load(final String filename) throws Exception {
    log.info("Loading from: " + filename);
    final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    final Document doc = docBuilder.parse(filename);

    final Element nDataset = (Element)doc.getChildNodes().item(0);

    final Map<NatKey, Record> newData = new HashMap<NatKey, Record>();

    {
      Node nRow = nDataset.getFirstChild();
      while (nRow != null) {
        if (nRow.getNodeType() == Node.ELEMENT_NODE) {
          final Element element = (Element)nRow;
          final NatKey key = new NatKey(Utils.nullIfEmpty(element.getAttribute("DepCode")), Utils.nullIfEmpty(element.getAttribute("DepJob")));
          if (newData.containsKey(key))
            throw new Exception("Duplicate natural key");
          else
            newData.put(key, new Record(key, Utils.nullIfEmpty(element.getAttribute("Description"))));
        }
        nRow = nRow.getNextSibling();
      }
    }

    namedParameterJdbcTemplate.query("SELECT DepCode, DepJob, ID FROM Data",
                                     Collections.<String,String>emptyMap(),
                                     (ResultSet rs) -> { //RowCallbackHandler
                                       final NatKey key = new NatKey(rs.getString(1), rs.getString(2));
                                       final int id = rs.getInt(3);
                                       if (newData.containsKey(key)) {
                                         log.debug("Updating: " + key);
                                         final Map<String, String> params = new HashMap<String, String>();
                                         params.put("id", String.valueOf(id));
                                         params.put("descr", newData.get(key).getDescription());
                                         namedParameterJdbcTemplate.update("UPDATE DATA SET Description = :descr where ID = :id", params);
                                       } else {
                                         log.debug("Deleting: " + key);
                                         namedParameterJdbcTemplate.update("DELETE FROM DATA where ID = :id", Collections.<String,String>singletonMap("id", String.valueOf(id)));
                                       }
                                       newData.remove(key);
                                     });


    {    
      final Map<String, String> params = new HashMap<String, String>();
      for (final Record record : newData.values()) {
        params.put("dc", record.getKey().getDepCode());
        params.put("dj", record.getKey().getDepJob());
        params.put("dd", record.getDescription());
        namedParameterJdbcTemplate.update("INSERT INTO DATA(DepCode, DepJob, Description) VALUES(:dc,:dj,:dd)", params);
      }
    }
  }
}