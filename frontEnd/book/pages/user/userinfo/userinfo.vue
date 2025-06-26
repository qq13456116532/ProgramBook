<template>
	<view class="userInfo-container">
		<view class="err-tips" :hidden="!errTips">{{errTips}}</view>
		<form bindsubmit="formSubmit">
		  <image class="headImg" :src="avatar" @error="headimgError" @click="uploadAvatar" ></image>
		  <view class="split-text">基本信息</view>
		  <view class="section">
		    <view class="section-title">昵称</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color:' + nickname ? '#000' : '#666'">{{nickname || '给自己起个响亮的名字吧'}}</view>
		    <input name="input" placeholder="请输入昵称" data-type="nickname" @input="userInput" v-model="nickname" v-if="!changeOrSubmit" />
		  </view>
		  <view class="section">
		    <view class="section-title">邮箱</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color: ' + email ? '#000' : '#666'">{{email || '未设置邮箱'}}</view>
		    <input name="input" placeholder="请输入常用邮箱" data-type="email" @input="userInput" v-model="email" v-if="!changeOrSubmit" />
		  </view>
		  <view class="split-text">可选信息</view>
		  <view class="section">
		    <view class="section-title">姓名</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color:'+  realm ? '#000' : '#666'">{{realm || '未设置姓名'}}</view>
		    <input name="input" placeholder="请输入自己的姓名" data-type="realm" @input="userInput" v-model="realm" v-if="!changeOrSubmit" />
		  </view>
		<view class="section">
		    <view class="section-title">年龄</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color: '+ (age > 0) ? '#000' : '#666'">{{(age > 0) ? age : '未设置年龄'}}</view>
		    <input name="input" placeholder="请输入年龄" data-type="age" @input="userInput" v-model="age" v-if="!changeOrSubmit" />
		  </view>
		  <view class="section">
		    <view class="section-title">生日</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color: '+ birthday ? '#000' : '#666'">{{birthday || '未设置出生日期'}}</view>
		    <picker mode="date" value="" start="1990-09-01" end="2017-09-01" data-type="birthday" @change="userInput" v-model="birthday" v-if="!changeOrSubmit" >
		    	<view class="picker" :style="'color:' + birthday ? '#000;' : '#888;'">{{birthday || '请输入出生日期'}}</view>
		    </picker>
		  </view>
		 <view class="section">
		    <view class="section-title">所在地</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color:'+ address ? '#000' : '#666'">{{address || '未设置所在地'}}</view>
		    <input name="input" placeholder="请输入当前所在位置" data-type="address" @input="userInput" v-model="address"  v-if="!changeOrSubmit" />
		  </view>
		 <view class="section">
		    <view class="section-title">个人签名</view>
		    <view class="display" v-if="changeOrSubmit" :style="'color: '+ signature ? '#000' : '#666'">{{signature || '未设置签名'}}</view>
		    <input name="input" placeholder="请输入个人签名" data-type="signature" @input="userInput" v-model="signature" v-if="!changeOrSubmit" />
		  </view>
		  <view class="btn-area">
		    <button formType="submit" type="primary">{{changeOrSubmit ? '修改' : '提交'}}</button>
		  </view>
		</form>
	</view>
</template>

