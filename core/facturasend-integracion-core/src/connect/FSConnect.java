package connect;

public class FSConnect {
	private boolean isEnabled;
	private String url;
	private Integer authorization;
	private boolean isSincrono;
	private String kudeFolder;
	private String xmlFolder;
	
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getAuthorization() {
		return authorization;
	}
	public void setAuthorization(Integer authorization) {
		this.authorization = authorization;
	}
	public boolean isSincrono() {
		return isSincrono;
	}
	public void setSincrono(boolean isSincrono) {
		this.isSincrono = isSincrono;
	}
	public String getKudeFolder() {
		return kudeFolder;
	}
	public void setKudeFolder(String kudeFolder) {
		this.kudeFolder = kudeFolder;
	}
	public String getXmlFolder() {
		return xmlFolder;
	}
	public void setXmlFolder(String xmlFolder) {
		this.xmlFolder = xmlFolder;
	}
	
}
