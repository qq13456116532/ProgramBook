<template>
	<!-- 模板部分保持不变，以确保样式和功能完全一致 -->
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
					<swiper-item v-for="(item, index) in emails" :key="item.id" :class="index == currentEmail? 'active': ''">
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
								<button v-if="item.contentButtonText" @click="gotoUrl" :data-gotoUrl="item.contentButtonUrl" :data-emailId="item.id">{{item.contentButtonText}}</button>
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
					<!-- 此处原始逻辑'已签到':'已签到'无意义，保持原样 -->
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
					<image :src="bookItem.headerImage" mode="scaleToFill" />
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
	// 使用 ES6 import 语法，更现代且有助于静态分析
	import Api from '../../utils/api/api';
	import util from '../../utils/util';

	export default {
		data() {
			return {
				// 用户信息
				username: "...",
				avatar: 'https://file.lantingshucheng.com/myApp/unknown_headimg.png', // 提供一个默认头像
				token: "",

				// 阅读统计
				hasReadTime: 0,
				continueReadDay: 0,
				hasBookNum: 0,

				// 消息（邮件）
				totalEmailsNum: 0,
				emails: [], // 初始化为空数组，由 API 填充
				currentEmail: 0, // Swiper索引通常从0开始

				// 任务
				tasks: {
					isSigned: false,
					allTasks: [], // 初始化为空数组，由 API 填充
				},

				// Swiper UI状态
				indicatorDots: false,
				autoplay: false,
				interval: 5000,
				duration: 1000,
				
				// 用于<template is="...">的组件数据
				err_tips_data: {},
				toast_data: {}
			};
		},
		onShow() {
			this.token = uni.getStorageSync('token');
			console.log("页面显示，当前token:", this.token);
			if (this.token) {
				this.fetchAllData();
			} else {
				util.showErrMsg(this, '请先登录', 1500);
			}
		},
		methods: {
			/**
			 * @description 使用Promise.all并行获取所有页面数据，提升加载性能
			 */
			async fetchAllData() {
				// 封装一个返回Promise的请求函数，便于与async/await结合使用
				const createRequest = (options) => {
					return new Promise((resolve, reject) => {
						uni.request({
							...options,
							success: (res) => resolve(res),
							fail: (err) => reject(err),
						});
					});
				};

				try {
					// 并行发起所有请求
					const [
						userInfoRes,
						readDayRes,
						readTimeRes,
						bookNumRes,
						tasksRes,
						emailsRes
					] = await Promise.all([
						createRequest({ url: Api.getUserInfo(this.token) }),
						createRequest({ url: Api.GetContinueReadDay(this.token) }),
						createRequest({ url: Api.getContinueTime(this.token) }),
						createRequest({ url: Api.gethasBookNum(this.token) }),
						createRequest({ url: Api.getAllTasks(this.token) }),
						createRequest({ url: Api.getAllEmails(this.token), method: 'GET' })
					]);

					// 按顺序处理返回结果
					if (userInfoRes.data?.code === 200) {
						const { avatar, nickName } = userInfoRes.data.data;
						this.avatar = avatar;
						this.username = nickName;
					}
					if (readDayRes.data?.code === 200) this.continueReadDay = readDayRes.data.data;
					if (readTimeRes.data?.code === 200) this.hasReadTime = readTimeRes.data.data;
					if (bookNumRes.data?.code === 200) this.hasBookNum = bookNumRes.data.data;
					if (tasksRes.data?.code === 200) this.tasks.allTasks = tasksRes.data.data;
					if (emailsRes.data?.code === 200) {
						this.emails = emailsRes.data.data;
						this.totalEmailsNum = this.emails.length;
					}
					
				} catch (error) {
					console.error("获取页面数据失败:", error);
					util.showErrMsg(this, '数据加载失败，请检查网络', 1500);
				}
			},

			/**
			 * @description 跳转到书籍详情
			 */
			gotoBookDetail(event) {
				const bookid = event.currentTarget.dataset.bookid;
				uni.navigateTo({
					url: '../book_detail/book_detail?bookid=' + bookid
				});
			},

			/**
			 * @description Swiper 切换事件处理
			 */
			changeEmail(event) {
				const currentIndex = event.detail.current;
				this.currentEmail = currentIndex;
				
				const showTip = (text) => {
					this.err_tips_data = { err_tips_show: true, err_tips_text: text };
					setTimeout(() => {
						// 使用箭头函数确保`this`指向正确
						this.err_tips_data = { err_tips_show: false, err_tips_text: '' };
					}, 3000);
				};
				
				// 边界提示
				if (currentIndex === 0) {
					showTip('当前为第一封邮件');
				} else if (currentIndex >= (this.totalEmailsNum - 1)) {
					showTip('当前为最后一封邮件');
				}
			},
			
			/**
			 * @description 处理消息卡片中的按钮点击，标记已读并跳转
			 */
			gotoUrl(event) {
				const { gotourl: gotoUrl, emailid: emailId } = event.currentTarget.dataset;
				console.log(`邮件按钮点击 -> URL: ${gotoUrl}, EmailID: ${emailId}`);

				// 标记邮件已读 (这是一个"即发即忘"的请求，不影响用户跳转)
				uni.request({
					url: Api.ReadEmail(emailId),
					method: 'POST',
					success: (res) => {
						// 修正了原始代码中的判断条件 res.code -> res.data.code
						if (res.data?.code === 200) {
							console.log("ReadEmail成功:", res.data);
						} else {
							console.warn("ReadEmail API未返回成功:", res.data);
						}
					},
					fail: (err) => {
						console.error("ReadEmail 请求失败:", err);
					}
				});

				// 根据URL类型执行不同跳转
				if (gotoUrl === '../shop/shop') {
					uni.switchTab({ url: gotoUrl });
				} else {
					uni.navigateTo({ url: gotoUrl });
				}
			},
			
			/**
			 * @description 跳转到已读书籍列表
			 */
			goToBookList() {
				uni.navigateTo({
					url: '../booklist/booklist',
				});
			},
			
			/**
			 * @description 跳转到用户信息页
			 */
			gotoUserInfo() {
				uni.navigateTo({
					url: './userInfo/userInfo',
				});
			},
			
			/**
			 * @description 头像加载失败时的回退处理
			 */
			headimgError() {
				this.avatar = 'https://file.lantingshucheng.com/myApp/unknown_headimg.png?imageView2/1/w/60/h/60/format/jpg/interlace/1/q/75|imageslim';
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
