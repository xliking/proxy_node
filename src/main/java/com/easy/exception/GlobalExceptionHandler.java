package com.easy.exception;

import com.easy.resutils.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value =BusinessException.class)
    public R handleException(BusinessException e) {
        e.printStackTrace();
        R resultValue = new R();
        resultValue.setCode(e.getErrorCode());
        resultValue.setMsg(e.getMessage());
        return resultValue;
    }

    /**
     * 400
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Object handleException400(HttpServletRequest req, HttpMessageNotReadableException e) throws Exception {
        e.printStackTrace();
        return R.failed("请求的参数中有数据类型或数据格式错误");
    }
    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    public R exceptionHandler(HttpServletRequest req, Exception e){
        e.printStackTrace();
        return R.failed();
    }

    @ExceptionHandler
    public R getMethodArgumentNotValidExceptionException(MethodArgumentNotValidException exception) {
        log.error("Global MethodArgumentNotValidException:",exception);
        MethodArgumentNotValidException exs = exception;
        BindingResult bindingResult = exs.getBindingResult();
        StringBuffer sb = new StringBuffer();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage() + ",");
        }
        return R.failed(sb.substring(0, sb.length()-1));
    }
}
