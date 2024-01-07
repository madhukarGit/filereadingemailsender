package com.chadmockitoracle.chadmockito.service.filereader;


import com.chadmockitoracle.chadmockito.domain.EmailDetails;
import com.chadmockitoracle.chadmockito.entity.HousingProduction;
import com.chadmockitoracle.chadmockito.repository.HousingRepository;
import com.chadmockitoracle.chadmockito.service.emailsender.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FileReaderImpl implements FileReader{

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize ;

    private final EmailService emailService;
    private final HousingRepository housingRepository;

    public FileReaderImpl(EmailService emailService, HousingRepository housingRepository) {
        this.emailService = emailService;
        this.housingRepository = housingRepository;
    }

    @Override
    public void readFileFromDirectory() {
        FileSystem providers = providers().get(0).getFileSystem(URI.create("file:///"));
        /**
         * Iterable<Path> rootDirectories = providers.getRootDirectories();
         *         for (Path rootDirectory : rootDirectories) {
         *             System.out.println(rootDirectory);
         *         }
         *
         *         Iterable<FileStore> fileStores = providers.getFileStores();
         *         for (FileStore fileStore : fileStores) {
         *             System.out.println(fileStore.name());
         *         }
         */

        try {
//            createDirectoryLinks();
//            readWriteDirectory();
            copyIncomingFilesToInProgress(Path.of("D:/kaggle/filewalkthrough/sampledataset/incoming"));
            movingFilesToArchivedZipDirectory();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private List<FileSystemProvider> providers(){
        return FileSystemProvider.installedProviders();
    }

    /**
     * Create Directory
     */
    private void createDirectoryLinks() throws IOException {
        FileSystemProvider fileSystemProvider = providers().get(0);
        URI uriFile =
                URI.create("file:///D:/kaggle/filewalkthrough/large_dataset/custom_1988_2020_dataset.csv");
        BasicFileAttributes fileAttributes =
                fileSystemProvider.readAttributes(Paths.get(uriFile), BasicFileAttributes.class);
        System.out.println(fileAttributes.creationTime());
    }

    /**
     * CREATE FILES - WRITE DATA , READING FILES
     */
    private void readWriteDirectory() throws IOException {
        FileSystemProvider fileSystemProvider = providers().get(0);
        URI uriZip = URI.create("jar:file:///" + "D:/kaggle/filewalkthrough/" + "archived.zip");

        Map<String,String> options = new HashMap<>();
        options.put("create","true");


        try(FileSystem fs = FileSystems.newFileSystem(uriZip, options);){
            Path local =
                    Paths.get("D:/kaggle/filewalkthrough/sampledataset/Catalog_v2.csv");
            Path target = fs.getPath("compressed.txt");
            copyFileToTarget(local, target);
        }
    }

    /**
     * copy from incoming path to target directory
     */
    private void copyFileToTarget(Path incoming, Path target){
        System.out.println("local path "+incoming);
        System.out.println("target path "+target);
        if(Files.exists(target)){
            try {
                Files.delete(target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Files.copy(incoming, target);
            System.out.println("File moved successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * Reading the directory and fetching the individual files
     */
    private Map<Path, Path> readTheDirectoryToIndividualFiles(Path path){

        Map<Path,Path> fileMapPaths = new HashMap<>();

        try (DirectoryStream<Path> directoryStream =Files.newDirectoryStream(path,"*.csv");){
            Iterator<Path> files  = directoryStream.iterator();
            if(!files.hasNext()){
                System.out.println("No File Found !!!!");
            }else{
                while(files.hasNext()){
                    Path filePath = files.next().toAbsolutePath();
                    System.out.println("file Path "+filePath);
                    System.out.println("file name "+filePath.getFileName());
                    fileMapPaths.put(filePath.getFileName(),filePath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileMapPaths;
    }

    /**
     * Listing files in directory
     */
    private void copyIncomingFilesToInProgress(Path incomingPathOfCsv){
        Map<Path, Path> directory = readTheDirectoryToIndividualFiles(incomingPathOfCsv);
        if(!directory.isEmpty()){
            directory.forEach((fileName, fileAbsolutePath)->{
                Path targetPath = Paths.get("D:/kaggle/filewalkthrough/sampledataset/progress");
                Path destinationDirectory = Paths.get(targetPath+"/"+fileName);
                processProgressData(fileAbsolutePath);
                copyFileToTarget(fileAbsolutePath,destinationDirectory);
            });
        }else{
            System.out.println("No Files Found !!");
            emailService.sendMailWithAttachment(emailDetails("No Files Found !"));
        }
    }

    /*
    * process in progress csv files, and process the data to DB
    * */
    private void processProgressData(Path progressDirectoryPath){
        List<HousingProduction> housingProductions =
                new ArrayList<>();
        try(Stream<String> lines
                    =Files.lines(progressDirectoryPath)){
            lines.
                    limit(10)
                        .forEach(housing ->{
                            HousingProduction housingProduction =
                                    new HousingProduction();
                            String[] housings = housing.split(",");
                            housingProduction.setBuildingPermitApplicationNumber(housings[0]);
                            housingProduction.setPermitAddress(housings[1]);
                            housingProduction.setPermitDescription(housings[2]);
                            housingProduction.setExistingUnitsInPTSDatabase(housings[3]);
                            housingProduction.setProposedUnitsInPTSDatabase(housings[4]);
                            housingProduction.setActualProposedUnits(housings[5]);
                            housingProductions.add(housingProduction);
                    });
            saveAllHousingRecordsBatchWise(housingProductions);
        } catch (IOException e) {
            emailService.sendMailWithAttachment(emailDetails("Processing of records failed !!"));
            throw new RuntimeException(e);
        }
    }


    /**
     * Moving Files To Archived Zip Directory
     */
    private void movingFilesToArchivedZipDirectory(){
        Path progressDirectory = Paths.get("D:/kaggle/filewalkthrough/sampledataset/progress");

        Map<Path, Path> progressPaths = readTheDirectoryToIndividualFiles(progressDirectory);

        URI uriZip = URI.create("jar:file:///" + "D:/kaggle/filewalkthrough/sampledataset/archived" + "/"+"archived.zip");

        Map<String,String> options = new HashMap<>();
        options.put("create","true");

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try(FileSystem fs = FileSystems.newFileSystem(uriZip, options);){
            if(!progressPaths.isEmpty()){
                progressPaths.forEach((fileName, fileAbsolutePath)->{
                    Path target = fs.getPath(LocalDate.now().format(dateFormatter)+"-"+fileName);
                    copyFileToTarget(fileAbsolutePath, target);
                });
            }else{
                emailService.sendMailWithAttachment(emailDetails("Could not process files !"));
            }
            emailService.sendMailWithAttachment(emailDetails("Files Moved Successfully !!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param message Sending emails
     * @return EmailDetails
     */
    private EmailDetails emailDetails(String message){
        EmailDetails emailDetails =
                new EmailDetails();
        emailDetails.setRecipient("hmk02330@gmail.com");
        emailDetails.setMsgBody(message);
        emailDetails.setSubject("SMTP Tested !!");
        emailDetails.setAttachment("D:/kaggle/filewalkthrough/email_attachements/spring.png");
        return emailDetails;
    }

    private void saveAllHousingRecordsBatchWise(List<HousingProduction> housingProductions){
        long start = System.currentTimeMillis();
        for (int i = 0; i < housingProductions.size(); i = i + batchSize) {
            if( i+ batchSize > housingProductions.size()){
                List<HousingProduction> bankCustomerList = housingProductions.subList(i, housingProductions.size() - 1);
                housingRepository.saveAll(bankCustomerList);
                break;
            }
            List<HousingProduction> housingProductionList = housingProductions.subList(i, i + batchSize);
            housingRepository.saveAll(housingProductionList);
        }
        long end = System.currentTimeMillis();
        float msec = end - start;
        // converting it into seconds
        float sec= msec/1000F;
        // converting it into minutes
        float minutes=sec/60F;
        System.out.println(minutes + " minutes");
    }
}
