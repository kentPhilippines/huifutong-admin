package test.demo.bean;

/**
 * <p>����ʵ��</p>
 * @author K
 */
public class Deal {
	/**
	 * ######################
	 * 	�ֶ���		�Ƿ����ǩ��   �Ƿ����
	 * 	appid			��			��		24		�̻��ţ����磺11396	�̻����׺�
		orderid			��			��		128		�����ţ�����Ψһ	ϵͳ��֤��ʽ appid+orderid����֤Ψһ�ԡ�
		notifyurl		��			��		96		�ص���ַ	�ص���ַ������72���ַ�
		pageUrl			��			��		124		ͬ�����ص�ַ��H5���Ʒ�ش���	֧���ɹ�����ת�ĵ�ַ
		amount			��			��		-		֧�����, ��λ  �� Ԫ
		passcode		��			��		24		ͨ�����룺PAY1005��֧����ɨ�롿	��Ʒ���ͱ�š���ѯ��Ӫ��
		rsasign			��			��		-		ǩ���ַ������ܣ����ܷ�ʽ�ο�ƽ̨��Demo	ǩ��
		userid			��			��		124		�����û�Ψһ��ʶ	������Ϊ�̻�����ұ�ʶ��������߳ɹ��ʣ�
		bankCode 		��			��		24		���б���	�磺 bankCode=ICBC������֧��ʱ����
		subject 		��			��		124		��Ʒ����	
		applydate		��			��		-		����ʱ�䣬ʱ���ʽ��yyyyMMddHHmmss	��ʱ���ʽ���������쳣��
	 */
	private String appId;
	private String orderId;
	private String notifyUrl;
	private String pageUrl;
	private String amount;
	private String passCode;
	private String sign ; 
	private String applyDate;
	private String subject;
	private String userid;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPassCode() {
		return passCode;
	}
	public void setPassCode(String passCode) {
		this.passCode = passCode;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
}
