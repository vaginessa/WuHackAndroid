package hackwu.wuhack;

import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Paul on 29.06.2015.
 */
public class HTMLModel
{

    private static Node getElementByID(NodeList nl, String id)
    {
        Node node = null;

        for (int i = 0; i < nl.getLength(); i++)
        {
            Node n = nl.item(i);

            if (n.hasAttributes())
            {
                Node idNode = n.getAttributes().getNamedItem("id");

                if (idNode != null && idNode.getTextContent().equals(id))
                {
                    node = n;
                    System.out.println("Found! " + n.getNodeName() + " " + n.getNodeValue());
                    break;
                }
            }

            if (n.hasChildNodes())
            {
                node = getElementByID(n.getChildNodes(), id);
            }

        }
        return node;
    }


    public static final String SCHEDULE_TEMPLATE = DAL.getFileContent("schedule_template.html");

    public static Document convertToHTMLv3(final WebView wv, final Lesson[][] schedule, final String title, final int calweek)
    {

        wv.loadData(SCHEDULE_TEMPLATE, "text/html; charset=UTF-8", null);

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                convert(wv.getDocument(), schedule, title, calweek);
                wv.reload();
            }
        });

        return wv.getDocument();
    }

    private static void convert(Document d, Lesson[][] schedule, String title, int calweek)
    {
        //WebBrowserTest.printAllNodes(d);

        d.getElementById("klasse").setTextContent(title);

        String[] days = getDays(calweek);
        d.getElementById("date").setTextContent(days[0] + " - " + days[1]);

        HTMLTableElement table = (HTMLTableElement) d.getElementById("schedule");
        HTMLTableSectionElement tableBody = (HTMLTableSectionElement) table.getTBodies().item(0);
        HTMLCollection rows = tableBody.getRows();

        for (int i = 1; i < rows.getLength(); i++)
        {
            HTMLTableRowElement row = (HTMLTableRowElement) rows.item(i);
            HTMLCollection cells = row.getCells();

            for (int j = 1; j < cells.getLength(); j++)
            {
                HTMLTableElement lessonTable = (HTMLTableElement) cells.item(j).getChildNodes().item(1);
                //HTMLCollection lessonRows = lessonTable.getRows();
                lessonTable.setTextContent("");

                for (int k = 0; k < lessonTable.getRows().getLength(); k++)
                {
                    lessonTable.removeChild(lessonTable.getChildNodes().item(k));

                }

                if (schedule[j - 1][i - 1] != null)
                {
//                    HTMLTableRowElement subjectRow = (HTMLTableRowElement) lessonRows.item(0);
//                    HTMLTableCellElement subjectCell = (HTMLTableCellElement) subjectRow.getCells().item(0);
//                    subjectCell.setTextContent("");
//
//                    for (int k = 0; k < lessonRows.getLength() - 1; k++) {
//                        lessonTable.deleteRow(1);
//                    }

                    Lesson lesson = schedule[j - 1][i - 1];




                    // TODO: rowspan




//          int repeat = 0;
//          boolean end = false;
//          while(!end)
//          {
//            repeat++;
//
//            if(schedule[j-1][i-1+repeat] != null)
//            {
//              if(!lesson.isEqualTo(schedule[j-1][i-1+repeat]))
//              {
//                end = true;
//              }
//            }
//          }
//
//          ((HTMLTableCellElement)cells.item(j)).setAttribute("rowspan", repeat + "");
//
//          for (int k = 1; k < repeat; k++)
//          {
//            ((HTMLTableRowElement)rows.item(i+k)).removeChild(((HTMLTableRowElement)rows.item(i+k)).getChildNodes().item(j));
//          }

                    HTMLTableRowElement subjectRow = (HTMLTableRowElement) d.createElement("tr"); //(HTMLTableRowElement) lessonRows.item(0);
                    HTMLTableCellElement subjectCell = (HTMLTableCellElement) d.createElement("td"); //subjectRow.getCells().item(0);
                    subjectCell.setTextContent(lesson.getSubject());
                    subjectCell.setAttribute("class", "subject");
                    subjectRow.appendChild(subjectCell);
                    lessonTable.appendChild(subjectRow);

//                for (int k = 0; k < lessonRows.getLength() - 1; k++) {
//                    lessonTable.deleteRow(1);
//                }
                    for (int k = 0; k < lesson.getTeachers().length; k++)
                    {
                        HTMLTableRowElement r = (HTMLTableRowElement) d.createElement("tr");
                        HTMLTableCellElement cellTeacher = (HTMLTableCellElement) d.createElement("td");
                        cellTeacher.setTextContent(lesson.getTeachers()[k]);
                        HTMLTableCellElement cellClassroom = (HTMLTableCellElement) d.createElement("td");
                        cellClassroom.setTextContent(lesson.getClassrooms()[k]);
                        r.appendChild(cellTeacher);
                        r.appendChild(cellClassroom);
                        lessonTable.appendChild(r);
                    }
                }
            }
        }
    }

    private static String[] getDays(int calweek)
    {
        String[] days = new String[2];

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, calweek);

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        days[0] = sdf.format(cal.getTime());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        days[1] = sdf.format(cal.getTime());

        return days;
    }
}
