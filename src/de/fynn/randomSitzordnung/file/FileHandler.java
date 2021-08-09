package de.fynn.randomSitzordnung.file;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import de.fynn.randomSitzordnung.Student;
import de.fynn.randomSitzordnung.flags.Flag;
import de.fynn.randomSitzordnung.flags.FlagFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileHandler {

    private final ConfigLoader configLoader = new ConfigLoader();

    public List<Student> loadStudentFile(File file){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            List<Student> students = new ArrayList<>();
            while ((line=reader.readLine())!=null){
                Flag flag = null;
                if(line.contains("-r")){
                    String[] strings = line.split("-r");
                    flag = FlagFactory.createFlag("fixRow",strings[1]);
                    line = strings[0];
                }else if(line.contains("-p")){
                    String[] strings = line.split("-p");
                    String[] values = strings[1].split(",");
                    flag = FlagFactory.createFlag("fixPos",values[0],values[1]);
                    line = strings[0];
                }
                students.add(flag==null?new Student(line):new Student(line,flag));
            }
            return students;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void exportFile(File file, List<Student> studentsObj, String roomNumber, String courseNumber){
        List<String> students = new ArrayList<>();
        for (Student s:
             studentsObj) {
            students.add(s.getName());
        }
        String[] filename = file.getName().split("\\.");
        int z = 0;
        if(filename[filename.length-1].equals("txt")){
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                int rows = configLoader.getRows();
                int[] studentsPerRow = studentsPerRow(students.size(),rows);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                if(!courseNumber.equals(""))writer.write(courseNumber+(roomNumber.equals("")?"\n":"   "));
                if(!roomNumber.equals(""))writer.write("Raum: "+roomNumber+"\n");
                if(!roomNumber.equals("")||!courseNumber.equals(""))writer.newLine();
                writer.write("Sitzplan vom "+ simpleDateFormat.format(new Date(System.currentTimeMillis())));
                writer.newLine();
                writer.newLine();
                for (int i = 0; i < rows; i++) {
                    writer.write("   "+(i+1)+". Reihe:");
                    writer.newLine();
                    for (int j = 0; j < studentsPerRow[i]; j++) {
                        writer.write("'"+students.get(z)+"'   ");
                        z++;
                    }
                    writer.newLine();
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                Document document = new Document(PageSize.A4,20,20,20,20);
                PdfWriter pdfWriter = PdfWriter.getInstance(document,new FileOutputStream(file));
                document.open();
                int rows = configLoader.getRows();
                int[] studentsPerRow = studentsPerRow(students.size(),rows);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                if(!courseNumber.equals("")){
                    document.add(new Paragraph(courseNumber+"   "));
                }
                if(!roomNumber.equals("")){
                    document.add(new Paragraph("Raum: "+roomNumber));
                }
                if (!courseNumber.equals("")||!roomNumber.equals("")){
                    document.add(Chunk.NEWLINE);
                }
                Image image = Image.getInstance("resources/Label.png");
                document.add(new Paragraph("Sitzplan vom "+ simpleDateFormat.format(new Date(System.currentTimeMillis()))));
                document.add(Chunk.NEWLINE);
                PdfContentByte cb = pdfWriter.getDirectContent();
                cb.setFontAndSize(BaseFont.createFont(),10);
                for (int i = 0; i < rows; i++) {
                    document.add(new Paragraph((i+1)+". Reihe:"));
                    document.add(Chunk.NEWLINE);
                    Paragraph paragraph = new Paragraph();
                    for (int j = 0; j < studentsPerRow[i]; j++) {
                        paragraph.add(new Chunk(students.get(z)));
                        z++;
                        if(j+1<studentsPerRow[i]) paragraph.add(new Chunk(image,0,0,true));
                    }
                    document.add(paragraph);
                    document.add(Chunk.NEWLINE);
                    document.add(Chunk.NEWLINE);
                }
                document.close();
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Student> importFile(File file){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            List<Student> students = new ArrayList<>();
            String line;
            while ((line=reader.readLine())!=null){
                if(line.contains("'")){
                    String[] names = line.split("'");
                    for (String s:
                         names) {
                        if(!s.equals("   ")&&!s.equals("")){
                            students.add(new Student(s));
                        }
                    }
                }
            }
            return students;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int[] studentsPerRow(int studentAmount, int rows){
        int[] studentsPerRow = new int[rows];
        for (int i = 0; i < studentAmount; i++) {
            studentsPerRow[i%rows]++;
        }
        return studentsPerRow;
    }

    public String getImp(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Impressum.txt"), StandardCharsets.UTF_8));
            String s;
            StringBuilder builer = new StringBuilder();
            while ((s=bufferedReader.readLine())!=null){
                if(s.equals("")){
                    builer.append("\n");
                }else {
                    builer.append(s);
                }
                builer.append("\n");
            }
            return builer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDS(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("Datenschutzerklärung.txt"), StandardCharsets.UTF_8));
            String s;
            StringBuilder builer = new StringBuilder();
            while ((s=bufferedReader.readLine())!=null){
                if(s.equals("")){
                    builer.append("\n");
                }else {
                    builer.append(s);
                }
                builer.append("\n");
            }
            return builer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
