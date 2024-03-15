package eu.fluidos.traslator;
import eu.fluidos.jaxb.CIDRSelector;


public class Ruleinfo {
    private String sourcePod;
    private String sourceNamespace;
    private CIDRSelector cidrSource;
    private String destinationPod;
    private String destinationNamespace;
    private CIDRSelector cidrDestination;
    private String port;
    private String protocol;

    public Ruleinfo(String sourcePod, String sourceNamespace, CIDRSelector cidrSource,
                    String destinationPod, String destinationNamespace, CIDRSelector cidrDestination,String port,String protocol) {
        this.sourcePod = sourcePod;
        this.sourceNamespace = sourceNamespace;
        this.cidrSource = cidrSource;
        this.destinationPod = destinationPod;
        this.destinationNamespace = destinationNamespace;
        this.cidrDestination = cidrDestination;
        this.port = port;
        this.protocol=protocol;
    }

    public String getSourcePod() {
        return sourcePod;
    }

    public void setSourcePod(String sourcePod) {
        this.sourcePod = sourcePod;
    }

    public String getSourceNamespace() {
        return sourceNamespace;
    }

    public void setSourceNamespace(String sourceNamespace) {
        this.sourceNamespace = sourceNamespace;
    }

    public CIDRSelector getCidrSource() {
        return cidrSource;
    }

    public void setCidrSource(CIDRSelector cidrSource) {
        this.cidrSource = cidrSource;
    }

    public String getDestinationPod() {
        return destinationPod;
    }

    public void setDestinationPod(String destinationPod) {
        this.destinationPod = destinationPod;
    }

    public String getDestinationNamespace() {
        return destinationNamespace;
    }

    public void setDestinationNamespace(String destinationNamespace) {
        this.destinationNamespace = destinationNamespace;
    }

    public CIDRSelector getCidrDestination() {
        return cidrDestination;
    }

    public void setCidrDestination(CIDRSelector cidrDestination) {
        this.cidrDestination = cidrDestination;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
