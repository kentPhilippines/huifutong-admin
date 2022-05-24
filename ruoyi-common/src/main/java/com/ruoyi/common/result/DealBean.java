package com.ruoyi.common.result;


public class DealBean {

	private String msg;
	private String url;
	private Object obj;
	private Integer cod;// 1成功  2 不成功
	
	public static DealBean DealBeanSu(String msg, String url, Object obj) {
		return new DealBean(msg,url,obj,1);
	}
	
	public DealBean(String msg, String url, Object obj, Integer cod) {
		super();
		this.msg = msg;
		this.url = url;
		this.obj = obj;
		this.cod = cod;
	}



	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public Integer getCod() {
		return cod;
	}
	public void setCod(Integer cod) {
		this.cod = cod;
	}

}
