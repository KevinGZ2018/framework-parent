package com.fushun.framework.util.exception.exception;


import com.fushun.framework.util.exception.base.BaseCustomizeMessageExceptionEnum;
import com.fushun.framework.util.exception.base.BaseExceptionEnum;
import com.fushun.framework.util.exception.base.SpringContextUtil;
import com.fushun.framework.util.exception.message.CodeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    protected String detailMessage;
    private Logger logger = LoggerFactory.getLogger(BaseException.class);
    private long errorCode;
    private Long stepNo = 1000000L;

    /**
     * @param baseExceptionEnum 定义的异常错误
     * @return
     * @date:
     * @author:
     * @version 1.0
     */
    public BaseException(BaseExceptionEnum baseExceptionEnum) {
        initCodeMessage(baseExceptionEnum.getText(), baseExceptionEnum);
    }

    /**
     * @param message                           自定义的错误信息
     * @param baseCustomizeMessageExceptionEnum 自定义异常错误
     * @return
     * @date:
     * @author:
     * @version 1.0
     */
    public BaseException(String message, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        initCodeMessage(message, baseCustomizeMessageExceptionEnum);
    }

    /**
     * 定期检查此实现抛出的异常，是否有可以优化
     *
     * @param message                           自定义的错误信息
     * @param cause                             抓到异常
     * @param baseCustomizeMessageExceptionEnum 自定义异常错误
     * @return
     * @date:
     * @author:
     * @version 1.0
     */
    public BaseException(String message, Throwable cause, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        if (cause != null) {
            this.initCause(cause);
        }
        initCodeMessage(message, baseCustomizeMessageExceptionEnum);
    }

    /**
     * @param cause             抓到异常
     * @param baseExceptionEnum 定义的异常错误
     * @return
     * @date:
     * @author:
     * @version 1.0
     */
    public BaseException(Throwable cause, BaseExceptionEnum baseExceptionEnum) {
        if (cause != null) {
            this.initCause(cause);
        }
        initCodeMessage(baseExceptionEnum.getText(), baseExceptionEnum);
    }

    private BaseException() {
        super();
    }

    /**
     * @param message           错误信息
     * @param baseExceptionEnum 错误吗
     * @return void
     * @date: 2018年12月16日16时06分
     * @author:wangfushun
     * @version 1.0
     */
    private void initCodeMessage(String message, BaseExceptionEnum baseExceptionEnum) {
        Long code = getBaseNo() * stepNo + baseExceptionEnum.getCode();
        CodeMessage codeMessage = SpringContextUtil.getBean(CodeMessage.class);
        String msg = "";
        if (codeMessage != null) {
            try {
                msg = codeMessage.getMessageByCodeNo(code);
            } catch (Exception e) {
                msg = codeMessage.getMessageForRedis(code);
            }
            if (msg != null && !"".equals(msg)) {
                logger.error("application error msg:[{}]", message);
                message = msg;
            }
        }

        this.detailMessage = message;
        this.errorCode = code;
    }

    /**
     * 获取异常的基础错误号，采用同级分段的方式。<br/>
     * 1级：基数*1000000个错误<br/>
     * 2级：在一级的基础下分段（也就是在1000000中分段）依次类推，<br/>
     * 但是除第一级子类需要实现此方法，二级子类只需要在BaseExceptionEnum.code值进行分段
     * 如：
     * <pre>
     *   class A extends BaseException{
     *       public static enum Enum implements BaseExceptionEnum {
     *       	BASECODE_EXCEPTION(1,"基础错误")
     *       };
     *
     *       protected int getBaseNo(){
     *           return 1;//定义异常的基数，此数会乘以1000000
     *       }
     *   };
     *   class B extends A{
     *       public static enum Enum implements BaseExceptionEnum {
     *       	BASECODE_EXCEPTION(1000,"基础错误")//子类占用父类的可用的no
     *       };
     *   };
     *   class C extends A{
     *       public static enum Enum implements BaseExceptionEnum {
     *       	BASECODE_EXCEPTION(2000,"基础错误")//子类占用父类的可用的no
     *       };
     *   };
     *   class D extends BaseException{
     *       public static enum Enum implements BaseExceptionEnum {
     *       	BASECODE_EXCEPTION(1,"基础错误")//
     *       };
     *       protected int getBaseNo(){
     *           return 2;//定义异常的基数，此数会乘以1000000
     *       }
     *   };
     *   </pre>
     *
     * @retun int
     * @date: 2018年12月16日17时06分
     * @author:wangfushun
     * @version 1.0
     */
    protected abstract long getBaseNo();

    /**
     * @return int 占用的区间  baseNo*1000000-endBaseNo*1000000
     * @date: 2018年12月16日17时32分
     * @author:wangfushun
     * @version 1.0
     */
    protected long getEndBaseNo() {
        return 0;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return this.detailMessage;
    }

}
