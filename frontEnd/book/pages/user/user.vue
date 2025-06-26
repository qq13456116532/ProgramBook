<template>
	<view>
	<import src="../component/email_digest/email_digest.vue" />
	<view class='today-container'>
	  <view class="headimg">
		<!--  <button style="width: 100rpx; height: 100rpx; padding: 0; border: none;border-radius: 50rpx;"
			open-type="chooseAvatar" @chooseavatar="chooseavatar" > -->
			  <image :src="avatar" model="center" @error="headimgError" />
		<!-- </button> -->
		  
	    <!-- <image :src="avatar" model="center" @error="headimgError" @click="gotoUserInfo" /> -->
	    <text class="username">{{username}}</text>
		
	    <!-- <open-data type="userNickName" class="username"></open-data>		 -->
	  </view>
	  <view class="more-info">
	    <view class="info-time">
	      <text class="time">
	        <text class="num">{{hasReadTime}}</text>
	        <text class="danwei">分钟</text>
	      </text>
	      <text class="des">有效阅读时长</text>
	    </view>
	    <view class="info-day">
	      <text class="day">
	        <text class="num">{{continueReadDay}}</text>
	        <text class="danwei">天</text>
	      </text>
	      <text class="des">坚持阅读天数</text>
	    </view>
	    <view class="info-book" @click="goToBookList">
	      <text class="book">
	        <text class="num">{{hasBookNum}}</text>
	        <text class="danwei">本</text>
	      </text>
	      <text class="des">已阅读的书籍</text>
	    </view>
	  </view>
	  <view class="email">
	    <view class="title">
	      <text>
	        <text class="text-underline">所有</text>消息</text>
	      <icon class="email-icon">
	        <icon class="num-bg"></icon>
	        <text class="num-value">{{totalEmailsNum}}</text>
	      </icon>
	    </view>
	
	    <!--滑块-->
	    <swiper style="height: 250rpx;" class="swipe" duration="600" @change="changeEmail" :current="currentEmail">
	        <swiper-item v-for="item in emails" :key="item.id" :class="index == currentEmail? 'active': ''">
	          <view>
	            <view class="author">
	              <image :src="item.digestAuthorImg" mode="scaleToFill"></image>
	              <view>
	                <text class="name">{{item.digestAuthorName}}</text>
	                <br />
	                <text class="des">{{item.digestDes}}</text>
	              </view>
	            </view>
	            <view class="title">{{item.title}}</view>
	            <view class="content">
	              <button v-if="item.contentButtonText" @click="gotoUrl"
				   :data-gotoUrl="item.contentButtonUrl" :data-emailId="item.id" >{{item.contentButtonText}}</button>
	              <!-- <text class="other">{{item.digest.content.other}}</text> -->
	            </view>
	          </view>
	          <image :src="item.bgImgUrl" class="slide-image" mode="scaleToFill" />
	        </swiper-item>
	    </swiper>
	  </view>
	  <view class="dailyTask">
	    <view class="title">
	      <text>
	        <text class="text-underline">今日</text>任务
	      </text>
	      <text class="isSigned">{{tasks.isSigned == true? '已签到': '已签到'}}</text>
	    </view>
	    <view class="task-item" v-for="(bookItem,bookindex) in tasks.allTasks" :key="bookItem.id" :data-bookid="bookItem.id" @click="gotoBookDetail">
	      <view class="bookDetail">
	        <text class="name">{{bookItem.factionName}}</text>
	        <text class="des">{{bookItem.des}}</text>
	        <view class="hot">
	          热度：{{bookItem.hot}} 
			  <uni-icons type="contact" size="30"></uni-icons>
	          <!-- <icon :class="(index+1) <= bookItem.bookHot? 'yellow-star-icon': 'gray-star-icon'" v-for="index of 5" :key="index" >显示</icon> -->
	        </view>
	        <view class="progress">
	          <text class="progressRate">{{bookItem.hasRead}}</text>
			  <text class="totalProgress">{{"/"+ bookItem.chapterSum +'\n学习进度'}}</text>
	        </view>
	      </view>
	      <image :src="bookItem.headerImage" mode="scaleToFill"/>
	    </view>
	  </view>
	
	  <!-- 出错打印 -->
	  <import src="../component/err_tips/err_tips.vue" />
	  <template is="err_tips" :data="err_tips_data" />
	
	  <!-- 成功的提示 -->
	  <import src="../component/toast/toast.vue" />
	  <template is="toast" :data="toast_data" />
	</view>
	</view>
