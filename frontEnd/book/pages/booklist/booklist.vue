<template>
	<view class="booklist-container">
		<view class="search">
			<view class="section">
				<view class="form">
					<view class="input-block">
						<input placeholder="请输入搜索内容" @focus="setIsSearching" @blur="judgeIsNull" @input="finishedInput"
							v-model="searchValue" />
					</view>
					<view class="search-block" :style="'display:'+ isSearching? 'none': 'inline-block'">
						<icon class="icon-small" style="margin-left: 630rpx;" type="search" color="#fff"></icon>
						<!-- <text>请输入搜索内容</text> -->
					</view>
				</view>
				<view class="toShop" bindtap="goToShop">
					<icon class="shop-icon"></icon>
				</view>
			</view>
		</view>
		<view class="header">
			<view class="headerLine"></view>
			<text class="headerTime">{{headerText}}</text>
			<view class="timeTab">
				<block v-for="item in showMonths" :key="item.month">
					<view :class="(item.monthIndex === monthIndex) ? 'active last everyTime' : 'everyTime'"
						@click="chooseMonth" :data-month="item.monthIndex">
						<text>{{item.monthCname}}</text>
						<icon :class="item.hasRecord? 'hasRecord': ''"></icon>
					</view>
				</block>
			</view>
		</view>
		<view class="list">
			<view class="all" v-if="books.length > 0">
				<block class="bookitem" v-for="(item,index) in books" :key="item.id">
					<block v-if="index<10 && item.isShow">
						<view class="bookInfo" @click="goToBookDetail(item.id)" :bookid="item.id">
							<text class="bookName">{{item.factionName}}</text>
							<text class="author">
								<text class="text-underline">作者：</text>{{item.author}}</text>
							<text class="bookDes">简介： {{item.des}}</text>
							<icon :class="item.hasRead === 0? 'read-icon isRead': 'read-icon notRead'"></icon>
						</view>
						<image :src="item.headerImage" mode="scaleToFill" />
					</block>
				</block>
			</view>
			<view class="empty" v-if="books.length === 0">
				<image src="https://bpic.588ku.com/element_origin_min_pic/00/77/63/0056e18ce712923.jpg"></image>
				<button @click="goToShop" plain="true" type="default">您还未添加书籍，快去书城看看</button>
			</view>
		</view>

		<!--  <import src="../component/err_tips/err_tips.vue" />
	  <template is="err_tips" :data="err_tips_data" />
	
	  <import src="../component/toast/toast.vue" />
	  <template is="toast" :data="toast_data" />
	

	  <import src="../component/err_page/err_page.vue" />
	  <template is="err_page" :data="err_page_data" /> -->
		<view class='box' :hidden="authorModal">
			<view class='msg-box'>
				<text class='title'>设置</text>
				<text class='aumsg'>We微书申请获得以下权限</text>
				<text class='msg'>●设置你的公开信息（昵称、头像等）</text>
				<view class="headimg">
					<button style="width: 100rpx; height: 100rpx; padding: 0; border: none;border-radius: 50rpx;"
						open-type="chooseAvatar" @chooseavatar="chooseavatar">
						<image :src="avatarsetting" model="center" @error="headimgError" />
					</button>
					<!-- <image :src="avatar" model="center" @error="headimgError" @click="gotoUserInfo" /> -->
					<!-- <text class="username">{{username}}</text> -->

				</view>
				<!-- <open-data type="userNickName" class="username"></open-data> -->
				<input v-model="nameSetting" @blur="handleNameBlur" type="nickname" class="weui-input"
					style="text-align: center;" placeholder="请输入昵称" />

				<view class='btn_wrapper'>
					<button @click='authorcancle' class='btn'>拒绝</button>
					<button class='btn confirm_btn' open-type='getUserInfo' @click='bindGetUserprofile'>允许</button>
				</view>
			</view>
		</view>
		<!-- 添加遮罩层 -->
		<view class='mask' :hidden="authorModal"></view>

	</view>

</template>

