package alchemist.fit.uom.alchemists.models;
public class FileUploadInfo {

    public String fileType;
    public String fileName;
    public String fileURL;


    public FileUploadInfo() {

    }

    public FileUploadInfo(String fileType, String fileName, String url) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileURL = url;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileURL() {
        return fileURL;
    }


}
