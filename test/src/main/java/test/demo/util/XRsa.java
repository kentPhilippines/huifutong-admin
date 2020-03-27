package test.demo.util;

import cn.hutool.Hutool;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;


import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;

public class XRsa {
	public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String RSA_ALGORITHM_SIGN = "SHA256WithRSA";
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    public static void main(String[] args) {
    	Map<String, String> createKeys = createKeys(512);
    	String publicKey = createKeys.get("publicKey");
    	String privateKey = createKeys.get("privateKey");
    	System.out.println(publicKey);
    	System.out.println(privateKey);
    	
	}
    public XRsa(String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //ͨ��X509�����Keyָ���ù�Կ����
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
            //ͨ��PKCS#8�����Keyָ����˽Կ����
        } catch (Exception e) {
            throw new RuntimeException("��֧�ֵ���Կ", e);
        }
    }
 
    public static Map<String, String> createKeys(int keySize){
        //ΪRSA�㷨����һ��KeyPairGenerator����
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }
        //��ʼ��KeyPairGenerator����,��Ҫ��initialize()Դ���������ƭ,��ʵ����������size����Ч��
        kpg.initialize(keySize);
        //�����ܳ׶�
        KeyPair keyPair = kpg.generateKeyPair();
        //�õ���Կ
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //�õ�˽Կ
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
 
        return keyPairMap;
    }
 
    /**
     * <p>��Կ����</p>
     * @param data
     * @return
     */
    public String publicEncrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("�����ַ���[" + data + "]ʱ�����쳣", e);
        }
    }
 
    /**
     * <p>˽Կ����</p>
     * @param data
     * @return
     */
    public String privateDecrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("�����ַ���[" + data + "]ʱ�����쳣", e);
        }
    }
 
    /**
     * <p>˽Կ����</p>
     * @param data
     * @return
     */
    public String privateEncrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("�����ַ���[" + data + "]ʱ�����쳣", e);
        }
    }
    /**
     * <p>��Կ����</p>
     * @param data
     * @return
     */
    public String publicDecrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("�����ַ���[" + data + "]ʱ�����쳣", e);
        }
    }
 
    /**
     * <p>ǩ��</p>
     * @param data
     * @return
     */
    public String sign(String data){
        try{
            //sign
            Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.encodeBase64URLSafeString(signature.sign());
        }catch(Exception e){
            throw new RuntimeException("ǩ���ַ���[" + data + "]ʱ�����쳣", e);
        }
    }
 
    /**
     * <p>��ǩ�ķ���</p>
     * @param data
     * @param sign
     * @return
     */
    public boolean verify(String data, String sign){
        try{
            Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        }catch(Exception e){
            throw new RuntimeException("��ǩ�ַ���[" + data + "]ʱ�����쳣", e);
        }
    }
 
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE)
            maxBlock = keySize / 8;
        else
            maxBlock = keySize / 8 - 11;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock)
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                else
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("�ӽ��ܷ�ֵΪ["+maxBlock+"]������ʱ�����쳣", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

}