</template>

<script>
	//today.js
	var Api = require('../../utils/api/api');
	var util = require('../../utils/util');
	
	export default {
		data() {
			return {
					username:"",
				    currentEmailPageid: 1,
				    totalEmailsNum: 5,
				    emails: [
				      {
				        id: '12312312311231',
				        bgImgUrl: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				        digest: {
				          author_img: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				          author_name: '熊猫老师',
				          author_des: '宣传部',
				          title: "“阅读推行赠书计划”已经开始",
				          content: {
				            button: {
				              text: '加入计划',
				              url: '../index/index',
				              eventHandler: 'goToUrl'
				            },
				            other: '或者到【我的--邀请好友加入】查看'
				          }
				
				        }
				      },
				      {
				        id: '1231221331231',
				        bgImgUrl: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				        digest: {
				          author_img: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				          author_name: '熊猫老师',
				          author_des: '宣传部',
				          title: "“阅读推行赠书计划”已经开始",
				          content: {
				            button: {
				              text: '加入计划',
				              url: '../index/index',
				              eventHandler: 'goToUrl'
				            },
				            other: '或者到【我的--邀请好友加入】查看'
				          }
				        }
				      },
				      {
				        id: '12312312123131',
				        bgImgUrl: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				        digest: {
				          author_img: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				          author_name: '熊猫老师',
				          author_des: '宣传部',
				          title: "“阅读推行赠书计划”已经开始",
				          content: {
				            button: {
				              text: '加入计划',
				              url: '../index/index',
				              eventHandler: 'goToUrl'
				            },
				            other: '或者到【我的--邀请好友加入】查看'
				          }
				        }
				      },
				      {
				        id: '12312312123131',
				        bgImgUrl: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				        digest: {
				          author_img: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				          author_name: '熊猫老师',
				          author_des: '宣传部',
				          title: "“阅读推行赠书计划”已经开始",
				          content: {
				            button: {
				              text: '加入计划',
				              url: '../index/index',
				              eventHandler: 'goToUrl'
				            },
				            other: '或者到【我的--邀请好友加入】查看'
				          }
				        }
				      },
				      {
				        id: '12312312123131',
				        bgImgUrl: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				        digest: {
				          author_img: 'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500',
				          author_name: '熊猫老师',
				          author_des: '宣传部',
				          title: "“阅读推行赠书计划”已经开始",
				          content: {
				            button: {
				              text: '加入计划',
				              url: '../index/index',
				              eventHandler: 'goToUrl'
				            },
				            other: '或者到【我的--邀请好友加入】查看'
				          }
				        }
				      }
				    ],
				    tasks: {
				      isSigned: false,
				      allTasks: [
				        // {
				        //   bookId: '123123121231231',
				        //   img: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/d5ef6b4b-c667-47b8-8986-cff1776e9862.jpg',
				        //   bookName: 'C/C++求职宝典',
				        //   bookDes: '本书以C/C++面试求职为核心，全面介绍了在C/C++面试中重点考查的各个考点，并从面试求职、职业规划、面试渠道、面试真题、面试过程及职场生存等各个方面讲解了一个面试者应该掌握的所有知识',
				        //   bookHot: 4,
				        //   progressRate: 5
				        // },
				        // {
				        //   bookId: '1231231231',
				        //   img: 'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/10c48144-2446-4909-aa7c-9310c78c489d.jpg',
				        //   bookName: 'HTML 5应用开发实践指南 ',
				        //   bookDes: '这本书反映了Web技术的革命。之前，人们普遍认为Web无须编程，只要把脚本硬塞入网页之中就行了。现在，HTML和JavaScript在实现良好用户体验的过程中产生了重要作用。通过阅读本书，你将会掌握Web发展进程中最前沿的技术。',
				        //   bookHot: 2,
				        //   progressRate: 2
				        // }
				      ]
				    },
				    indicatorDots: false,
				    autoplay: false,
				    interval: 5000,
				    duration: 1000,
				    currentEmail: 1,
				    avatar: '',
				    hasReadTime: 0,
				    continueReadDay: 0,
				    hasBookNum: 0,
					token: ""
			};
		},
		onShow: function () {
		    var self = this;
		    // 得到邮件的数据
		    // self.getEmailsByPageid(self.data.currentEmailPageid);
			self.token = wx.getStorageSync('token');
			console.log("刚加载，看一下token:",self.token)
			uni.request({
				url:Api.getUserInfo(self.token),
				success(res) {
					if(res.data.code == 200){
						console.log("getUserInfo的结果是",res)
						self.avatar = res.data.data.avatar
						self.username = res.data.data.nickName
					}else{
						util.showErrMsg(self, '请检查网络', 1000);
					}
				}
			})
			uni.request({
				url: Api.GetContinueReadDay(self.token),
				success(res) {
					if(res.data.code == 200){
						// console.log("GetContinueReadDay的结果是",res)
						self.continueReadDay = res.data.data
					}else{
						util.showErrMsg(self, '请检查网络', 1000);
					}
				}
			})
			uni.request({
				url:Api.getContinueTime(self.token),
				success(res) {
					if(res.data.code == 200){
						// console.log("getContinueTime的结果是",res)
						self.hasReadTime = res.data.data
					}else{
						util.showErrMsg(self, '请检查网络', 1000);
					}				
				}
			})
			uni.request({
				url:Api.gethasBookNum(self.token),
				success(res) {
					if(res.data.code == 200){
						self.hasBookNum = res.data.data
					}else{
						util.showErrMsg(self, '请检查网络', 1000);
					}
				
				}
			})
			uni.request({
				url:Api.getAllTasks(self.token),
				success(res) {
					if(res.data.code == 200){
						console.log("getAllTasks的结果是",res)
						self.tasks.allTasks = res.data.data
					}else{
						util.showErrMsg(self, '请检查网络', 1000);
					}
				}
			})
			uni.request({
				    url: Api.getAllEmails(self.token),
				    method: 'GET',
					success(res) {
						if(res.data.code == 200){
							console.log("getAllEmails的结果是",res)
							self.emails = res.data.data
							self.totalEmailsNum = self.emails.length
						}else{
							util.showErrMsg(self, '请检查网络', 1000);
						}
					},
					fail() {
						console.log("getAllEmails失败，请检查网络")
					}
			})	 
		  },
		  methods:{
			  
			  gotoBookDetail: function (event) {
			  	// console.log("gotoBookDetail,event是",event)
			    var bookid = event.currentTarget.dataset.bookid;
			    // 判断当前书籍在不在我的书单中
			    uni.navigateTo({
			      url: '../book_detail/book_detail?bookid=' + bookid
			    });
			  },
			  getEmailsByPageid: function (pageid) {
			      // uni.request({
			      //     url: Api.getEmailsByPageid(pageid),
			      //     method: 'GET',
			      //     success: function (res) {
			      //         self.setData({totalEmailsNum: res.totalNum, emails: res.data});
			      //         //将邮件数据存入缓存
			      //         uni.setStorageSync(emails, res.data);
			      //     },
			      //     fail: function () {
			      //         // 尝试读取缓存中的值
			      //         // try {self.setData({emails: uni.getStorageSync('emails')});} catch (err) { console.log(err) }
			      //     }
			      // });
			    },
			    changeEmail: function (event) {
			      var self = this;
			      var currentIndex = event.detail.current;
				  this.currentEmail = currentIndex
				  console.log("currentIndex: ",currentIndex)				  
			      if (currentIndex <= 0) {
			        //提示用户当前为第一封邮件
					this.err_tips_data = {err_tips_show: true, err_tips_text: '当前为第一封邮件'}
					console.log('当前为第一封邮件')
			        setTimeout(function () {
						this.err_tips_data = {err_tips_show: false, err_tips_text: ''}
			        }, 3000);
			      } else if (currentIndex >= (self.totalEmailsNum - 1)) {
			        //提示用户当前为最后一封
					this.err_tips_data={err_tips_show: true, err_tips_text: '当前为最后一封邮件'}
					console.log('当前为最后一封邮件')
			        setTimeout(function () {
						this.err_tips_data = {err_tips_show: false, err_tips_text: ''}
			        }, 3000);
			      }
			      //当加载到第五篇，就为下面的数据做准备
			      if ((currentIndex + 1) % 5 != 0) {
					  this.currentEmailPageid = currentIndex
					  // console.log('当前为最后一封邮件,还要往后')
					  
			        // self.getEmailsByPageid(self.data.currentEmailPageid);
			      } else {
					  // console.log('当前为最后一封邮件,还要往后')
			        //请求后面的邮件数据
			      }
			    },
			    //email中按钮的处理事件
			    // goToUrl: function (event) {
			    //   //获取位于button上的url参数
			    //   var gotoUrl = event.currentTarget.dataset.gotourl;
			    //   console.log('正在执行邮件按钮的点击事件，传入的参数是: ' + gotoUrl);
			    //   uni.navigateTo({
			    //     url: gotoUrl,
			    //   });
			    // },
				  gotoUrl: function (event) {
					  var self = this
				    // 获取位于button上的url参数
				    var gotoUrl = event.currentTarget.dataset.gotourl;
					// console.log("看一下gotoURL的参数",event.currentTarget.dataset)
					var emailId = event.currentTarget.dataset.emailid;
				    console.log('正在执行邮件按钮的点击事件，传入的参数gotoUrl是: ' + gotoUrl);
					console.log('正在执行邮件按钮的点击事件，传入的参数emailId是: ' + emailId);
					uni.request({
						    url: Api.ReadEmail(emailId),
						    method: 'POST',
							success(res) {
								if(res.code == 200){
									console.log("ReadEmail的结果是",res)
									
								}else{
									// util.showErrMsg(self, '请检查网络', 1000);
								}
							},
							fail() {
								console.log("ReadEmail失败，请检查网络")
							}
					})	 
					if(gotoUrl == '../shop/shop'){
						uni.switchTab({
							url: gotoUrl
						})
					}else{
						uni.navigateTo({
						  url: gotoUrl,
						});
					}		    
						
				  },
			    goToBookList: function () {
			      uni.navigateTo({
			        url: '../booklist/booklist',
			      });
			    },
			    gotoUserInfo: function () {
			      uni.navigateTo({
			        url: './userInfo/userInfo',
			      });
			    },
			    headimgError: function () {
			    this.avatar = 'https://file.lantingshucheng.com/myApp/unknown_headimg.png?imageView2/1/w/60/h/60/format/jpg/interlace/1/q/75|imageslim'
				}
		  }
	}
