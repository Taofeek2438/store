package com.codewithtaofeek.store.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/pdf")
public class PdfToExcelController {

    // Adjust path to wherever your `tabula.jar` is located relative to project root or provide absolute path
    private final String TABULA_JAR_PATH = "libs/tabula.jar";  // 🔁 Update this to your actual path

    @PostMapping("/convert")
    public ResponseEntity<?> convertPdfToExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Use system temp directory
            String tempDir = System.getProperty("java.io.tmpdir");

            // Save uploaded file to temp location
            String uniqueId = UUID.randomUUID().toString();
            File pdfFile = new File(tempDir, uniqueId + ".pdf");
            file.transferTo(pdfFile);

            // Output CSV file in same temp location
            File csvFile = new File(tempDir, uniqueId + ".csv");

            // Run tabula
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-jar", TABULA_JAR_PATH,
                    "--pages", "all",
                    "--outfile", csvFile.getAbsolutePath(),
                    "--format", "CSV",
                    pdfFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Log output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Tabula] " + line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.status(500).body("Failed to convert PDF to CSV.");
            }

            // Return CSV content
            byte[] csvBytes = Files.readAllBytes(csvFile.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("converted.csv").build());

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }
}
