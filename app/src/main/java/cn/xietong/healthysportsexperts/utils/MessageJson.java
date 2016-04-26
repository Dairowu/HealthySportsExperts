package cn.xietong.healthysportsexperts.utils;

/**
 * 
 * @author 林思旭，2016.4.9
 *
 */
public class MessageJson {

	private String fid;
	private String ft;
	private String mc;
	private String tId;

	public void settId(String tId) {
		this.tId = tId;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public void setFt(String ft) {
		this.ft = ft;
	}
	public void setMc(String mc) {
		this.mc = mc;
	}
	public String gettId() {
		return tId;
	}

	
	public String getFid() {
		return fid;
	}

	public String getFt() {
		return ft;
	}

	public String getMc() {
		return mc;
	}

	
	
}
