package com.fiberhome.filink.picture.exception;


/**
 * <p>
 * DeletePicRelationException 删除图片记录异常
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-21
 */
public class DeletePicRelationException extends RuntimeException {

    public DeletePicRelationException(){};

    public DeletePicRelationException(String msg) {
        super(msg);
    }
}
