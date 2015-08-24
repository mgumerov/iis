import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

public class LoadCommand implements Command {
  private static final Logger log = LoggerFactory.getLogger(DumpCommand.class);

  private JdbcTemplate jdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  @Transactional
  public void execute(final String[] args) throws Exception {
    load(args[0]);
  }

  private class Record {
    private final NatKey natKey;
    public NatKey getNatKey() { return natKey; }
    private final String description;
    public String getDescription() { return description; }

    public Record(final NatKey natKey, final String description) {
      this.natKey = natKey;
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

    //For batches we have to store separate lists
    final Collection<Integer> deletes = new ArrayList<Integer>(); //печаль... но не array же заводить сразу с запасом, плюс счетчик? некрасиво
    final Map<Integer,Record> updates = new HashMap<Integer,Record>(); //и тут тоже неохота новую структуру заводить

    jdbcTemplate.query("SELECT DepCode, DepJob, ID FROM Data",
                                     (Object[])null,
                                     (ResultSet rs) -> { //RowCallbackHandler
                                       final NatKey key = new NatKey(rs.getString(1), rs.getString(2));
                                       final Integer id = rs.getInt(3);
                                       final Record item = newData.get(key); //map's values are non-null
                                       if (item != null)
                                         updates.put(id, item);
                                       else
                                         deletes.add(id);
                                       newData.remove(key);
                                     });

    final Collection<Record> inserts = newData.values(); //осталось то, чего не было в Ѕƒ

    jdbcTemplate.batchUpdate("DELETE FROM DATA WHERE ID=?", deletes, 2,
                             (final PreparedStatement ps, final Integer value) -> ps.setInt(1, value.intValue()) );

    jdbcTemplate.batchUpdate("UPDATE DATA SET Description = ? where ID = ?", updates.entrySet(), 2,
                             (final PreparedStatement ps, final Map.Entry<Integer,Record> entry) -> {
                                 ps.setInt(2, entry.getKey());
                                 ps.setString(1, entry.getValue().getDescription());
                             });

    jdbcTemplate.batchUpdate("INSERT INTO DATA(DepCode, DepJob, Description) VALUES(?,?,?)", inserts, 2,
                             (final PreparedStatement ps, final Record record) -> {
                                 ps.setString(1, record.getNatKey().getDepCode());
                                 ps.setString(2, record.getNatKey().getDepJob());
                                 ps.setString(3, record.getDescription());
                             });
  }
}