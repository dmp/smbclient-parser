import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by yanagisawa on 2017/03/26.
 */
public class SmbClientListHelperTest {
    @Test
    public void listService() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/");
        assertEquals(SmbClientListHelper.Type.ListService, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient -L //host -A auth -g",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listServiceNoTrailingSlash() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host");
        assertEquals(SmbClientListHelper.Type.ListService, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient -L //host -A auth -g",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFile() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/share/");
        assertEquals(SmbClientListHelper.Type.ListFile, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFileNoTrailingSlash() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/share");
        assertEquals(SmbClientListHelper.Type.ListFile, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFileWithHalfWidthSpace() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/foo bar/");
        assertEquals(SmbClientListHelper.Type.ListFile, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/foo bar", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient //host/foo bar -A auth -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFileWithFullWidthSpace() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/いろは　にほへと/");
        assertEquals(SmbClientListHelper.Type.ListFile, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/いろは　にほへと", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient //host/いろは　にほへと -A auth -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFileWithCd() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/share/foo/");
        assertEquals(SmbClientListHelper.Type.ListFileWithCd, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertEquals("foo", p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -D foo -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFileWithCdWithHalfWidthSpace() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/share/foo bar/");
        assertEquals(SmbClientListHelper.Type.ListFileWithCd, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertEquals("foo bar", p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -D foo bar -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void listFileWithCdWithFullWidthSpace() throws Exception {
        SmbClientListHelper p = SmbClientListHelper.parse("smb://host/share/いろは　にほへと/");
        assertEquals(SmbClientListHelper.Type.ListFileWithCd, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertEquals("いろは　にほへと", p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -D いろは　にほへと -c ls",
                StringUtils.join(p.getCommand("auth"), " "));
    }

    @Test
    public void getFile() throws Exception {
        SmbClientDownloadHelper p = SmbClientDownloadHelper.parse("smb://host/share/foo.doc");
        assertEquals(SmbClientDownloadHelper.Type.GetFile, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertNull(p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -c lcd storage; get \"foo.doc\";",
                StringUtils.join(p.getCommand("auth", "storage"), " "));
    }

    @Test
    public void getFileWithCd() throws Exception {
        SmbClientDownloadHelper p = SmbClientDownloadHelper.parse("smb://host/share/foo/bar.doc");
        assertEquals(SmbClientDownloadHelper.Type.GetFileWithCd, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertEquals("foo", p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -D foo -c lcd storage; get \"bar.doc\";",
                StringUtils.join(p.getCommand("auth", "storage"), " "));
    }

    @Test
    public void getFileWithCdWithHalfWidthSpace() throws Exception {
        SmbClientDownloadHelper p = SmbClientDownloadHelper.parse("smb://host/share/foo bar/baz 42.doc");
        assertEquals(SmbClientDownloadHelper.Type.GetFileWithCd, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertEquals("foo bar", p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -D foo bar -c lcd storage; get \"baz 42.doc\";",
                StringUtils.join(p.getCommand("auth", "storage"), " "));
    }

    @Test
    public void getFileWithCdWithFullWidthSpace() throws Exception {
        SmbClientDownloadHelper p = SmbClientDownloadHelper.parse("smb://host/share/いろ　は/にほへと　ちりぬるを.doc");
        assertEquals(SmbClientDownloadHelper.Type.GetFileWithCd, p.getType());
        assertEquals("host", p.getHost());
        assertEquals("//host/share", p.getServiceName());
        assertEquals("いろ　は", p.getInitialPath());
        assertEquals("smbclient //host/share -A auth -D いろ　は -c lcd storage; get \"にほへと　ちりぬるを.doc\";",
                StringUtils.join(p.getCommand("auth", "storage"), " "));
    }
}