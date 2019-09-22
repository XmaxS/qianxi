package com.horizon.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnums {

    CATEGORY_NOT_FOUND(404,"商品分类不存在"),
    Brand_NOT_FOUND(404,"品牌不存在"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(400,"文件类型错误"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没查到"),
    SPEC_PARAM_NOT_FOUND(404,"商品参数没查到"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    GOODS_DETAIL_NOT_FOUND(404,"商品详情不存在"),
    GOODS_SKU_NOT_FOUND(404,"商品Sku不存在"),
    GOODS_STOCK_NOT_FOUND(404,"商品库存不存在"),
    GOODS_SAVE_ERROR(500,"新增商品失败"),
    GOODS_ID_CANNOT_BE_NULL(500,"商品ID不能为空"),
    GOODS_UPDATE_ERROR(500,"修改商品失败"),
    INVALID_VERIFY_CODE(400,"无效的验证码"),
    USERNAME_OR_PASSWORD_ERROR(400,"用户名或密码错误"),
    INVALID_USERNAME_PASSWORD(400,"用户名或密码错误"),
    RECEIVER_ADDRESS_NOT_FOUND(400,"收货地址未找到"),
    ORDER_STATUS_EXCEPTION(400,"订单状态异常"),
    CREATE_PAY_URL_ERROR(400,"创建支付失败"),
    ORDER_NOT_FOUND(400,"订单没找到"),
    WX_PAY_SIGN_INVALID(400,"微信支付异常"),
    WX_PAY_NOTIFY_PARAM_ERROR(400,"微信支付参数异常"),
    INVAILD_USER_DATA_TYPE(400,"用户数据类型不正确")
    ;
    private int code;
    private String msg;
}
