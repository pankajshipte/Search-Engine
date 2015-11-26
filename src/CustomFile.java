import java.io.File;

class CustomFile{
    private final File file;
    private long offset;

    public CustomFile(File file) {
        this.file = file;
        this.offset = 0;
    }

    public File getFile() {
        return file;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}