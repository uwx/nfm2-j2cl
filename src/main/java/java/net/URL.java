package java.net;

public class URL {
    public final elemental2.dom.URL url;

    public URL(URL context, String spec) throws MalformedURLException {
        url = new elemental2.dom.URL(spec, context.url);
    }

    public URL(String href) throws MalformedURLException {
        url = new elemental2.dom.URL(href);
    }

    public String getQuery() {
        return url.search;
    }

    /**
     * Gets the path part of this {@code URL}.
     *
     * @return  the path part of this {@code URL}, or an
     * empty string if one does not exist
     * @since 1.3
     */
    public String getPath() {
        return url.pathname;
    }

    /**
     * Gets the userInfo part of this {@code URL}.
     *
     * @return  the userInfo part of this {@code URL}, or
     * <CODE>null</CODE> if one does not exist
     * @since 1.3
     */
    public String getUserInfo() {
        return url.username;
    }

    /**
     * Gets the authority part of this {@code URL}.
     *
     * @return  the authority part of this {@code URL}
     * @since 1.3
     */
    public String getAuthority() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the port number of this {@code URL}.
     *
     * @return  the port number, or -1 if the port is not set
     */
    public int getPort() {
        return Integer.parseInt(url.port);
    }

    /**
     * Gets the default port number of the protocol associated
     * with this {@code URL}. If the URL scheme or the URLStreamHandler
     * for the URL do not define a default port number,
     * then -1 is returned.
     *
     * @return  the port number
     * @since 1.4
     */
    public int getDefaultPort() {
        return -1;
    }

    /**
     * Gets the protocol name of this {@code URL}.
     *
     * @return  the protocol of this {@code URL}.
     */
    public String getProtocol() {
        return url.protocol;
    }

    /**
     * Gets the host name of this {@code URL}, if applicable.
     * The format of the host conforms to RFC 2732, i.e. for a
     * literal IPv6 address, this method will return the IPv6 address
     * enclosed in square brackets ({@code '['} and {@code ']'}).
     *
     * @return  the host name of this {@code URL}.
     */
    public String getHost() {
        return url.host;
    }

    /**
     * Gets the file name of this {@code URL}.
     * The returned file portion will be
     * the same as <CODE>getPath()</CODE>, plus the concatenation of
     * the value of <CODE>getQuery()</CODE>, if any. If there is
     * no query portion, this method and <CODE>getPath()</CODE> will
     * return identical results.
     *
     * @return  the file name of this {@code URL},
     * or an empty string if one does not exist
     */
    public String getFile() {
        return "";
    }

    /**
     * Gets the anchor (also known as the "reference") of this
     * {@code URL}.
     *
     * @return  the anchor (also known as the "reference") of this
     *          {@code URL}, or <CODE>null</CODE> if one does not exist
     */
    public String getRef() {
        return null;
    }

    @Override
    public String toString() {
        return url.href;
    }
}
