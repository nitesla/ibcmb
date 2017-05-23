
package com.expertedge.entrustplugin.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.expertedge.entrustplugin.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PerformTokenAuthCorporateNoCategory_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performTokenAuthCorporateNoCategory");
    private final static QName _PerformTokenAuthNoCategoryResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performTokenAuthNoCategoryResponse");
    private final static QName _PingEntrustConnResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "pingEntrustConnResponse");
    private final static QName _PerformPasswordAuthResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performPasswordAuthResponse");
    private final static QName _PerformTokenAuthResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performTokenAuthResponse");
    private final static QName _PerformCreateEntrustUser_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performCreateEntrustUser");
    private final static QName _PerformTokenAuthCorporateNoCategoryResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performTokenAuthCorporateNoCategoryResponse");
    private final static QName _PerformCreateEntrustUserResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performCreateEntrustUserResponse");
    private final static QName _PerformDeleteEntrustUserResponse_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performDeleteEntrustUserResponse");
    private final static QName _PerformTokenAuthNoCategory_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performTokenAuthNoCategory");
    private final static QName _PingEntrustConn_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "pingEntrustConn");
    private final static QName _PerformDeleteEntrustUser_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performDeleteEntrustUser");
    private final static QName _PerformPasswordAuth_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performPasswordAuth");
    private final static QName _PerformTokenAuth_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "performTokenAuth");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.expertedge.entrustplugin.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PerformCreateEntrustUser }
     * 
     */
    public PerformCreateEntrustUser createPerformCreateEntrustUser() {
        return new PerformCreateEntrustUser();
    }

    /**
     * Create an instance of {@link PerformTokenAuthCorporateNoCategoryResponse }
     * 
     */
    public PerformTokenAuthCorporateNoCategoryResponse createPerformTokenAuthCorporateNoCategoryResponse() {
        return new PerformTokenAuthCorporateNoCategoryResponse();
    }

    /**
     * Create an instance of {@link PerformTokenAuthNoCategory }
     * 
     */
    public PerformTokenAuthNoCategory createPerformTokenAuthNoCategory() {
        return new PerformTokenAuthNoCategory();
    }

    /**
     * Create an instance of {@link PerformCreateEntrustUserResponse }
     * 
     */
    public PerformCreateEntrustUserResponse createPerformCreateEntrustUserResponse() {
        return new PerformCreateEntrustUserResponse();
    }

    /**
     * Create an instance of {@link PerformDeleteEntrustUserResponse }
     * 
     */
    public PerformDeleteEntrustUserResponse createPerformDeleteEntrustUserResponse() {
        return new PerformDeleteEntrustUserResponse();
    }

    /**
     * Create an instance of {@link PerformTokenAuthCorporateNoCategory }
     * 
     */
    public PerformTokenAuthCorporateNoCategory createPerformTokenAuthCorporateNoCategory() {
        return new PerformTokenAuthCorporateNoCategory();
    }

    /**
     * Create an instance of {@link PerformTokenAuthNoCategoryResponse }
     * 
     */
    public PerformTokenAuthNoCategoryResponse createPerformTokenAuthNoCategoryResponse() {
        return new PerformTokenAuthNoCategoryResponse();
    }

    /**
     * Create an instance of {@link PingEntrustConnResponse }
     * 
     */
    public PingEntrustConnResponse createPingEntrustConnResponse() {
        return new PingEntrustConnResponse();
    }

    /**
     * Create an instance of {@link PerformPasswordAuthResponse }
     * 
     */
    public PerformPasswordAuthResponse createPerformPasswordAuthResponse() {
        return new PerformPasswordAuthResponse();
    }

    /**
     * Create an instance of {@link PerformTokenAuthResponse }
     * 
     */
    public PerformTokenAuthResponse createPerformTokenAuthResponse() {
        return new PerformTokenAuthResponse();
    }

    /**
     * Create an instance of {@link PerformTokenAuth }
     * 
     */
    public PerformTokenAuth createPerformTokenAuth() {
        return new PerformTokenAuth();
    }

    /**
     * Create an instance of {@link PerformDeleteEntrustUser }
     * 
     */
    public PerformDeleteEntrustUser createPerformDeleteEntrustUser() {
        return new PerformDeleteEntrustUser();
    }

    /**
     * Create an instance of {@link PerformPasswordAuth }
     * 
     */
    public PerformPasswordAuth createPerformPasswordAuth() {
        return new PerformPasswordAuth();
    }

    /**
     * Create an instance of {@link PingEntrustConn }
     * 
     */
    public PingEntrustConn createPingEntrustConn() {
        return new PingEntrustConn();
    }

    /**
     * Create an instance of {@link AuthResponseDTO }
     * 
     */
    public AuthResponseDTO createAuthResponseDTO() {
        return new AuthResponseDTO();
    }

    /**
     * Create an instance of {@link PasswordAuthDTO }
     * 
     */
    public PasswordAuthDTO createPasswordAuthDTO() {
        return new PasswordAuthDTO();
    }

    /**
     * Create an instance of {@link AdminResponseDTO }
     * 
     */
    public AdminResponseDTO createAdminResponseDTO() {
        return new AdminResponseDTO();
    }

    /**
     * Create an instance of {@link UserAdminDTO }
     * 
     */
    public UserAdminDTO createUserAdminDTO() {
        return new UserAdminDTO();
    }

    /**
     * Create an instance of {@link TokenAuthCorporateDTO }
     * 
     */
    public TokenAuthCorporateDTO createTokenAuthCorporateDTO() {
        return new TokenAuthCorporateDTO();
    }

    /**
     * Create an instance of {@link UserDelAdminDTO }
     * 
     */
    public UserDelAdminDTO createUserDelAdminDTO() {
        return new UserDelAdminDTO();
    }

    /**
     * Create an instance of {@link TokenAuthDTO }
     * 
     */
    public TokenAuthDTO createTokenAuthDTO() {
        return new TokenAuthDTO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTokenAuthCorporateNoCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performTokenAuthCorporateNoCategory")
    public JAXBElement<PerformTokenAuthCorporateNoCategory> createPerformTokenAuthCorporateNoCategory(PerformTokenAuthCorporateNoCategory value) {
        return new JAXBElement<PerformTokenAuthCorporateNoCategory>(_PerformTokenAuthCorporateNoCategory_QNAME, PerformTokenAuthCorporateNoCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTokenAuthNoCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performTokenAuthNoCategoryResponse")
    public JAXBElement<PerformTokenAuthNoCategoryResponse> createPerformTokenAuthNoCategoryResponse(PerformTokenAuthNoCategoryResponse value) {
        return new JAXBElement<PerformTokenAuthNoCategoryResponse>(_PerformTokenAuthNoCategoryResponse_QNAME, PerformTokenAuthNoCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingEntrustConnResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "pingEntrustConnResponse")
    public JAXBElement<PingEntrustConnResponse> createPingEntrustConnResponse(PingEntrustConnResponse value) {
        return new JAXBElement<PingEntrustConnResponse>(_PingEntrustConnResponse_QNAME, PingEntrustConnResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformPasswordAuthResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performPasswordAuthResponse")
    public JAXBElement<PerformPasswordAuthResponse> createPerformPasswordAuthResponse(PerformPasswordAuthResponse value) {
        return new JAXBElement<PerformPasswordAuthResponse>(_PerformPasswordAuthResponse_QNAME, PerformPasswordAuthResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTokenAuthResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performTokenAuthResponse")
    public JAXBElement<PerformTokenAuthResponse> createPerformTokenAuthResponse(PerformTokenAuthResponse value) {
        return new JAXBElement<PerformTokenAuthResponse>(_PerformTokenAuthResponse_QNAME, PerformTokenAuthResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformCreateEntrustUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performCreateEntrustUser")
    public JAXBElement<PerformCreateEntrustUser> createPerformCreateEntrustUser(PerformCreateEntrustUser value) {
        return new JAXBElement<PerformCreateEntrustUser>(_PerformCreateEntrustUser_QNAME, PerformCreateEntrustUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTokenAuthCorporateNoCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performTokenAuthCorporateNoCategoryResponse")
    public JAXBElement<PerformTokenAuthCorporateNoCategoryResponse> createPerformTokenAuthCorporateNoCategoryResponse(PerformTokenAuthCorporateNoCategoryResponse value) {
        return new JAXBElement<PerformTokenAuthCorporateNoCategoryResponse>(_PerformTokenAuthCorporateNoCategoryResponse_QNAME, PerformTokenAuthCorporateNoCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformCreateEntrustUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performCreateEntrustUserResponse")
    public JAXBElement<PerformCreateEntrustUserResponse> createPerformCreateEntrustUserResponse(PerformCreateEntrustUserResponse value) {
        return new JAXBElement<PerformCreateEntrustUserResponse>(_PerformCreateEntrustUserResponse_QNAME, PerformCreateEntrustUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformDeleteEntrustUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performDeleteEntrustUserResponse")
    public JAXBElement<PerformDeleteEntrustUserResponse> createPerformDeleteEntrustUserResponse(PerformDeleteEntrustUserResponse value) {
        return new JAXBElement<PerformDeleteEntrustUserResponse>(_PerformDeleteEntrustUserResponse_QNAME, PerformDeleteEntrustUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTokenAuthNoCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performTokenAuthNoCategory")
    public JAXBElement<PerformTokenAuthNoCategory> createPerformTokenAuthNoCategory(PerformTokenAuthNoCategory value) {
        return new JAXBElement<PerformTokenAuthNoCategory>(_PerformTokenAuthNoCategory_QNAME, PerformTokenAuthNoCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingEntrustConn }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "pingEntrustConn")
    public JAXBElement<PingEntrustConn> createPingEntrustConn(PingEntrustConn value) {
        return new JAXBElement<PingEntrustConn>(_PingEntrustConn_QNAME, PingEntrustConn.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformDeleteEntrustUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performDeleteEntrustUser")
    public JAXBElement<PerformDeleteEntrustUser> createPerformDeleteEntrustUser(PerformDeleteEntrustUser value) {
        return new JAXBElement<PerformDeleteEntrustUser>(_PerformDeleteEntrustUser_QNAME, PerformDeleteEntrustUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformPasswordAuth }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performPasswordAuth")
    public JAXBElement<PerformPasswordAuth> createPerformPasswordAuth(PerformPasswordAuth value) {
        return new JAXBElement<PerformPasswordAuth>(_PerformPasswordAuth_QNAME, PerformPasswordAuth.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PerformTokenAuth }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.entrustplugin.expertedge.com/", name = "performTokenAuth")
    public JAXBElement<PerformTokenAuth> createPerformTokenAuth(PerformTokenAuth value) {
        return new JAXBElement<PerformTokenAuth>(_PerformTokenAuth_QNAME, PerformTokenAuth.class, null, value);
    }

}