<script>
	//today.js
	var Api = require('../../../utils/api/api');
	var util = require('../../../utils/util');
	var qiniuUploader = require('../../../utils/qiniuUpload');
	export default {
		data() {
			return {
				 age: '',
				    nickname: '',
				    birthday: '',
				    signature: '',
				    address: '',
				    realm: '',
				    email: '',
				    avatar: '',
				    hasReadTime: 0,
				    continueReadDay: 0,
				    hasBookNum: 0,
				    changeOrSubmit: true, // 用户正在执行的操作，是修改（true）还是已经修改好了准备提交（false）
				    errTips: '',
				    err_tips_data: { err_tips_show: false, err_tips_text: '' },
				    err_page_data: null, //app状态页
				    is_click_img: false // 是否点击了上传头像
			};
		},
		  onLoad: function () {
		    var self = this;
		    // 从缓存中读取用户信息
		    uni.getStorage({
		      key: 'userInfo',
		      success: function (res) {
				  this.avatar = res.data.avatar
				  this.hasReadTime = res.data.hasReadTime
				  this.continueReadDay = res.data.continueReadDay
				  this.hasBookNum=res.data.books
				  this.address = res.data.address
				  this.signature= res.data.signature
		      this.email =res.data.email
			  this.nickname = res.data.nickName
			  this.birthday =  res.data.birthday
			  this.realm = res.data.realm
			  this.age = (res.data.age > 0 ? res.data.age : '')
	
		        console.log('从缓存中读取个人信息成功');
		      }
		    });
		  },
		methods:{
			// 初始化七牛相关参数
			 initQiniu: function(callback) {
			  var options = {
			    region: 'SCN', // 华东区，生产环境应换成自己七牛帐户bucket的区域
			    uptokenURL: Api.getUploadToken(), // 生产环境该地址应换成自己七牛帐户的token地址，具体配置请见server端
			    domain: 'https://file.lantingshucheng.com/' // 生产环境该地址应换成自己七牛帐户对象存储的域名
			  };
			  qiniuUploader.init(options);
			},
			 headimgError: function () {
			  this.avatar = "https://file.lantingshucheng.com/myApp/unknown_headimg.png?imageView2/1/w/60/h/60/format/jpg/interlace/1/q/75|imageslim"
			  },
			  // 表单提交事件
			  formSubmit: function () {
			    var self = this;
			    // 只有当前处于提交状态才会执行下面的代码
			    if(!self.changeOrSubmit){
			      // 校验合法性
			      var emailReg = /^[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?$/;
			      var passwordReg = /^[a-zA-Z\d_]{8,}$/; //最少8位
			      var nickNameReg = /^[\u4e00-\u9fa5\w]{2,8}$/; //2-8位数字或者字母或者中文
			      var realNameReg = /^[\u4e00-\u9fa5\w]{2,8}$/; //2-8位数字或者字母或者中文
			      var cityReg = /^[\u4e00-\u9fa5\w ]{2,18}$/; //2-10位数字或者字母或者中文
			      var numberReg = /\d+/;
			      if (nickNameReg.test(self.nickname)) {
			        if (emailReg.test(self.email)) {
			          var updateData = {};
			          updateData.nickname = self.nickname;
			          updateData.email = self.email;
			          if(self.realm && realNameReg.test(self.realm)){
			            updateData.realm = self.realm;
			          }
			          if(self.age && numberReg.test(self.age)){
			            updateData.age = self.age;
			          }
			          if(self.birthday && (typeof self.birthday === 'string')){
			            updateData.birthday = self.birthday;
			          }
			          if(self.address && cityReg.test(self.address)){
			            updateData.address = self.address;
			          }
			          if(self.signature && (typeof self.signature === 'string')){
			            updateData.signature = self.signature;
			          }
			          if(self.avatar && self.is_click_img){
			            updateData.avatar = self.avatar;
			          }
			          var userid = uni.getStorageSync('id').userid;
			          uni.request({
			            url: Api.updateUserInfo(),
			            method: 'POST',
			            data: {userid: userid, info: updateData},
			            success: function (res) {
			              var tmpData = res.data.data;
			              //注册成功，缓存userid和openid
			              if(tmpData.code == 0){
			                console.log('更新个人信息成功');
							self.changeOrSubmit = true
			                //更新缓存中的userInfo
			                self.getUserInfo(userid);
			              }
			            },
			            fail: function (err) {
			              console.log('注册失败， ' + err);
						  this.errTips =  '更新个人信息失败'
						  this.changeOrSubmit = false
			            }
			          });
			        } else {
						this.errTips ="请输入有效邮箱"
						this.changeOrSubmit = false
			        }
			      } else {
					  this.errTips ="请输入2-10字母或、数字或者中文组成的昵称"
					  this.changeOrSubmit = false
			      }
			    }else{
					this.changeOrSubmit = !self.data.changeOrSubmit
			    }
			  },
			  getUserInfo: function (userid) {
			    uni.request({
			      url: Api.getUserInfo(userid),
			      success: function (res) {
			        var tmpData = res.data.data;
			        if (tmpData && tmpData.code == 0) {
			          //将书单数据缓存到本地
			          uni.setStorage({
			            key: 'userInfo',
			            data: tmpData.info,
			            success: function (res) {
			              console.log('成功更新本地缓存中用户信息');
			            }
			          });
			        } else {
			          console.log('请求用户信息失败....');
			          Util.showErrMsg(self, '获取个人信息失败', 1000);
			        }
			      },
			      fail: function (err) {
			        console.log(err);
					this.err_page_data = {
			            show: true,
			            image_url: 'https://file.lantingshucheng.com/myapp/err_tips/network_err.png',
			            text: '努力找不到网络>_<请检查后重试',
			            buttonText: '重试',
			            click: 'getUserInfo'
			          }
			      }
			    });
			  },
			  uploadAvatar: function () {
			    uni.hideToast();
			    uni.showToast({ title: '上传中', icon: 'loading', duration: 300 })
			    initQiniu();
			    var self = this;
				this.is_click_img =false;
			    uni.chooseImage({
			      sourceType: ['camera', 'album'],
			      sizeType: ['compressed'], //压缩图片
			      count: 1, //最多一张
			      success: function (res) {
			        //七牛上传文件
			        var filePath = res.tempFilePaths[0];
			        // self.setData({avatar: res.tempFilePaths[0]});
			        qiniuUploader.upload(filePath, (res) => {
			          console.log(res);
			          //更新图片地址
					  this.avatar =res.imageURL
					  this.is_click_img = true
					  			          uni.hideToast();
			          uni.showToast({ title: '上传成功', icon: 'success', duration: 2000 });
			          setTimeout(function () { uni.hideToast() }, 2000)
			        }, (error) => {
			          console.error('error: ' + JSON.stringify(error));
			        }, uni.getStorageSync('id').openid);
			      },
			      fail: function (err) {
			        console.log("选择图片失败, " + err);
			      }
			    })
			  },
			  // 生日输入框的输入事件
			  userInput: function(e){
			    var self = this;
			    var type = e.target.type;
			    switch(type){
			      case 'nickname':
				  this.nickName  = e.detail.value
			        break;
			      case 'email':
				  this.email = e.detail.value
			        break;
			      case 'realm':
				  this.realm = e.detail.value
			        break;
			      case 'age':
				  this.age = e.detail.value
			        break;
			      case 'birthday':
				  this.birthday = e.detail.value
			        break;
			      case 'address':
				  this.address = e.detail.value
			        break;
			      case 'signature':
				  this.signature = e.detail.value
			        break;
			    }
			  }
		}
	}
</script>

<style lang="stylus">
.userInfo-container{
	background-color: #efeff4;
	padding-top: 80rpx;
	position: absolute;
	width: 100%;
	height: 100%;
	text-align: center;
	font-size: 30rpx;
	box-sizing: border-box;
}
.err-tips{
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 70rpx;
	line-height: 70rpx;
	background-color: #e64340;
	color: #fff;
}
.section{
	background-color: #fff;
	width: 100%;
	text-align: left;
	height: 80rpx;
	margin-bottom: 0;
}
.section .section-title{
	display: inline-block;
	width: 30%;
	height: 100%;
	line-height: 100%;
	font-weight: bold;
	position: relative;
	top: 4rpx;
	padding-left: 20rpx;
	box-sizing: border-box;
}
.section > input, .section > picker{
	display: inline-block;
	width: 70%;
	height: 100%;
	line-height: 100%;
	vertical-align: middle;
}
.picker{
	height: 80rpx;
	line-height: 80rpx;
}
.headImg{
	width: 150rpx;
	height: 150rpx;
	border-radius: 75rpx;
	margin-bottom: 10rpx;
}
.split-text{
	text-align: left;
	background: transparent;
	height: 80rpx;
	line-height: 80rpx;
	padding-left: 20rpx;
	font-size: 26rpx;
	color: #888888;
}
.btn-area > button{
	margin-top: 40rpx;
}
.display{
	display: inline-block;
	width: 70%;
	height: 100%;
	line-height: 80rpx;
	vertical-align: middle;
}
</style>
