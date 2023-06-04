<!--
错误提示图片地址：
数据为空错误： https://file.lantingshucheng.com/myapp/err_tips/empty_err.png
网络错误：https://file.lantingshucheng.com/myapp/err_tips/network_err.png
未登录错误：https://file.lantingshucheng.com/myapp/err_tips/nologin_err.png
邮件为空错误：https://file.lantingshucheng.com/myapp/err_tips/mail_err.png
开发中错误：https://file.lantingshucheng.com/myapp/err_tips/developing_err.png
 -->
<template name="err_page">
  <view :hidden="!err_page_data.show" class="err_page">
  	  <image :src="err_page_data.image_url"></image>
      <view class="page_des">{{err_page_data.text}}</view>
      <button class="page_button" @click="err_page_data.click">{{err_page_data.buttonText}}</button>
  </view>
</template>

<script>
	export default {
	}
</script>

<style lang="stylus">
.err_page{
	position: absolute;
	z-index: 10000;
	background: #fff;
	height: 100%;
	width: 100%;
	top: 0;
	left: 0;
	text-align: center;
	color: #9c9c9c;
	font-size: 30rpx;
}
.err_page > image{
	width: 330rpx;
	height: 330rpx;
	margin-top: 40%;
	margin-bottom: 30rpx;
}
.err_page > button{
	margin-top: 40rpx;
	width: 180rpx;
	line-height: 60rpx;
	height: 60rpx;
	font-size: 32rpx;
	color: #fd9941;
	border: 2rpx solid #fd9941;
	border-radius: 8rpx;
}
</style>
