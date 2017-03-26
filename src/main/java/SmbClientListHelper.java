import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by yanagisawa on 2017/03/26.
 */
public class SmbClientListHelper implements SmbClientHelper {
    enum Type {
        ListService {
            @Override
            String[] getCommand(SmbClientHelper helper, String authFilePath) {
                return new String[]{"smbclient", "-L", helper.getServiceName(), "-A", authFilePath, "-g"};
            }

            @Override
            OutputParser getOutputParser() {
                return new OutputParser() {
                    @Override
                    public ArrayList<ArrayList<CdaFileDataDAO>> parse(String path, Process process) {
                        // cf. parseRootOutput()
                        throw new UnsupportedOperationException("unimplemented");
                    }
                };
            }
        }, ListFile {
            @Override
            String[] getCommand(SmbClientHelper helper, String authFilePath) {
                return new String[]{"smbclient", helper.getServiceName(), "-A", authFilePath, "-c", "ls"};
            }

            @Override
            OutputParser getOutputParser() {
                return new OutputParser() {
                    @Override
                    public ArrayList<ArrayList<CdaFileDataDAO>> parse(String path, Process process) {
                        // cf. parseLsOutput()
                        throw new UnsupportedOperationException("unimplemented");
                    }
                };
            }
        }, ListFileWithCd {
            @Override
            String[] getCommand(SmbClientHelper helper, String authFilePath) {
                return new String[]{"smbclient", helper.getServiceName(), "-A", authFilePath, "-D", helper.getInitialPath(), "-c", "ls"};
            }

            @Override
            OutputParser getOutputParser() {
                return ListFile.getOutputParser();
            }
        }, GetFile {
            @Override
            String[] getCommand(SmbClientHelper helper, String authFilePath) {
                return new String[0];
            }

            @Override
            OutputParser getOutputParser() {
                return null;
            }
        }, GetFileWithCd {
            @Override
            String[] getCommand(SmbClientHelper helper, String authFilePath) {
                return new String[0];
            }

            @Override
            OutputParser getOutputParser() {
                return null;
            }
        }, Error {
            @Override
            String[] getCommand(SmbClientHelper helper, String authFilePath) {
                throw new UnsupportedOperationException();
            }

            @Override
            OutputParser getOutputParser() {
                throw new UnsupportedOperationException();
            }
        };

        abstract String[] getCommand(SmbClientHelper helper, String authFilePath);

        abstract OutputParser getOutputParser();

    }

    public static class CdaFileDataDAO {

    }

    interface OutputParser {
        ArrayList<ArrayList<CdaFileDataDAO>> parse(String path, Process process);
    }

    private Type type = Type.Error;
    private String host;
    private String serviceName;
    private String initialPath;

    private SmbClientListHelper() {

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

    /**
     * @param authFilePath Path will be given to -A option
     * @return command String[] to be executed by ProcessBuilder
     */
    public String[] getCommand(String authFilePath) {
        return type.getCommand(this, authFilePath);
    }

    public OutputParser getOutputParser() {
        return type.getOutputParser();
    }

    public static SmbClientListHelper parse(String url) {
        SmbClientListHelper instance = new SmbClientListHelper();
        // URL protocol should be "smb"
        if (!url.startsWith("smb://")) {
            return instance;
        }
        final String[] segments = url.substring("smb://".length()).split("/");
        switch (segments.length) {
            case 0: // invalid input
                throw new IllegalArgumentException(url);
            case 1: // no path
                instance.type = Type.ListService;
                instance.host = segments[0];
                instance.serviceName = "//" + segments[0];
                return instance;
            case 2:
                instance.type = Type.ListFile;
                instance.host = segments[0];
                instance.serviceName = "//" + segments[0] + "/" + segments[1];
                return instance;
            default:
                instance.type = Type.ListFileWithCd;
                instance.host = segments[0];
                instance.serviceName = "//" + segments[0] + "/" + segments[1];
                instance.initialPath = StringUtils.join(ArrayUtils.subarray(segments, 2, segments.length), "/");
                return instance;
        }
    }
}
