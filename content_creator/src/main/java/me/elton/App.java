package me.elton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x5.template.Chunk;
import com.x5.template.Theme;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {

        String fileLocation = "/home/eltojaro/Downloads/Registration.xlsx";

        FileInputStream file = new FileInputStream(new File(fileLocation));
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            // loaded the excel sheet
            StringBuilder fname = null;
            StringBuilder lname = null;
            StringBuilder ctry = null;
            StringBuilder email = null;
            StringBuilder city = null;
            StringBuilder street = null;
            StringBuilder zip = null;
            StringBuilder title = null;

            // templating
            Theme theme = new Theme();
            Chunk chunk = theme.makeChunk("template", "txt");

            // iterating thru rows
            int maxRowNum = sheet.getLastRowNum();             
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; }

                email = getCString(row, 1);
                fname = getCString(row, 2);
                lname = getCString(row, 3);
                street = getCString(row, 4);
                zip = getCString(row, 5);
                city = getCString(row, 6);
                ctry = getCString(row, 7);

                System.out.println("Doing "+fname);

                title = getSHA(fname, lname, email);

                System.out.println("sha: " + title);

                chunk.set("title", title);
                chunk.set("fname", fname);
                chunk.set("lname", lname);
                chunk.set("ctry", ctry);
                chunk.set("email", email);
                chunk.set("city", city);
                chunk.set("street", street);
                chunk.set("zip", zip);
                chunk.set("access1", "true");
                chunk.set("access2", "true");
                chunk.set("access3", "true");

                String outfilePath = "content/ID/" + title + ".md";
                File outFile = new File(outfilePath);
                boolean result = outFile.createNewFile(); 
                if (result) 
                {
                    System.out.println("file created " 
                    + outFile.getCanonicalPath());
                } else {
                    System.out.println("File already exist at location: " 
                    + outFile.getCanonicalPath());
                }
                FileWriter outWrite = new FileWriter(outFile);

                chunk.render(outWrite);

                outWrite.flush();
                outWrite.close();

                Thread.sleep(200);

                if(row.getRowNum() == 10) {
                    break;}
            }
        }

    }

    public static StringBuilder getCString(Row row, int cellnum) {
        return new StringBuilder(row.getCell(cellnum).toString());
    }

    public static StringBuilder getSHA(StringBuilder... s) throws NoSuchAlgorithmException {

        StringBuilder toHash = new StringBuilder();

        for (StringBuilder str : s) {
            toHash.append(str);
        }

        // convert to SHA256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                toHash.toString().getBytes(StandardCharsets.UTF_8));

        // convert to hexa
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (int i = 0; i < encodedhash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedhash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return new StringBuilder(hexString.toString());
    }
}
