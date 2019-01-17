package cn.kidtop.framework.qiniu;

import cn.kidtop.framework.http.HttpClient;
import cn.kidtop.framework.qiniu.result.DelFileResultDTO;
import cn.kidtop.framework.util.JsonUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * 七牛服务
 *
 * @version 1.0
 * @date: 2017-09-15 16:05:28
 * @author:wangfushun
 */

public class QiNiuService {

    private static QiNiuService qiNiuService = new QiNiuService();
    private Logger logger = LoggerFactory.getLogger(QiNiuService.class);
    ;

    private QiNiuService() {
    }

    public static QiNiuService init() {
        return qiNiuService;
    }

    public static void main(String[] args) {
        QiNiuConfig.init().setAccessKey("8T432QAc0etJ9jEbdVkoZtrbbwr-Ryh-rjF1I7IN");
        QiNiuConfig.init().setSecretKey("LEdLtYIwcYAZyv3jFIt_bkkTWwblBJ8tEfa44Lve");
        QiNiuConfig.init().setBucket("devtopkid");
        QiNiuConfig.init().setImagePreUrl("http://oz5c1a8kf.bkt.clouddn.com/");
        QiNiuService qiNiuService = new QiNiuService();
        String fileInfo = qiNiuService.getFileInfo("daf84c1e6ffa4139b3a4b7f70b4dcba9d");
        System.out.println(fileInfo);
    }

    /**
     * 获取上传凭证
     *
     * @return
     * @date: 2017-09-15 16:05:56
     * @author:wangfushun
     * @version 1.0
     */
    public String getFileToken() {
        String bucket = QiNiuConfig.init().getBucket();
        Auth auth = Auth.create(QiNiuConfig.init().getAccessKey(), QiNiuConfig.init().getSecretKey());
        StringMap putPolicy = new StringMap();
//		//数据处理指令，支持多个指令
//		String saveMp4Entry = String.format("%s:avthumb_test_target.mp4", bucket);
//		String saveJpgEntry = String.format("%s:vframe_test_target.jpg", bucket);
//		String avthumbMp4Fop = String.format("avthumb/mp4|saveas/%s", UrlSafeBase64.encodeToString(saveMp4Entry));
//		String vframeJpgFop = String.format("vframe/jpg/offset/1|saveas/%s", UrlSafeBase64.encodeToString(saveJpgEntry));
//		//将多个数据处理指令拼接起来
//		String persistentOpfs = StringUtils.join(new String[]{
//		        avthumbMp4Fop, vframeJpgFop
//		}, ";");
//		putPolicy.put("persistentOps", persistentOpfs);
//		//数据处理队列名称，必填
//		putPolicy.put("persistentPipeline", "mps-pipe1");
//		//数据处理完成结果通知地址
//		putPolicy.put("persistentNotifyUrl", "http://api.example.com/qiniu/pfop/notify");
        //回调地址
//		if(ObjectUtils.isNotEmpty(systemConfigFacade.getSystemConfigByCode("IMAGE_UPLOAD_CALL_BACK_URL").getValue())){
//			putPolicy.put("callbackUrl", systemConfigFacade.getSystemConfigByCode("IMAGE_UPLOAD_CALL_BACK_URL").getValue());
//			//回调参数imageInfo
//			putPolicy.put("callbackBody", "{\"key\":\"$(key)\",\"imageInfo\":\"$(imageInfo)\",\"avInfo\":\"$(avinfo)\",\"id\":\""+fileId+"\",\"fileType\":"+fileType+",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"size\":$(fsize),\"name\":$(fname),\"ext\":$(ext),\"mimeType\":$(mimeType)}");
//			putPolicy.put("callbackBodyType", "application/json");
//		}else{
//			putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"imageInfo\":\"$(imageInfo)\",\"avInfo\":\"$(avinfo)\",\"id\":\""+fileId+"\",\"fileType\":"+fileType+",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"size\":$(fsize),\"name\":$(fname),\"ext\":$(ext),\"mimeType\":$(mimeType)}");
//		}
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"imageInfo\":$(imageInfo),\"avInfo\":$(avinfo),\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"size\":\"$(fsize)\",\"name\":\"$(fname)\",\"ext\":\"$(ext)\",\"mimeType\":\"$(mimeType)\"}");
        long expireSeconds = 3600L;
        String upToken = auth.uploadToken(bucket, null, expireSeconds, putPolicy);
//		System.out.println(upToken);
        return upToken;
    }

    /**
     * 批量删除文件
     *
     * @param keyList 1000个
     * @date: 2017-09-15 15:48:19
     * @author:wangfushun
     * @version 1.0
     */
    public DelFileResultDTO butchDelFile(String[] keyList) {
        DelFileResultDTO delFileResultDTO = new DelFileResultDTO();
        if (keyList.length > 1000) {
            delFileResultDTO.setAllFail(true);
            return delFileResultDTO;
        }
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        //...其他参数参考类注释

        Auth auth = Auth.create(QiNiuConfig.init().getAccessKey(), QiNiuConfig.init().getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            //单次批量请求的文件数量不得超过1000
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(QiNiuConfig.init().getBucket(), keyList);
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
            for (int i = 0; i < keyList.length; i++) {
                BatchStatus status = batchStatusList[i];
                String key = keyList[i];
                if (status.code == 200) {
                    delFileResultDTO.addSuccess(key);
                } else {
                    delFileResultDTO.addFail(key);
                }
            }
        } catch (QiniuException ex) {
            delFileResultDTO.setAllFail(true);
        }

        return delFileResultDTO;
    }

    /**
     * 上传图片
     *
     * @param b
     * @param key 默认不指定key的情况下，以文件内容的hash值作为文件名
     * @date: 2017-11-09 17:06:43
     * @author:wangfushun
     * @version 1.0
     */
    public String upload(byte[] b, String key) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        String upToken = getFileToken();
        //...生成上传凭证，然后准备上传
        try {
            Response response = uploadManager.put(b, key, upToken);
            //解析上传成功的结果
            if (!response.isOK()) {
                logger.debug("七牛上传错误" + JsonUtil.classToJson(response));
                throw new QiNiuExceptin(QiNiuExceptin.Enum.UPLOAD_IMAGE_ERROR_EXCEPTION);
            }

            return response.bodyString();
        } catch (QiniuException e) {
            throw new QiNiuExceptin(QiNiuExceptin.Enum.UPLOAD_IMAGE_ERROR_EXCEPTION);
        }
    }

    /**
     * 获取文件对象
     *
     * @param key
     * @return
     * @date: 2017-11-15 15:12:25
     * @author:wangfushun
     * @version 1.0
     */
    public String getFileInfo(String key) {
//		//构造一个带指定Zone对象的配置类
//		Configuration cfg = new Configuration(Zone.zone2());
//		//...其他参数参考类注释
//
//		Auth auth = Auth.create(QiNiuConfig.init().getAccessKey(), QiNiuConfig.init().getSecretKey());
//		BucketManager bucketManager = new BucketManager(auth, cfg);
//		 FileInfo fileInfo;
        String str = null;
        try {
            String url = QiNiuConfig.init().getImagePreUrl() + key + "?imageInfo";
            HttpClient httpClient = new HttpClient(url, "utf-8");
            str = httpClient.get();
        } catch (KeyManagementException | MalformedURLException | NoSuchAlgorithmException e) {
        } catch (IOException e) {
        }
        return str;
    }
}