<script>
	import {
		getMyBooks
	} from '../../utils/api/api';
	//booklist.js
	var Api = require('../../utils/api/api');
	var Util = require('../../utils/util');
	//获取应用实例
	var app = getApp();
	export default {
		data() {
			return {
				headerText: '',
				showMonths: [],
				books: [],
				isSearching: false,
				searchValue: '',
				userInfo: {},
				err_page_data: null, //app状态页
				monthIndex: 0,
				authorModal: true,
				avatarsetting: "https://wk-gulimall.oss-cn-beijing.aliyuncs.com/132.jfif",
				nameSetting: ""
			}
		},
		onHide() {
			this.searchValue = ""
		},
		onReady: function() {
			var self = this;
			var timeResult = self.allMonths();
			this.showMonths = timeResult.showMonths
			this.headerText = timeResult.headerText
			console.log("时间结果是", timeResult)
			//先获取本地缓存中的书单数据，等接口返回之后再更新

			wx.getStorage({
				key: 'booklist',
				success: function(res) {
					console.log('使用本地缓存的书单数据');
					self.books = res.data
				},
				fail(res) {
					console.log("书本缓存数据获取失败")
				}
			});
		},
		onLoad: function(e) {
			var self = this;
			//显示加载中
			wx.showToast({
				title: '正在获取书单...',
				icon: 'loading',
				duration: 1000
			});
		},
		onShow: function() {
			var self = this			
			var isLogin = uni.getStorage({
				key: "token",
				success(res) {
					var token = res.data
					console.log("打印一下缓存里的token", token)
					if (res.data) {
						// console.log("当前已经执行到了token是", res.data)
						wx.request({
							url: Api.GetjudgeExpire(token),
							success: function(suc) {
								if (suc.data.code == 200)
									console.log("judgeExpire请求成功,没过期", suc)
								else {
									console.log("judgeExpire请求但是缓存过期了", suc)
									self.authorModal = false;
									wx.hideTabBar({
										animation: true
									})
									return;
								}
							},
							fail: function(e) {
								console.log("请求失败", e)
							}
						})
						self.authorcancle()
					}
				},
				fail(res) {
					console.log("获取token失败", res)
					self.authorModal = false;
					wx.hideTabBar({
						animation: true
					})
				}
			})
			//获取我的书单
			// console.log("读取一下用户ID")
			this.getMyBooks(wx.getStorageSync('token'));

		},
		methods: {
			handleNameBlur(event) {
				this.nameSetting = event.detail.value
			},
			chooseavatar: function(e) {
				var self = this
				console.log("执行chooseavatar，e.detail.avatarUrl是", e.detail.avatarUrl)
				// 获取图片数据
				uni.getImageInfo({
				  src: e.detail.avatarUrl,
				  success: function (res) {
					  console.log("uni.getImageInfo的结果是",res)
				    // 将图片数据转为Base64编码字符串
				    uni.getFileSystemManager().readFile({
				      filePath: res.path,
				      encoding: 'base64',
				      success: function (res) {
						  console.log("转化为base64：",res)
						  self.avatarsetting = 'data:image/jpeg;base64,'+res.data
				        // 将Base64编码字符串作为参数发送请求
				        // uni.request({
				        //   url: '后端接口url',
				        //   method: 'POST',
				        //   data: {
				        //     image: res.data
				        //   },
				        //   success: function (res) {
				        //     console.log(res.data)
				        //   }
				        // })
				      }
				    })
				  },
				  fail(res) {
				  	console.log("uni.getImageInfo失败，结果是",res)
				  	
				  }
				})
				
				
				// this.avatarsetting = e.detail.avatarUrl
			},
			authorcancle() {
				this.authorModal = true;
				wx.showTabBar({
					animation: true
				})
			},
			//授权+登录
			bindGetUserprofile() {
				var self = this
				console.log(self.avatarsetting + "和" + self.nameSetting)
				if (self.nameSetting == "") {
					uni.showToast({
						title: '未设置昵称',
						icon: 'error',
						duration: 2000
					});
					return;
				}
				if (self.avatarsetting == "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/2wBDAAcFBQYFBAcGBgYIBwcICxILCwoKCxYPEA0SGhYbGhkWGRgcICgiHB4mHhgZIzAkJiorLS4tGyIyNTEsNSgsLSz/wAALCACEAIQBAREA/8QAGwABAAIDAQEAAAAAAAAAAAAAAAMFAgQGAQj/xAAuEAEAAgECAggGAgMAAAAAAAAAAQIDBBEFIRIVMUFRZKLhEyJhcYHBFFIjkdH/2gAIAQEAAD8A+gQAiAAAiAAAiAAAAIgAiAAAAAAAAAAAAEOr1VNJh+JfnPdHjKgz67Uai29r2iPCOUIa5L47b0vaJ+llpw/ilpvGLUW335Rf/q3AAAAc/wAWzzl1017sfyR+2iDpeHZ5z6Klrc5jlP5bIAAAOY1sTGuzRP8AeUAL7g0TGimfG8/pYAAAApOM6Wa5o1Fey/KfsrBlWtsl4pWu8zyh02lwxp9NTF/WOf3TAAAAxy4q5sU4713pLl82P4Oa+Pt6MzH+ka44Lp69GdRPOd9qfRbAAAANPWcSxaes1rbp5PCO77uftacl5vbnMzvLFv8ADuIfxJml67455/aV5iy481OnjtW8fVmAAAh1GrxaWnSyW590d8qXV8UzajetP8dPCO2fw0QEmHPkwX6eK1qSuNJximXauf5J8e72WPbzh6AAOUy5b5sk5L23mWAAC64LlvbHkx2tvWu2353WgAAqbcCrN5mufaPDo7/t51F5j0e51F5j0e51F5j0e51F5j0e51F5j0ex1F5j0e51F5j0e7d0OiroqXiL9Obds9nY2gAAAAAAAAAAAAAAAAAAAAAAAf/Z") {
					uni.showToast({
						title: '未设置头像',
						icon: 'error',
						duration: 2000
					});
					return;
				}
				this.authorcancle()
				wx.login({
					success(mysuc) {
						console.log("wxlogin后看一下jscode", mysuc)
						if (mysuc.code) {
							//发起网络请求
							wx.request({
								url: Api.GetOnLogin(),
								method: 'POST',
								data: {
									code: mysuc.code,
									nickName: self.nameSetting,
									avatar: self.avatarsetting,
									// session_key: mysuc.session_key
								},
								success: function(suc) {
									console.log("GetOnLogin请求成功", suc)
									try {
										wx.setStorageSync('token', suc.data.data.token)
									} catch (e) {
										console.log("保存token出错了",e)

									}
									self.getMyBooks(suc.data.data.token)
									console.log("登录成功！")
								},
								fail: function(e) {
									console.log("请求失败", e)
								}
							})
						} else {
							console.log('登录失败！' + mysuc.errMsg)
						}
					}
				})
			},
			//获取我的书单
			getMyBooks: function(token) {
				var self = this;
				console.log("正在执行getMyBooks")
				uni.request({
					url: Api.getMyBooks(token),
					success: function(res) {
						var books = res.data.data || [];
						console.log('书籍信息');
						console.log(books);
						books.forEach(function(item) {
							item.isShow = true;
						});
						//更新视图books
						self.books = books
						//将书单数据缓存到本地
						// uni.setStorage({
						// 	key: 'booklist',
						// 	data: books,
						// 	success: function(res) {
						// 		console.log('成功保存书籍列表到本地缓存');
						// 	}
						// });
					},
					fail: function() {
						console.log("请求书籍列表失败");
						self.err_page_data = {
							show: true,
							image_url: 'https://bpic.588ku.com/element_origin_min_pic/00/77/63/0056e18ce712923.jpg',
							text: '努力找不到网络>_<请检查后重试',
							buttonText: '重试',
							click: 'getMyBooks'
						}
					},
					complete: function() {
						//请求完成结束loading
						wx.hideToast();
					}
				});
			},
			allMonths: function() {
				var self = this;
				var resultArray = [];
				var today = new Date();
				var month = today.getMonth();
				// todo给出是否有记录的判断
				// resultArray.push({
				//   monthCname: Util.eNumToCNum(Math.abs(month)) + '月',
				//   hasRecord: false
				// });
				for (var i = 0; i <= 6; i++) {
					resultArray.push({
						monthCname: Util.eNumToCNum(Math.abs(month - i < 0 ? month + 12 - i : month - i)) +
							'月',
						// hasRecord: ((Math.random() > 0.5) ? true : false),
						hasRecord: i == 0,
						monthIndex: i
					});
				}
				return {
					headerText: today.getFullYear() + '年' + resultArray[0].monthCname,
					showMonths: resultArray.reverse()
				}
			},
			goToShop: function() {
				console.log("gotoshop")
				uni.switchTab({
					url: '../shop/shop',
					success() {
						console.log("switchTab调用成功")
					},
					fail(e) {
						console.log("switchTab调用失败", e)
					}
				});
			},
			goToBookDetail: function(e) {
				console.log("goToBookDetail的currentBookId是", e)
				uni.navigateTo({
					url: '../book_detail/book_detail?bookid=' + e
				})
			},
			setIsSearching: function() {
				this.isSearching = true;
			},
			judgeIsNull: function(event) {
				if (event.detail.value == '') {
					this.isSearching = false
				} else {
					this.isSearching = true
				}
			},
			finishedInput: function(event) {
				var self = this;
				console.log("完成输入", event.detail.value)
				var searchStr = event.detail.value;
				const myRegex = new RegExp(searchStr, "i");

				if (searchStr) {
					var allBooks = self.books;
					allBooks.forEach(function(item, index, array) {
						var isNeedtoChage = true;
						if (myRegex.test(item.factionName)) {

							item.isShow = true;
							isNeedtoChage = false;
						}
						//查询小说作者名称
						if (myRegex.test(item.author)) {
							item.author = self.findAndSigned(searchStr, item.author);
							item.factionName = self.findAndSigned(searchStr, item.factionName);
							item.isShow = true;
							isNeedtoChage = false;
						}
						if (isNeedtoChage) {
							item.isShow = false;
						}
					});
					self.books = allBooks
				} else {
					console.log('查询数据为空，不做任何操作');
					uni.getStorage({
						key: 'openid',
						success: function(res) {
							var id = res.data;
							self.getMyBooks(id);

						},
						fail(e) {
							console.log("获取失败ID", e)
						}
					});
				}

			},
			findAndSigned: function(searchString, readyToBeSearch) {
				if (typeof searchString == 'string') {
					var regExp = new RegExp(searchString, 'igm');
					var leftStr = ''; //记录关键词左边的字符串
					var rightStr = ''; //记录关键词右边的字符串
					var count = 1; //计数器
					var tempStr = readyToBeSearch; //用于正则匹配的字符串
					var notChageStr =
						readyToBeSearch; //用于截取字符串，和上面一样的值是因为不能把一个值既用于正则运算又用于记录加入<code></code>的新的字符串,这样会使得循环变成无限循环
					var lastIndex = 0; //记录关键词的位置
					while (regExp.exec(tempStr) != null) {
						console.log(++count);
						lastIndex = regExp.lastIndex + 13 * (count -
							1); //每次循环notChageStr并非不变，而是多了<code></code>共计13个字符，所以为了保证后续循环中lastindex的正确性应该将lastindex自增13
						leftStr = notChageStr.substring(0, lastIndex - searchString.length);
						rightStr = notChageStr.substring(lastIndex);
						notChageStr = leftStr + '<code>' + searchString + '</code>' + rightStr;
					}
					return notChageStr;
				} else {
					console.log('The param of findAndSigned is error!....')
					return '';
				}
			},
			clearSearchContent: function() {
				//将不显示的书籍设置显示
				var allBooks = this.data.books;
				allBooks.forEach(function(item) {
					item.isShow = true;
				});
				this.searchValue = ''
				this.books = allBooks
			},
			//选择月份
			chooseMonth: function(event) {
				var self = this;
				var month = event.target.month;
				// console.log(event)
				this.monthIndex = month
			}
		}
	}
