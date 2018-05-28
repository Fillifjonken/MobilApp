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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

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

        /***
         * Variables for further use....
         */
        BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;


        BaseFont urName = null;

        try {
            urName = BaseFont.createFont(BaseFont.HELVETICA, "UTF-8", BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

        // Title Order Details...
        // Adding Title....
        Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
        // Creating Chunk
        Chunk mOrderDetailsTitleChunk = new Chunk(getString(R.string.match_report), mOrderDetailsTitleFont);
        // Creating Paragraph to add...
        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
        // Setting Alignment for Heading
        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
        // Finally Adding that Chunk
        try {
            document.add(mOrderDetailsTitleParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            document.add(new Chunk(" "));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            Font registeredPlayersFont = new Font(urName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk registeredPlayersChunk = new Chunk(getString(R.string.registered_players), registeredPlayersFont);
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
            document.add(createPlayerTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            document.add(new Chunk(" "));
            document.add(signatureTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            Font resultAndFairplayFont = new Font(urName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk rafChunk = new Chunk(getString(R.string.result_and_fairplay), resultAndFairplayFont);
            // Creating Paragraph to add...
            Paragraph rafPar = new Paragraph(rafChunk);
            // Setting Alignment for Heading
            rafPar.setAlignment(Element.ALIGN_CENTER);
            document.add(rafPar);
            //document.add(Chunk.NEWLINE);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        try {
            document.add(createResultAndFairplayTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();
        finish();
    }

    public PdfPTable createPlayerTable() throws DocumentException {
        // a table with three columns
        PdfPTable playerTable = new PdfPTable(2);
        // the cell object
        PdfPCell team1Cell;
        PdfPCell team2Cell;
        // we add a cell with colspan 3
        team1Cell = new PdfPCell(new Phrase(DataHolder.getInstance().getTeam1Name()));
        playerTable.addCell(team1Cell);
        // now we add a cell with rowspan 2
        team2Cell = new PdfPCell(new Phrase(DataHolder.getInstance().getTeam2Name()));
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

        cell = new PdfPCell(new Paragraph(getString(R.string.resultcard)));
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(getString(R.string.fairplay)));
        table.addCell(cell);

        cell = new PdfPCell(img);
        table.addCell(cell);

        cell = new PdfPCell(img2);
        table.addCell(cell);
        return table;
    }

    public PdfPTable signatureTable() throws DocumentException {
        Image img = null;
        try {
            String imagePath = DataHolder.getInstance().getTeam1SignaturePath();
            img = Image.getInstance(imagePath);
            img.scaleToFit(200, 180);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image img2 = null;
        try {
            String imagePath = DataHolder.getInstance().getTeam2SignaturePath();
            img2 = Image.getInstance(imagePath);
            img2.scaleToFit(200, 180);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfPTable table = new PdfPTable(2);
        PdfPCell cell;

        cell = new PdfPCell(new Paragraph(DataHolder.getInstance().getTeam1Name() + " " +
                getString(R.string.trainer_signature)));
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(DataHolder.getInstance().getTeam2Name() + " " +
                getString(R.string.trainer_signature)));
        table.addCell(cell);

        cell = new PdfPCell(img);
        table.addCell(cell);

        cell = new PdfPCell(img2);
        table.addCell(cell);
        return table;
    }
}