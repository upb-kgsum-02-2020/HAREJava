package dev.kgpg.kgsum.controller;

import dev.kgpg.kgsum.HARE.HARERank;
import dev.kgpg.kgsum.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.function.library.leviathan.log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

@RestController
@CrossOrigin(origins = "*")
public class KgsumController {


    private static final String tempFilePath = "kgsum.ttl";

    @PostMapping("getTesting")
    public String getTesting(@RequestParam(name = "file_input") MultipartFile inputFile) {
        Boolean fileResponse = false;
        String error = "";
        try {
            fileResponse = this.writeToTempFolder(inputFile);
        } catch (IOException e) {
            fileResponse = false;
            error = e.getMessage();
        }
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", true);
        jsonResponse.put("response", "working");
        jsonResponse.put("fileResponse", fileResponse);
        jsonResponse.put("error", error);

        return jsonResponse.toString();
    }


    @PostMapping("/kgsum")
    public String getGraph(@RequestParam(name = "file_input") MultipartFile inputFile) throws IOException {

//      log.info("In getKGraphSalsa", inputFile);
        this.writeToTempFolder(inputFile);

        JSONObject jsonResponse;

        RDFReadWriteHandler reader = new RDFReadWriteHandler();
        long read_tic = System.currentTimeMillis();//narase added for time
        Model readModel = reader.readData(tempFilePath);
        long read_tac = System.currentTimeMillis();//narase added for time
        RDFReadWriteHandler write = new RDFReadWriteHandler();
        long exe_tic = System.currentTimeMillis();//added time calculation Narase
        HARERank hrTester = new HARERank(readModel);
        hrTester.calculateRank();
        //added start for time calculation Narase
        long exe_tac = System.currentTimeMillis();
        System.out.println("Reading Data time is " + ((read_tac - read_tic) / 1000d) + " seconds");
        System.out.println("Total Execution HARE  time is " + ((exe_tac - exe_tic) / 1000d) + " seconds");
        //added finish for time calculation Narase
        write.writeFilteredTriples(
                hrTester.getS_t_Final(),
                hrTester.getMatrxUtil().getTripleList(),
                tempFilePath.substring(0, tempFilePath.indexOf('.')));


        jsonResponse = new JSONObject();
        jsonResponse.put("status", true);
        return jsonResponse.toString();
    }

    public Boolean writeToTempFolder(MultipartFile inputFile) throws IOException {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(tempFilePath);
            fos.write(inputFile.getBytes());
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteTempFile() {
        File ttl = new File(tempFilePath);
        ttl.delete();
    }
}
