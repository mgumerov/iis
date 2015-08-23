import java.util.List;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;


public class DumpCommand implements Command {
  private static final Logger log = LoggerFactory.getLogger(DumpCommand.class);

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public void execute(final String[] args) throws Exception {
    final String filename = args[0];

    final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    final Document doc = docBuilder.newDocument();
    final Element rootElement = doc.createElement("dataset");
    doc.appendChild(rootElement);

    //Можно было бы разделить логику извлечения данных и логику команды, но логика извлечения очень проста
    //и не хочется новый класс модели ради нее городить.
    namedParameterJdbcTemplate.query("SELECT DepCode, DepJob, Description FROM Data",
      Collections.<String,String>emptyMap(),
      (ResultSet rs) -> { //RowCallbackHandler
        final Element datarow = doc.createElement("row");
        datarow.setAttribute("DepCode", rs.getString(1));
        datarow.setAttribute("DepJob", rs.getString(2));
        datarow.setAttribute("Description", rs.getString(3));
        rootElement.appendChild(datarow);
      });

    final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    final Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    final DOMSource source = new DOMSource(doc);
    final StreamResult result = new StreamResult(new java.io.File(filename));
    transformer.transform(source, result);
  }
}