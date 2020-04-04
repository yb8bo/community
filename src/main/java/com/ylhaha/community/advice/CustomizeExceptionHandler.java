package com.ylhaha.community.advice;

import com.ylhaha.community.dto.ResultDTO;
import com.ylhaha.community.exception.CustomizeErrorCode;
import com.ylhaha.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yl
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    Object handler(Throwable e, Model model, HttpServletRequest request){
        //获取请求内容
        String contentType = request.getContentType();
        //根据请求内容为json/html返回给客户端json/html
        if("application/json".equals(contentType)){
            if (e instanceof CustomizeException){
                return ResultDTO.errorOf((CustomizeException)e);
            }else {
                return ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
        }else {
            if (e instanceof CustomizeException){
                model.addAttribute("message",e.getMessage());
            }else {
                model.addAttribute("message",CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}