</script>

<style lang="stylus">
@charset "UTF-8";
.today-container {
  background-color: #F2F2F2; }

.today-container .headimg {
  background-color: #FD9941;
  text-align: center;
  color: #ffffff;
  padding-top: 20rpx; }

.today-container .headimg image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx; }

.today-container .headimg .username {
  display: block;
  font-size: 28rpx; }

.today-container .more-info {
  background-color: #FD9941;
  color: #ffffff;
  font-size: 22rpx; }

.today-container .more-info view {
  width: 33%;
  display: inline-block;
  text-align: center;
  border-right: 1px solid #ffffff;
  padding: -0 24rpx 0 24rpx;
  box-sizing: border-box;
  margin-top: 26rpx;
  margin-bottom: 32rpx; }

.today-container .more-info view:last-child {
  border-right: 0; }

.today-container .more-info view text .num {
  font-size: 40rpx; }

.today-container .more-info view text.des {
  display: block; }

/*所有邮件的css*/
.today-container .email {
  margin-top: 20rpx;
  background-color: #fff; }

.today-container .title {
  font-size: 30rpx;
  font-weight: bold;
  color: #494949;
  position: relative;
  padding: 15rpx 20rpx; }

.today-container .title .text-underline {
  line-height: 30rpx; }

.today-container .title .email-icon {
  display: inline-block;
  position: absolute;
  background-size: 240rpx 240rpx;
  background-position: 4rpx -154rpx;
  width: 50rpx;
  height: 35rpx;
  right: 20rpx;
  top: 15rpx; }

.today-container .title .email-icon .num-bg {
  display: inline-block;
  position: absolute;
  width: 23rpx;
  height: 23rpx;
  right: -5rpx;
  top: -5rpx;
  border-radius: 10rpx;
  background-color: #FD9941; }

.today-container .title .email-icon .num-value {
  display: inline-block;
  position: absolute;
  width: 23rpx;
  height: 23rpx;
  right: -4rpx;
  top: -3rpx;
  font-size: 16rpx;
  color: #fff;
  text-align: center; }

.today-container swipe {
  height: 250rpx !important; }

.today-container swiper-item {
  width: 80% !important;
  position: relative;
  left: 10%;
  text-align: center;
  height: 200rpx !important;
  top: 25rpx; }

.today-container swiper-item.active {
  height: 250rpx !important;
  top: 0rpx;
  animation: gotoNextpage 1s; }

.today-container swiper-item > image {
  opacity: .5;
  height: 100%;
  width: 95%;
  z-index: 1;
  border-radius: 15rpx; }

.today-container swiper-item > view {
  position: absolute;
  height: 100%;
  width: 95%;
  left: 3%;
  z-index: 2;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 15rpx;
  color: #ffffff; }

.today-container swiper-item .author {
  text-align: left;
  padding: 4% 4% 0 4%; }

.today-container swiper-item .author > image {
  display: inline-block;
  height: 60rpx;
  width: 60rpx; }

.today-container swiper-item .author > view {
  display: inline-block;
  margin-left: 15rpx;
  position: relative;
  top: -10rpx; }

.today-container swiper-item .name {
  font-size: 24rpx;
  font-weight: bold;
  display: block; }

.today-container swiper-item .des {
  font-size: 22rpx;
  display: block;
  margin-top: 3rpx; }

.today-container swiper-item .title {
  color: #ffffff;
  font-size: 24rpx; }

.today-container swiper-item .content {
  font-size: 22rpx; }

.today-container swiper-item .content > button {
  width: 34%;
  height: 40rpx;
  border-radius: 20rpx;
  border: 1px solid #ffffff;
  font-size: 22rpx;
  color: #ffffff;
  background: transparent;
  line-height: 40rpx; }

.today-container swiper-item .content .other {
  line-height: 50rpx; }

/*今日任务*/
.today-container .dailyTask {
  background: #ffffff;
  position: relative;
  margin-top: 20rpx;
  margin-bottom: 20rpx; }

.today-container .isSigned {
  position: absolute;
  right: 20rpx;
  top: 15rpx;
  font-size: 24rpx;
  color: #FD9941; }

.today-container .task-item {
  padding: 10rpx 20rpx 10rpx 30rpx;
  height: 180rpx; }

.today-container .task-item image {
  height: 180rpx;
  width: 120rpx;
  box-shadow: 4rpx 4rpx 2rpx #888;
  float: left;
  display: inline-block;
  margin-left: -100%; }

.today-container .bookDetail {
  float: left;
  width: 100%;
  padding: 10rpx 10rpx 10rpx 160rpx;
  font-size: 22rpx;
  box-sizing: border-box; }

.today-container .bookDetail::after {
  content: "";
  display: none;
  clear: both; }

.today-container .bookDetail .name {
  display: block;
  font-size: 26rpx;
  font-weight: blod; }

.today-container .bookDetail .des {
  display: block;
  margin: 10rpx 0 10rpx 0;
  /*两行，多于的省略*/
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical; }

.today-container .hot .gray-star-icon {
  display: inline-block;
  height: 25rpx;
  width: 25rpx;
  background-size: 150rpx 150rpx;
  background-position: -93rpx -126rpx;
  margin-right: 4rpx;
  vertical-align: middle;
  position: relative;
  top: -1rpx; }

.yellow-star-icon {
  display: inline-block;
  height: 25rpx;
  width: 25rpx;
  background-size: 150rpx 150rpx;
  background-position: -62rpx -126rpx;
  margin-right: 4rpx;
  vertical-align: middle;
  position: relative;
  top: -1rpx; }

.today-container .progress {
  text-align: right;
  height: 24rpx; }

.today-container .progressRate {
  display: inline-block;
  line-height: 24rpx;
  height: 24rpx;
  font-size: 50rpx;
  color: #FD9941; }

.today-container .totalProgress {
  display: inline-block;
  height: 24rpx;
  width: 100rpx;
  text-align: left;
  font-size: 20rpx; }

/*email翻页的动画*/
@keyframes gotoNextpage {
  from {
    top: 25rpx; }
  to {
    top: 0; } }

</style>
