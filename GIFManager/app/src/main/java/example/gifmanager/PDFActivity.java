package example.gifmanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PDFActivity extends Activity {
    private static String FILE = Environment.getExternalStorageDirectory()
            + "/HelloWorld.pdf";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        // Create a document and set it's properties
        /**
         * Creating Document
         */
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();

        // Location to save
        File pdfFile = new File(DataHolder.getInstance().getReportPath());

        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Open to write
        document.open();

        // Document Settings
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor(DataHolder.getInstance().getAdminCode());
        document.addCreator(DataHolder.getInstance().getAdminCode());


        BaseFont urName = null;

        try {
            urName = BaseFont.createFont(BaseFont.HELVETICA, "UTF-8", BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            // adds a title for the document
            Font registeredPlayersFont = new Font(urName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            String titleString = "Match " + DataHolder.getInstance().getNr() + " " +
                    "20" + DataHolder.getInstance().getCurrentDate() + "\n\n";
            Chunk registeredPlayersChunk = new Chunk(titleString, registeredPlayersFont);
            // Creating Paragraph to add...
            Paragraph registeredPlayersParagraph = new Paragraph(registeredPlayersChunk);
            // Setting Alignment for Heading
            registeredPlayersParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(registeredPlayersParagraph);
            //document.add(Chunk.NEWLINE);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            // adds an empty space and adds a player table
            //document.add(Chunk.NEWLINE);
            document.add(createPlayerTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        try {
            // adds an empty space and adds a table with result and fairplay cards
            document.add(new Chunk(" "));
            document.add(createResultAndFairplayTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            // adds an empty space and adds a table with signatures from both teams
            document.add(new Chunk(" "));
            document.add(signatureTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();
        finish();
    }

    /*
     * creates a table with the registered players for each team
     */
    public PdfPTable createPlayerTable() throws DocumentException {
        // a table with three columns
        PdfPTable playerTable = new PdfPTable(2);
        // the cell object
        PdfPCell team1Cell;
        PdfPCell team2Cell;


        BaseFont urName = null;
        Font tableHeading = new Font(urName, 24.0f, Font.NORMAL, BaseColor.BLACK);

        Chunk rChunk = new Chunk(DataHolder.getInstance().getTeam1Name(), tableHeading);
        // Creating Paragraph to add...
        Paragraph rPar = new Paragraph(rChunk);
        // Setting Alignment for Heading
        rPar.setAlignment(Element.ALIGN_CENTER);

        Chunk fChunk = new Chunk(DataHolder.getInstance().getTeam2Name(), tableHeading);
        // Creating Paragraph to add...
        Paragraph fPar = new Paragraph(fChunk);
        // Setting Alignment for Heading
        fPar.setAlignment(Element.ALIGN_CENTER);

        team1Cell = new PdfPCell(rPar);
        playerTable.addCell(team1Cell);

        team2Cell = new PdfPCell(fPar);
        playerTable.addCell(team2Cell);

        ArrayList<String> team1 = DataHolder.getInstance().getTeam1Members();
        ArrayList<String> team2 = DataHolder.getInstance().getTeam2Members();

        if(team1.isEmpty()){
            team1.add(getString(R.string.no_players_registered));
        }
        if(team2.isEmpty()){
            team2.add(getString(R.string.no_players_registered));
        }

        int maxLength = (team1.size() > team2.size())?team1.size():team2.size();

        for(int i = 0; i < maxLength; i++){
            if(team1.size() > i){
                playerTable.addCell(team1.get(i));
            }else {
                playerTable.addCell("");
            }

            if (team2.size() > i){
                playerTable.addCell(team2.get(i));
            }else{
                playerTable.addCell(" ");
            }
        }


        return playerTable;
    }

    /*
     * Creates a table with the result and fairplaycard next to eachother with heading
     */
    public PdfPTable createResultAndFairplayTable() throws DocumentException {
        Image img = null;
        try {
            String imagePath = DataHolder.getInstance().getResultImagePath();
            img = Image.getInstance(imagePath);
            img.scaleToFit(209, 300);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image img2 = null;
        try {
            String imagePath = DataHolder.getInstance().getFairplayImagePath();
            img2 = Image.getInstance(imagePath);
            img2.scaleToFit(209, 300);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfPTable table = new PdfPTable(2);
        PdfPCell cell;

        BaseFont urName = null;
        Font tableHeading = new Font(urName, 24.0f, Font.NORMAL, BaseColor.BLACK);

        Chunk rChunk = new Chunk(getString(R.string.resultcard), tableHeading);
        // Creating Paragraph to add...
        Paragraph rPar = new Paragraph(rChunk);
        // Setting Alignment for Heading
        rPar.setAlignment(Element.ALIGN_CENTER);

        Chunk fChunk = new Chunk(getString(R.string.fairplay), tableHeading);
        // Creating Paragraph to add...
        Paragraph fPar = new Paragraph(fChunk);
        // Setting Alignment for Heading
        fPar.setAlignment(Element.ALIGN_CENTER);

        cell = new PdfPCell(rPar);
        table.addCell(cell);

        cell = new PdfPCell(fPar);
        table.addCell(cell);

        cell = new PdfPCell(img);
        table.addCell(cell);

        cell = new PdfPCell(img2);
        table.addCell(cell);
        return table;
    }

    /*
     * creates a table with the signature of both teams
     */
    public PdfPTable signatureTable() throws DocumentException {
        Image img = null;
        try {
            String imagePath = DataHolder.getInstance().getTeam1SignaturePath();
            img = Image.getInstance(imagePath);
            img.scaleToFit(200, 100);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image img2 = null;
        try {
            String imagePath = DataHolder.getInstance().getTeam2SignaturePath();
            img2 = Image.getInstance(imagePath);
            img2.scaleToFit(200, 100);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BaseFont urName = null;
        Font tableHeading = new Font(urName, 18.0f, Font.NORMAL, BaseColor.BLACK);

        Chunk rChunk = new Chunk(DataHolder.getInstance().getTeam1Name() + " " +
                getString(R.string.trainer_signature), tableHeading);
        // Creating Paragraph to add...
        Paragraph rPar = new Paragraph(rChunk);
        // Setting Alignment for Heading
        rPar.setAlignment(Element.ALIGN_CENTER);

        Chunk fChunk = new Chunk(DataHolder.getInstance().getTeam2Name() + " " +
                getString(R.string.trainer_signature), tableHeading);
        // Creating Paragraph to add...
        Paragraph fPar = new Paragraph(fChunk);
        // Setting Alignment for Heading
        fPar.setAlignment(Element.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(2);
        PdfPCell cell;

        cell = new PdfPCell(rPar);
        table.addCell(cell);

        cell = new PdfPCell(fPar);
        table.addCell(cell);

        cell = new PdfPCell(img);
        table.addCell(cell);

        cell = new PdfPCell(img2);
        table.addCell(cell);
        return table;
    }
}