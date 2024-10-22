package anb52.jobhunter.controller;

import anb52.jobhunter.domain.response.file.ResUploadFileDTO;
import anb52.jobhunter.service.FileService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.StorageException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

     @Autowired
    private final FileService fileService;

    @Value("${anb52.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(@RequestParam(name = "file", required = false) MultipartFile file,
                                                   @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // skip validate
        if(file == null || file.isEmpty()){
            throw new StorageException("File is Empty. Please upload a file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = false;
        for (String extension : allowedExtensions) {
            // Kiểm tra xem tên file có kết thúc bằng phần mở rộng hợp lệ không
            if (fileName.toLowerCase().endsWith(extension)) {
                isValid = true;
                break;
            }
        }
        if(!isValid){
            throw new StorageException("Invalid file extension. only allows " + allowedExtensions.toString());
        }

        //create a directory if not exist
         this.fileService.createDirectory(baseURI + folder);

        // sotre file
        String uploadFile = this.fileService.store(file,folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());


        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder) in query params.");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + fileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
