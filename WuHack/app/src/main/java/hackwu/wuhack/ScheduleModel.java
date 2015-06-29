package hackwu.wuhack;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Paul on 29.06.2015.
 */
public class ScheduleModel
{
    private Lesson[][][] timetable = new Lesson[32][5][12];
    private List<String> kuerzel = new ArrayList<>(90);
    private List<String> classes = new ArrayList<>(30);
    private List<String> classrooms = new ArrayList<>(50);

    private static final ScheduleModel instance = new ScheduleModel();


    public static ScheduleModel getInstance()
    {
        return instance;
    }

    public Lesson[][] analyzeDoc(Document d, int calweek, int index)
    {
        Lesson[][] schedule = new Lesson[5][12];
        int[] doneCells = new int[12];

        for (int i = 0; i < doneCells.length; i++)
        {
            doneCells[i] = 0;
        }

        if (d == null)
        {
            return null;
        }

        Node f = d.getElementsByTagName("font").item(1);

        String klasse = removeLineBreak(f.getTextContent());

        if (!this.classes.contains(klasse))
        {
            this.classes.add(klasse);
        }

        NodeList tables = d.getElementsByTagName("table");

        Node table =  tables.item(0);

        NodeList rows = table.getChildNodes();

        for (int i = 1; i < rows.getLength(); i = i + 2)
        {
            Node row = rows.item(i);

            NodeList cells = row.getChildNodes();

            for (int j = 1; j < cells.getLength(); j++)
            {
                if (schedule[j - 1 + doneCells[(i - 1) / 2]][((i - 1) / 2)] == null)
                {

                    LinkedList<String> teachers = new LinkedList<>();
                    String subject = "???";
                    LinkedList<String> classrooms = new LinkedList<>();
                    boolean isLesson = true;


                    Node cell =  cells.item(j);

                    Node inTable = cell.getChildNodes().item(0);
                    NodeList inRows = inTable.getChildNodes();

                    for (int k = 0; k < inRows.getLength(); k++)
                    {
                        Node inRow =  inRows.item(k);
                        NodeList inCells = inRow.getChildNodes();

                        if (k == 0)
                        {
                            if (removeSigns(inCells.item(0).getTextContent().trim()).length() > 5)
                            {
                                // (todo, derweil wird nur geloscht)
                                isLesson = false;
                            }
                            else
                            {
                                subject = removeSigns(inCells.item(0).getTextContent().trim());
                            }

                        }
                        else
                        {
                            if (removeSigns(inCells.item(0).getTextContent().trim()).length() > 3)
                            {
                                // (todo, derweil wird nur geloscht)
                                isLesson = false;
                            }
                            else
                            {
                                String classroom;

                                if (inCells.getLength() == 1)
                                {
                                    classroom = "???";
                                }
                                else
                                {
                                    classroom = removeSigns(inCells.item(1).getTextContent().trim());
                                }

                                String kuerzel = removeSigns(inCells.item(0).getTextContent().trim());
                                teachers.add(kuerzel);

                                if (!this.kuerzel.contains(kuerzel) && !kuerzel.equals("---"))
                                {
                                    this.kuerzel.add(kuerzel);
                                }

                                classrooms.add(classroom);

                                if (!this.classrooms.contains(classroom) && !classroom.equals("???") && !classroom.equals("---"))
                                {
                                    this.classrooms.add(classroom);
                                }
                            }
                        }
                    }

                    for (int k = 0; k < (Integer.parseInt(cell.getAttributes().getNamedItem("rowspan").getTextContent()) / 2); k++)
                    {
                        int spalte = j - 1 + doneCells[(i - 1) / 2];
                        int zeile = (i - 1) / 2 + k;

                        //System.out.println("Spalte: " + spalte + ", Zeile: " + zeile);

                        if (isLesson && schedule[spalte][zeile] == null)
                        {
                            Lesson l = new Lesson(teachers.toArray(new String[0]), subject, klasse, classrooms.toArray(new String[0]), calweek);
                            schedule[spalte][zeile] = l;

                        }
                    }
                }
                else
                {
                    //System.out.println("longer lesson!");
                    doneCells[(i - 1) / 2]++;
                    j--;
                }
            }

        }

        return schedule;
    }