</script>

<style>
	.box .msg-box .headimg {
		background-color: #ebe9fd;
		text-align: center;
		color: #ffffff;
		/* padding-top: 20rpx; */
	}

	.box .msg-box .headimg image {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50rpx;
	}

	.mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		/* 设置半透明黑色 */
		z-index: 100;
		/* 将遮罩层置于页面内容之上 */
		height: 100vh;
		/* 将高度设置为视口的高度 */
		width: 100vw;
		/* 将宽度设置为视口的宽度 */
	}

	/* @import '../component/err_page/err_page.vue'; */
	.booklist-container {
		background-color: #f2f2f2;
		position: relative;
		height: 100%;
		top: -1rpx;
		display: flex;
		flex-direction: column;
	}

	.booklist-container .search {
		background-color: #fd9941;
		flex: 0 1 85rpx;
	}

	.booklist-container .search .section {
		width: 96%;
		margin: 0 auto;
		height: 70rpx;
		position: relative;
	}

	.booklist-container .section .form {
		background-color: #fbb16f;
		width: 100%;
		height: 100%;
	}

	.booklist-container .search .input-block {
		width: 100%;
		height: 100%;
		line-height: 100%;
		color: #fff;
		display: inline-block;
		border-radius: 6rpx;
		font-size: 28rpx;
		position: relative;
	}

	.booklist-container .search .input-block>input {
		width: 100%;
		padding-left: 20rpx;
		height: 100%;
		box-sizing: border-box;
	}

	.booklist-container .search .input-block>icon {
		height: 70%;
		position: absolute;
		right: 17rpx;
		top: 17rpx;
		z-index: 999;
		font-size: 30rpx;
	}

	.booklist-container .search .search-block {
		position: absolute;
		top: 14rpx;
		left: 20rpx;
		height: 50rpx;
		line-height: 100%;
		font-size: 28rpx;
		color: #fff;
	}

	.booklist-container .search .search-block>text {
		margin-left: 4rpx;
	}

	.booklist-container icon.search-icon {
		width: 50rpx;
		display: inline-block;
		height: 100%;
		background-size: 230rpx 230rpx;
		background-position: -90rpx -90rpx;
		vertical-align: middle;
	}

	.booklist-container .toShop {
		width: 80rpx;
		height: 100%;
		position: absolute;
		right: 0;
		top: 0;
	}

	.booklist-container icon.shop-icon {
		display: inline-block;
		width: 40rpx;
		height: 40rpx;
		background-size: 230rpx 230rpx;
		background-position: 0rpx -100rpx;
		position: absolute;
		right: 10rpx;
		top: 15rpx;
	}

	.booklist-container .header {
		text-align: center;
		position: relative;
		background-color: #ffffff;
	}

	.booklist-container .header .headerLine {
		display: inline-block;
		height: 3rpx;
		width: 76%;
		background-color: #ccc;
		margin-top: 60rpx;
	}

	.booklist-container .header .headerTime {
		background-color: #ffffff;
		display: block;
		height: 20rpx;
		line-height: 20rpx;
		width: 240rpx;
		font-size: 32rpx;
		font-weight: bold;
		position: absolute;
		left: 50%;
		top: 52rpx;
		transform: translateX(-50%);
		color: #888;
	}

	.booklist-container .header .timeTab {
		margin-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.booklist-container .header .timeTab .everyTime {
		position: relative;
		display: inline-block;
		height: 100rpx;
		width: 100rpx;
		text-align: center;
	}

	.booklist-container .header .timeTab .everyTime>text {
		font-size: 24rpx;
		color: #888;
		vertical-align: middle;
		margin-right: 10rpx;
		line-height: 90rpx;
	}

	.booklist-container .header .timeTab .everyTime.last {
		margin-right: 0rpx;
	}

	.booklist-container .header .timeTab .everyTime.active {
		border: none;
		background-color: #fd9941;
		border-radius: 10rpx;
		box-shadow: 2rpx 2rpx 2rpx #cccccc;
	}

	.booklist-container .header .timeTab .everyTime.active>text {
		color: #fff;
	}

	.booklist-container .header .timeTab .everyTime>icon {
		position: absolute;
		left: 50%;
		top: 80%;
		transform: translateX(-50%);
		display: block;
		height: 10rpx;
		width: 10rpx;
		border: none;
		border-radius: 5rpx;
		background-color: transparent;
	}

	.booklist-container .header .timeTab .everyTime>icon.hasRecord {
		background-color: #888;
	}

	.booklist-container .list {
		margin-top: 20rpx;
		padding: 30rpx 30rpx;
		background-color: #fff;
		flex: 1;
	}

	.booklist-container .list .empty {
		text-align: center;
	}

	.booklist-container .list .empty>image {
		margin-top: 40rpx;
		height: 300rpx;
		width: 300rpx;
	}

	.booklist-container .list .empty>button {
		margin-top: 30rpx;
		width: 70%;
		height: 70rpx;
		line-height: 70rpx;
		border-color: #fd9941;
		color: #fd9941;
		font-size: 30rpx;
	}

	.booklist-container .list .all>image {
		height: 180rpx;
		width: 140rpx;
		box-shadow: 4rpx 4rpx 2rpx #888;
		float: left;
		display: inline-block;
		margin-left: -100%;
		margin-bottom: 20rpx;
	}

	.booklist-container .list .bookInfo {
		float: left;
		width: 100%;
		padding: 10rpx 10rpx 10rpx 180rpx;
		font-size: 22rpx;
		box-sizing: border-box;
		position: relative;
		margin-bottom: 20rpx;
		height: 200rpx;
		overflow: hidden;
	}

	.booklist-container .list .bookInfo::after {
		content: '';
		display: none;
		clear: both;
	}

	.booklist-container .bookInfo>text {
		display: block;
		color: #353535;
	}

	.booklist-container .bookInfo .bookName {
		font-size: 24rpx;
		font-weight: bold;
	}

	.booklist-container .bookInfo .author {
		font-size: 22rpx;
		margin-top: 10rpx;
	}

	.booklist-container .bookInfo .bookDes {
		font-size: 22rpx;
		color: #888;
		margin-top: 20rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
	}

	.booklist-container .bookInfo .text-underline {
		border-bottom: 4rpx solid #fd9941;
		margin-right: 10rpx;
		color: #888;
	}

	.booklist-container .bookInfo>icon {
		position: absolute;
		right: 20rpx;
		top: 10rpx;
		display: inline-block;
		width: 30rpx;
		height: 45rpx;
	}

	.booklist-container .bookInfo>icon.isRead {
		background-size: 210rpx 210rpx;
		background-position: -133rpx -80rpx;
	}

	.booklist-container .bookInfo>icon.notRead {
		background-size: 210rpx 210rpx;
		background-position: -176rpx -80rpx;
	}




	/*modal css*/
	.box {
		width: 100%;
		height: 50%;
		left: 12%;
		top: 20%;
		position: fixed;
		z-index: 101;

	}

	.msg-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		background-color: #ffffff;
		border-radius: 5px;
		width: 76%;
		padding-top: 30rpx;
		border: 6rpx solid black;
	}

	.title {
		margin: 10rpx;
		width: 100%;
		text-align: center;
		padding-bottom: 10px;
		border-bottom: 2rpx solid rgba(7, 17, 27, 0.1);
	}

	.msg {
		/* margin: 20rpx; */
		font-size: 12px;
		color: #ccc;
		border-top: 2rpx solid rgba(7, 17, 27, 0.1);
		padding-top: 20rpx;
		/* padding-bottom: 20px; */
		width: 80%;
	}

	.btn_wrapper {
		display: flex;
		flex-direction: row;
		align-items: center;
		height: 100rpx;
		line-height: 100rpx;
		border-top: 4rpx solid rgba(7, 17, 27, 0.1);
		width: 100%;
	}

	.btn {
		background-color: #fff;
		flex: 1;
		height: 100rpx;
		line-height: 100rpx;
		text-align: center;
		font-size: 36rpx;
		width: 200%;
		border-radius: 0;
	}

	.aumsg {
		padding-top: 80rpx;
		font-size: 34rpx;
		width: 80%;
	}

	.confirm_btn {
		border-left: 2rpx solid rgba(7, 17, 27, 0.1);
		color: green;
	}

	.btn::after {
		border: 0;
	}
</style>