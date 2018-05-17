package example.gifmanager;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

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

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import example.gifmanager.R;

public class PDFActivity extends Activity {
    private static String FILE = Environment.getExternalStorageDirectory()
            + "/HelloWorld.pdf";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a document and set it's properties
        /**
         * Creating Document
         */
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();

        // Location to save
        File dest = new File(Environment.getExternalStorageDirectory(), "GIFManager");
        File pdfFile = new File(dest + File.separator +
                "DOC_"+ "result" + ".pdf");
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
        document.addAuthor("ASD");
        document.addCreator("asd");

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
        Chunk mOrderDetailsTitleChunk = new Chunk("Match Report", mOrderDetailsTitleFont);
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

        // Fields of Order Details..
        // Adding Chunks for Title and value
        /*
        Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
        Chunk mOrderIdChunk = new Chunk("Match report", mOrderIdFont);
        Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
        try {
            document.add(mOrderIdParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/

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

        /*
        try {
            document.add(new Paragraph("Result"));
            img.scaleToFit(200, 300);
            document.add(img);
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/


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

        /*
        try {
            document.add(new Paragraph("Fairplay"));
            img2.scaleToFit(200, 300);
            document.add(img2);
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/

        PdfPTable table = new PdfPTable(2);
        PdfPCell cell;

        cell = new PdfPCell(new Paragraph("Resultcard"));
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Fairplay"));
        table.addCell(cell);

        cell = new PdfPCell(img);
        //cell.setColspan(3);
        table.addCell(cell);

        // now we add a cell with rowspan 2
        cell = new PdfPCell(img2);
        //cell.setRowspan(2);
        table.addCell(cell);
        // we add the four remaining cells with addCell()

        try {
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();

    }
}