    private String[] convertClassrooms(List<String> classrooms)
    {
        String[] array = new String[classrooms.size()];

        for (int i = 0; i < classrooms.size(); i++)
        {
            array[i] = classrooms.get(i);
        }

        return array;
    }

    public Lesson[][] getTeacherLessons(String ku)
    {
        Lesson[][] table = new Lesson[5][12];

        for (int i = 0; i < timetable.length; i++)
        {
            for (int j = 0; j < timetable[i].length; j++)
            {
                for (int k = 0; k < timetable[i][j].length; k++)
                {
                    Lesson l = timetable[i][j][k];

                    if (l != null && contains(l.getTeachers(), ku))
                    {
                        table[j][k] = l;
                    }
                }
            }
        }

        return table;
    }

    public Lesson[][] getClassLessons(String cl)
    {
        Lesson[][] table = new Lesson[5][12];

        boolean classfound = false;
        int i = 0;
        while (!classfound && i < timetable.length)
        {
            int j = 0;
            int k = 0;
            boolean found = false;
            while (!found && j < timetable[0].length)
            {
                while (!found && k < timetable[0][0].length)
                {
                    if (timetable[i][j][k] != null)
                    {
                        found = true;

                        if (timetable[i][j][k].getKlasse().equals(cl))
                        {
                            table = timetable[i];
                            classfound = true;
                        }
                    }

                    k++;
                }
                j++;
            }
            i++;
        }

        return table;
    }

    public Lesson[][] getClassroomLessons(String cl)
    {
        Lesson[][] table = new Lesson[5][12];

        for (int i = 0; i < timetable.length; i++)
        {
            for (int j = 0; j < timetable[i].length; j++)
            {
                for (int k = 0; k < timetable[i][j].length; k++)
                {
                    Lesson l = timetable[i][j][k];

                    if (l != null && contains(l.getClassrooms(), cl))
                    {
                        table[j][k] = l;
                    }
                }
            }
        }
        return table;
    }

    public void loadAllLessons(final WebView wv, final int week)
    {
        classes.clear();
        kuerzel.clear();
        classrooms.clear();

        wv.setWebViewClient(new WebViewClient() {
            int counter = 1;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (counter < timetable.length - 1)
                {
                    counter++;
                    String next_url = "https://supplierplan.htl-kaindorf.at/supp_neu/" + (week) + "/c/c" + String.format("%05d", counter) + ".htm";
                    wv.loadUrl(next_url);

                }
                else
                {
                    wv.setWebViewClient(null);
                }
            }
        });

        wv.loadUrl("https://supplierplan.htl-kaindorf.at/supp_neu/" + (week) + "/c/c" + String.format("%05d", 1) + ".htm");
    }

    private boolean contains(String[] a, String k)
    {
        boolean b = false;

        for (int i = 0; i < a.length; i++)
        {
            if (a[i].equals(k))
            {
                b = true;
            }
        }

        return b;
    }

    private String removeSigns(String s)
    {
        char[] ignorableChars
                =
                {
                        '.', ',', '\'', '\"', '!', '?', '§', '$',
                        '%', '&', '/', '(', ')', '=', '\\', ']',
                        '[', '{', '}', '#', '+', '*', '~', '\n'
                };

        for (char ignorableChar : ignorableChars)
        {
            s = s.replace(ignorableChar + "", "");
        }

        return s;
    }

    public String removeLineBreak(String s)
    {
        String s1 = "";

        s1 = s.replace("\n", "");

        return s1;
    }

    public List<String> getClasses()
    {
        return classes;
    }

    public List<String> getKuerzel()
    {
        return kuerzel;
    }

    public Lesson[][][] getTimetable()
    {
        return timetable;
    }

    public List<String> getClassrooms()
    {
        return classrooms;
    }
}
