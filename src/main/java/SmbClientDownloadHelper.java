import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yanagisawa on 2017/03/26.
 */
public class SmbClientDownloadHelper implements SmbClientHelper {

    enum Type {
        GetFile {
            @Override
            String[] getCommand(SmbClientDownloadHelper helper, String authFilePath, String localDownloadPath) {
                return new String[]{"smbclient", helper.getServiceName(), "-A", authFilePath, "-c",
                        String.format(FORMAT, localDownloadPath, helper.getFileName())};
            }
        }, GetFileWithCd {
            @Override
            String[] getCommand(SmbClientDownloadHelper helper, String authFilePath, String localDownloadPath) {
                return new String[]{"smbclient", helper.getServiceName(), "-A", authFilePath,
                        "-D", helper.getInitialPath(),
                        "-c", String.format(FORMAT, localDownloadPath, helper.getFileName())};
            }
        }, Error {
            @Override
            String[] getCommand(SmbClientDownloadHelper helper, String authFilePath, String localDownloadPath) {
                throw new UnsupportedOperationException();
            }
        };

        private static final String FORMAT = "lcd %s; get \"%s\";";

        abstract String[] getCommand(SmbClientDownloadHelper helper, String authFilePath, String localDownloadPath);
    }

    private Type type = Type.Error;
    private String host;
    private String serviceName;
    private String initialPath;

    private String fileName;

    private SmbClientDownloadHelper() {

    }

    public Type getType() {
        return type;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getInitialPath() {
        return initialPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String[] getCommand(String authFilePath, String localDownloadPath) {
        return type.getCommand(this, authFilePath, localDownloadPath);
    }

    public static SmbClientDownloadHelper parse(String url) {
        SmbClientDownloadHelper instance = new SmbClientDownloadHelper();
        // URL protocol should be "smb"
        if (!url.startsWith("smb://")) {
            return instance;
        }
        final String[] segments = url.substring("smb://".length()).split("/");
        switch (segments.length) {
            case 0: // run through
            case 1: // run through
            case 2: // invalid input
                throw new IllegalArgumentException(url);
            case 3:
                instance.type = Type.GetFile;
                instance.host = segments[0];
                instance.serviceName = "//" + segments[0] + "/" + segments[1];
                instance.fileName = segments[2];
                return instance;
            default:
                instance.type = Type.GetFileWithCd;
                instance.host = segments[0];
                instance.serviceName = "//" + segments[0] + "/" + segments[1];
                final int last = segments.length - 1;
                instance.initialPath = StringUtils.join(ArrayUtils.subarray(segments, 2, last), "/");
                instance.fileName = segments[last];
                return instance;
        }
    }
}
