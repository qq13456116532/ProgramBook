<template>
	<view class="book-detail-container">
		<view class="headBackgroundContainer">
			<!-- 	<view class="headBackground" :style="background:url(bookDetail.headImage) no-repeat center;background-size:200%;" > 
			</view> -->
			<view class="headBackground"
				:style="'background: url(' + bookDetail.headerImage + ') no-repeat center; background-size: 200%;'">
			</view>
		</view>
		<view class="head">
			<view class="bookInfo">
				<image class="headImg" :src="bookDetail.headerImage" mode="aspectFill"></image>
				<view class="left-text">
					<view class="bookName">{{bookDetail.name}}</view>
					<view class="author">作者：{{bookDetail.author}}</view>
					<view class="wordsNum">字数：348.33万</view>
					<view class="source">最新章节：{{bookDetail.newest}}</view>
					<view class="updatetime">更新时间：{{bookDetail.updateTime}}</view>
				</view>
			</view>
			<view class="clearfix"></view>
			<view class="btn-group">
				<view class="addToList" @click="addOrRemove">
					<view class="true" :hidden="!isInList == 0">
						<icon class="addList-icon" />加入书架
					</view>
					<view class="false" :hidden="!isInList == 1">
						<icon type="success_no_circle" color="#fd9941" size="20" />从书架中移除
					</view>
				</view>
				<view class="read" @click="goToReader">
					<image src="../../static/read1.png"></image>立即阅读
				</view>
			</view>
		</view>
		<view class="book-des">
			<view class="des-content" @click="showAllDesMethod">
				{{showAllDes || !bookDetail.shortDes ? (bookDetail.des.length<20?'本书是一种介绍编程语言、技术或概念的书籍，通常包含代码示例、练习和项目，帮助读者学习和实践编程。编程书有不同的难度级别，适合不同的读者群体，从初学者到专家。':bookDetail.des) : bookDetail.shortDes}}
				<text class="read-more" :hidden="showAllDes || !bookDetail.shortDes">阅读全部 </text>
			</view>
		</view>
		<view class="splitor"></view>
		<view class="comments">
			<view class="title">精彩评论<text class="add-comment" @click="toWriteComment">写评论</text>
			</view>
			<view class="comment-list">
				<view v-if="comments.length > 0" class="comment-item" v-for="item in comments"
					:data-commentid="item.rootComment.commentid" :key="item.rootComment.commentid">
					<image class="headImg" :src="item.rootComment.avatar" mode="scaleToFill"></image>
					<view class="comment-right">
						<view class="reader-name" :data-userid="item.rootComment.userid">{{item.rootComment.nickname}}
						</view>
						<view class="create-time">{{item.rootComment.time}}</view>
						<view class="comment">{{item.rootComment.content}}</view>
						<view class="comment-more" v-if="item.child.length > 0" @click="readMoreComments"
							:data-commentid="item.rootComment.commentid">{{'查看' + item.child.length + '回复 >'}}</view>
						<view class="childComments" v-if="item.child.length > 0"
							:hidden="!item.rootComment.isOpenMoreComment">
							<view class="child-item" v-for="(childItem, i) in item.child" :key="childItem.commentid"
								:data-commentid="childItem.commentid" :data-nickname="childItem.nickname"
								:data-userid="childItem.userid" @click="toWriteComment">
								{{childItem.nickname}} 回复 {{childItem.reply}} ： {{childItem.content}}
								<text class="childItem-time">{{childItem.time}}</text>
							</view>
						</view>
						<view class="toolbar">
							<!-- <icon :class="item.rootComment.isLike==1 ? 'like-selected-icon' : 'like-icon'"
								@click="addLikeNum" :data-commentid="item.rootComment.commentid" /> -->
								<icon :class="item.rootComment.isLike==1 ? 'iconfont icon-selectetd' : 'iconfont icon-like'"
									@click="addLikeNum" :data-commentid="item.rootComment.commentid" />
							<text class="likeNum">{{item.rootComment.likenum || 0}}</text>
							<icon class="iconfont reply" :data-commentid="item.rootComment.commentid"
								:data-nickname="item.rootComment.nickname" :data-userid="item.rootComment.userid"
								@click="toWriteComment" />
						</view>
					</view>
				</view>
				<view class="no-comments" v-if="comments.length == 0">
					<image src="https://wk-gulimall.oss-cn-beijing.aliyuncs.com/shafa.jfif"></image>
					暂无评论，快去抢沙发...
				</view>
			</view>
		</view>
		<view class="commnet-input-bar" :hidden="commentInputHide">
			<input class="comment-input" :value="currentCommentValue" placeholder="发表评论"
				placeholder-class="commentInputP" :focus="!commentInputHide" maxlength="100" @blur="hideCommentBar"
				@confirm="sendComment" confirm-type="send"></input>
			<view class="cancle" @click="hideCommentBar">取消</view>
		</view>

		<!-- 出错打印 -->
		<import src="../component/err_tips/err_tips.wxml" />
		<template is="err_tips" :data="err_tips_data" />

		<!-- 成功的提示 -->
		<import src="../component/toast/toast.wxml" />
		<template is="toast" :data="toast_data" />

		<!-- app状态页 -->
		<import src="../component/err_page/err_page.wxml" />
		<template is="err_page" :data="err_page_data" />
	</view>

</template>

<script>
	import Api from '../../utils/api/api.js';
	import Util from '../../utils/util.js';
	export default {
		data() {
			return {
				bookid: null,
				isInList: 0, // 是否已经在书架中
				userInfo: null,
				commentInputHide: true,
				showAllDes: false,
				bookDetail: null,
				comments: [],
				err_page_data: null, //app状态页
				commentType: 'root',
				token:"",
				userid: ""
			};
		},
		computed: {
			// isInList == 1 19
			judgeInlist1() {
				return this.isInList == 1;
			},
			// isInList == 0
			judgeInlist0() {
				return this.isInList == 0;
			},
		},
		onLoad(options) {
			var self = this;
			// show loading
			self.token = wx.getStorageSync('token');
			uni.showToast({
				title: '加载中',
				icon: 'loading',
				duration: 0
			});
			uni.request({
				url:Api.GetUserId(self.token),				
				success(res) {
					console.log("GetUserId的值是",res)
					self.userid = res.data.data
				}
			})
			
			console.log("打印一下options", options)
			this.showAllDes = false;
			this.bookid = options.bookid;
			self.getBookDetail();
			self.getComments(options.bookid);
		},
		methods: {
			getBookDetail: function() {
				var self = this;
				var bookid = this.bookid;
				uni.request({
					url: Api.getBookDetail(bookid),
					success: function(res) {
						// console.log("getBookDetail执行成功",res)
						var tmpData = res.data;
						console.log("getBookDetail的tmpData是",tmpData)
						if (tmpData && tmpData.code == 200) {
							//格式化日期
							var date = new Date(tmpData.data.updateTime);
							tmpData.data.updateTime = Util.formatDate3(date);
							// devide des into shortDes and des;
							var shortDes = '';
							// format des
							var des = tmpData.data.des;
							tmpData.data.des = des.replace(/( ){2,}/, ' ');
							if (des.length > 95) {
								shortDes = des.substring(0, 76);
							}
							tmpData.data.shortDes = shortDes;
							// console.log("看一下修改以后的tmpData", tmpData)
							self.bookDetail = tmpData.data
							self.err_page_data = null
							uni.setNavigationBarTitle({
								title: tmpData.data.factionName
							});
							self.bookDetail.name = tmpData.data.factionName
							uni.request({
								url:Api.GetJudgeIsInList(self.token,bookid),
								success(res) {
									console.log("GetJudgeIsInList的结果是",res.data)
									self.isInList = res.data.data
								}
							
							})
						} else {
							console.log('请求书籍信息失败....');
							self.err_page_data = {
								show: true,
								image_url: 'https://file.lantingshucheng.com/myapp/err_tips/network_err.png',
								text: '努力找不到网络>_<请检查后重试',
								buttonText: '重试',
								click: 'getBookDetail'
							}
						}
					},
					fail: function(err) {
						console.log(err);
						self.err_page_data = {
							show: true,
							image_url: 'https://file.lantingshucheng.com/myapp/err_tips/network_err.png',
							text: '努力找不到网络>_<请检查后重试',
							buttonText: '登录',
							click: 'getBookDetail'
						}
					},
					complete: function() {
						//hide loading
						setTimeout(function() {
							uni.hideToast()
						}, 1000)
					}
				});
			},
			getComments: function(bookid) {
				var self = this;
				uni.request({
					url: Api.listComments(bookid)+ "&token="+self.token,
					success: function(res) {
						var tmpData = res.data;
						console.log("打印一下评论",tmpData)
						if (tmpData && tmpData.code == 200) {
							// 格式化日期
							var finalData = tmpData.data.map(function(item) {
								// rootComment
								var rootDate = new Date(item.rootComment.time);
								item.rootComment.time = Util.formatDate3(rootDate);
								if (item.rootComment.userid == self.userid) {
									item.rootComment.nickname = "我"
								}
								// child
								item.child.forEach(function(childItem) {
									var childDate = new Date(childItem.time);
									childItem.time = Util.formatDate3(childDate);
									if (childItem.userid == self.userid) {
										childItem.nickname = "我"
									}
								});
								// isLike and isOpenMoreComment
								// item.rootComment.isLike = 0;
								item.rootComment.isOpenMoreComment = 0;
								return item;
							});
							self.comments = finalData

						} else {
							Util.showErrMsg(self, '请求评论失败!', 1000);
							
							console.log('请求评论失败....');
							self.err_page_data= {
									show: true,
									image_url: 'https://file.lantingshucheng.com/myapp/err_tips/network_err.png',
									text: '努力找不到网络>_<请检查后重试',
									buttonText: '重试',
									click: 'getBookDetail'
								}
						}
					},
					fail: function(err) {
						console.log(err);
						self.err_page_data={
								show: true,
								image_url: 'https://file.lantingshucheng.com/myapp/err_tips/network_err.png',
								text: '努力找不到网络>_<请检查后重试',
								buttonText: '重试',
								click: 'getBookDetail'
							}
					},
					complete: function() {
						//hide loading
						setTimeout(function() {
							uni.hideToast()
						}, 1000)
					}
				});
			},
			showAllDesMethod: function() {
				if (this.bookDetail.shortDes) {
					if (this.showAllDes) {
						this.showAllDes = false;
					} else {
						this.showAllDes = true;
					}
				}
			},
			addLikeNum: function(e) {
				// console.log("当前在执行addLikeNum",e)
				var self = this;
				var commentid = e.target.dataset.commentid;
				var isInLikeArr = self.comments.some(function(item) {
					return commentid === item.rootComment.commentid && item.rootComment.isLike
				});
				// 如果已经点赞了，就不重复发接口了
				if (!isInLikeArr) {
					var addLikeData = {
						bookid: self.bookid,
						commentid: commentid,
						userid: self.userid
					};
					console.log("看一下addLikeData",addLikeData)
					wx.request({
					  method: 'post',
					  url: Api.addLikeNum(),
					  data: addLikeData,
					  success: function (res) {
					    var tmpData = res.data;
						console.log("看一下tmpData",tmpData)
						
					    if (tmpData && tmpData.code === 200) {
							if(tmpData.data==0){
								wx.showToast({
								  title: '已经点赞',
								  icon: 'error',
								  duration: 2000
								})
								return;
							}
					      console.log('点赞成功');
					      var comments = self.comments;
					      // 更新comments
					      comments.forEach(function (item) {
					        if (commentid === item.rootComment.commentid) {
					          item.rootComment.isLike = 1;
					          ++item.rootComment.likenum;
					        }
					      })
						  self.comments = comments
					    } else {
					      Util.showErrMsg(self, '点赞失败!', 1000);
					    }
					  },
					  fail: function (err) {
					    console.log(err);
					    Util.showErrMsg(self, '点赞失败!', 1000);
					  }
					});
				}else{
					var RemoveLikeData = {
						bookid: self.bookid,
						commentid: commentid,
						token: self.token
					};
					console.log("看一下RemoveLikeData",RemoveLikeData)
					wx.request({
					  method: 'post',
					  url: Api.RemoveLikeNum(self.token),
					  data: RemoveLikeData,
					  success: function (res) {
						console.log("RemoveLikeNum看一下res",res)
					      var comments = self.comments;
					      // 更新comments
					      comments.forEach(function (item) {
					        if (commentid === item.rootComment.commentid) {
					          item.rootComment.isLike = 0;
					          --item.rootComment.likenum;
					        }
					      })
						  self.comments = comments
					  }
					});
				}
			},
			addOrRemove: function() {
				var self = this;
				// 已在书架的先提示确认是否移除，然后调用deleteMyBook， 不在驾的调用addMyBook
				if (self.isInList === 1) {
					var deleteData = {
						userid: self.userid,
						bookid: self.bookid
					};
					  uni.request({
					    method: 'post',
					    url: Api.deleteMyBooks(),
					    data: deleteData,
					    success: function (res) {
					      var tmpData = res.data;
					      if (tmpData && tmpData.code === 200) {
					        console.log('将该书已成功从书架中移除');
					        uni.showToast({
					          title: '已从书架移除',
					          icon: 'success',
					          duration: 2000
					        });
					        setTimeout(function () {
					          uni.hideToast();
					        }, 1000);
							self.isInList = 0;
					      } else {
					        Util.showErrMsg(self, '移除失败!', 1000);
					      }
					    },
					    fail: function (err) {
					      console.log(err);
					      Util.showErrMsg(self, '移除失败!', 1000);
					    }
					  });
					} else {					
					  var addData = {
					    userid: self.userid,
					    bookid: self.bookid
					  };
					  uni.request({
					    method: 'post',
					    url: Api.addMyBooks(),
					    data: addData,
					    success: function (res) {
					      var tmpData = res.data;
					      if (tmpData && tmpData.code === 200) {
					        console.log('该书添加已成功添加至书架',tmpData);
					        uni.showToast({
					          title: '已添加至书架',
					          icon: 'success',
					          duration: 2000
					        });
					        setTimeout(function () {
					          uni.hideToast();
					        }, 1000);
							self.isInList = 1
					      } else if(tmpData.code === 400){
					        uni.showToast({
					          title: '本书已添加',
					          icon: 'error',
					          duration: 2000
					        });
							self.isInList = 1;
					      }
					    },
					    fail: function (err) {
					      console.log(err);
					      Util.showErrMsg(self, '添加失败!', 1000);
					    }
					  });
				}
			},
			goToReader: function() {
				uni.navigateTo({
					url: 'reader/reader?bookid=' + this.bookid + '&name=' + this.bookDetail.name
				});
			},
			//评论相关
			readMoreComments: function(e) {
				var self = this;				
				var commentid = e.target.dataset.commentid;
				var hasOpenMoreComent = self.comments.some(function(item) {
					// console.log("看一下每一步的",commentid,item.rootComment.commentid,item.rootComment.isOpenMoreComment)
					return commentid === item.rootComment.commentid && item.rootComment.isOpenMoreComment
				});
				// console.log("hasOpenMoreComent是", hasOpenMoreComent)
				var comments = self.comments;
				// 更新comments
				comments.forEach(function(item) {
					if (commentid === item.rootComment.commentid) {
						if (hasOpenMoreComent) {
							item.rootComment.isOpenMoreComment = 0;
						} else {
							item.rootComment.isOpenMoreComment = 1;
						}
					}
				})
				self.comments = comments
			},
			toWriteComment: function(e) {
				console.log("towriteComment,看一下拿到的参数",e)
				var commentid = e.target.dataset.commentid;
				var nickname = e.target.dataset.nickname;
				var userid = e.target.dataset.userid;
				if (commentid && nickname && userid) {
					this.commentInputHide = false;
					this.commentType = {
						id: commentid,
						nickname: nickname,
						userid: userid
					};
				} else {
					this.commentInputHide = false;
					this.commentType = {
						id: 'root',
						nickname: '',
						userid: ''
					}
				}
			},
			hideCommentBar: function() {
				this.commentInputHide = true;
			},
			stageCommentValue: function(e) {
				this.currentCommentValue = e.value
			},
			sendComment: function(e) {
				console.log("看一下两种触发方式区别", e)
				var self = this;
				var comments = self.comments;
				var content = e.detail.value;
				// console.log("content是",content)
				var addCommentData = {
					userid: self.userid,
					bookid: self.bookid,
					father: self.commentType.id,
					content: content
				};
				console.log("看一下addCommentData", addCommentData)
				// 不能给自己回复
				if (self.userid !== self.commentType.userid) {
					// 调用增加评论的接口
					uni.request({
						url: Api.addComment(),
						method: 'post',
						data: JSON.stringify(addCommentData),
						success: function(res) {
							var tmpData = res.data;
							console.log("tmpData是什么", tmpData)
							if (tmpData && tmpData.code == 200) {
								// 评论成功
								if (addCommentData.father === 'root') {
									self.getComments(self.bookid)
									// comments.unshift({
									// 	rootComment: {
									// 		commentid: tmpData.data,
									// 		userid: self.userid,
									// 		nickname: '我',
									// 		avatar: self.userInfo.avatar,
									// 		father: 'root',
									// 		content: content,
									// 		isLike: 0,
									// 		likenum: 0,
									// 		isOpenMoreComment: 0,
									// 		time: Util.formatDate3(new Date())
									// 	},
									// 	child: []
									// });
								} else {
									comments.forEach(function(item) {
										var isFind = false;
										// 先查找root
										if (item.rootComment.commentid === self.commentType.id) {
											item.child.push({
												commentid: tmpData.commentid,
												userid: self.userid,
												nickname: '我',
												avatar: self.userInfo.avatar,
												father: self.commentType.id,
												content: content,
												reply: self.commentType.nickname,
												time: Util.formatDate3(new Date())
											});
											isFind = true;
										} else {
											// 再查找child
											if (item.child.length > 0) {
												item.child.forEach(function(item2, index, array) {
													if (item2.commentid === self
														.commentType.id) {
														array.push({
															commentid: tmpData.commentid,
															userid: self.userid,
															nickname: '我',
															avatar: self.userInfo.avatar,
															father: self.commentType.id,
															content: content,
															reply: self.commentType.nickname,
															time: Util.formatDate3(
																new Date())
														});
													}
												});
												isFind = true;
											}
										}
										//发表评论之后保证isOpenMoreComment是1，即可以看到详细回复
										if (isFind) {
											item.rootComment.isOpenMoreComment = 1;
										}
									});
								}
								// 更新书评数组
								self.comments = comments
								self.currentCommentValue = ''
								uni.showToast({
									title: '发布成功',
									icon: 'success',
									duration: 0
								});
								setTimeout(function() {
									uni.hideToast();
								}, 1000)
							} else {
								console.log('发布....');
								Util.showErrMsg(self, '发布评论失败', 1000);
							}
						},
						fail: function(err) {
							console.log(err);
							Util.showErrMsg(self, '发布评论失败', 1000);
						}
					});
				} else {
					Util.showErrMsg(self, '请勿给自己回复!', 1000);
				}
			}
		}

	}
</script>

<style lang="stylus">
	/* CDN 服务仅供平台体验和调试使用，平台不承诺服务的稳定性，企业客户需下载字体包自行发布使用并做好备份。 */
	.bookName {
	  display: -webkit-box;
	    -webkit-line-clamp: 1; /* number of lines to show */
	    -webkit-box-orient: vertical;  
	    overflow: hidden;
		width: 400rpx; 
	}
	
	.reply::before{
		content: "\e644";
		font-size: 50rpx;
	}
	.icon-selectetd::before{
	  content: '\e8c3';
	}
	
	.icon-like::before{
	  content: '\e8ad';
	}
	.err_page {
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

	.err_page>image {
		width: 330rpx;
		height: 330rpx;
		margin-top: 40%;
		margin-bottom: 30rpx;
	}

	.err_page>button {
		margin-top: 40rpx;
		width: 180rpx;
		line-height: 60rpx;
		height: 60rpx;
		font-size: 32rpx;
		color: #fd9941;
		border: 2rpx solid #fd9941;
		border-radius: 8rpx;
	}

	.book-detail-container {
		line-height: 1.8;
		font-size: 12px;
	}

	.headBackgroundContainer {
		width: 100%;
		height: 484rpx;
		overflow: hidden;
		position: absolute;
		z-index: -1;
	}

	.headBackground {
		margin-left: -28rpx;
		margin-top: -28rpx;
		width: 110%;
		height: 600rpx;
		-webkit-filter: blur(28rpx) brightness(60%);
	}

	.head {
		color: #ffffff;
		font-size: 24rpx;
		padding: 40rpx;
	}

	.head .headImg {
		width: 210rpx;
		height: 290rpx;
		vertical-align: text-top;
		float: left;
	}

	.head .bookInfo {
		overflow: auto;
		margin-bottom: 40rpx;
	}

	.head .bookInfo .left-text {
		color: rgba(255, 255, 255, 0.9);
		margin-left: 40rpx;
		float: left;
		display: inline-block;
	}

	.head .bookInfo .left-text .bookName {
		font-size: 32rpx;
		color: #ffffff;
		margin-bottom: 24rpx;
	}

	.head .bookInfo .left-text view {
		margin-bottom: 12rpx;
	}

	.head .btn-group {
		line-height: 0rpx;
		font-size: 0;
	}

	/*两个图标*/
	.addList-icon {
		background-size: 220rpx 220rpx;
		background-position: -181rpx 0rpx;
		display: inline-block;
		height: 40rpx;
		width: 40rpx;
		vertical-align: middle;
		position: relative;
		top: -4rpx;
		margin-right: 10rpx;
	}

	.readNow-icon {
		background-size: 400rpx 400rpx;
		background-position: 6rpx -76rpx;
	}

	.head .btn-group .addToList,
	.read {
		font-size: 28rpx;
		display: inline-block;
		box-sizing: border-box;
		width: 315rpx;
		padding: 20rpx 10rpx;
		text-align: center;
		background: rgba(238,150,17, 0.8);
		border-radius: 8rpx;
		height: 72rpx;
	}

	.head .btn-group .read {
		margin-left: 40rpx;
	}

	.head .btn-group .addToList .false icon {
		vertical-align: middle;
		position: relative;
		top: -5rpx;
		margin-right: 10rpx;
	}

	.head .btn-group image {
		vertical-align: text-top;
		margin-top: -4rpx;
		margin-right: 10rpx;
		height: 38rpx;
		width: 38rpx;
	}

	.book-des {
		font-size: 28rpx;
		padding: 40rpx;
	}

	.book-des .read-more {
		padding-left: 10rpx;
		color: #fd9941;
		font-size: 24rpx;
	}

	.book-des .des-content {
		text-align: justify;
	}

	.comments {}

	.comments .title {
		border-bottom: 1rpx solid #dcdcdc;
		padding: 0 30rpx;
		height: 80rpx;
		font-size: 32rpx;
		line-height: 80rpx;
	}

	.comments .title .add-comment {
		color: #FD9941;
		float: right;
	}

	.comments .comment-list {
		margin-top: -1rpx;
		background: #f5f5f5;
	}

	.comment-item {
		background: #ffffff;
		padding: 20rpx;
		box-sizing: border-box;
		overflow: auto;
		width: 100%;
	}

	.comment-item .headImg {
		float: left;
		width: 70rpx;
		height: 70rpx;
		margin-right: 20rpx;
		border-radius: 40rpx;
		display: inline-block;
		border: 1rpx solid #eeeeee;
	}

	.comment-item .comment-right {
		float: left;
		width: 610rpx;
		display: inline-block;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #e1e1e1;
		position: relative;
	}

	.comment-item .comment-right .reader-name {
		color: #666666;
		font-size: 24rpx;
	}

	.comment-item .comment-right .comment {
		text-align: justify;
		color: #333;
		font-size: 26rpx;
	}

	.comment-item .comment-right .comment-more {
		background: #efefef;
		font-size: 22rpx;
		padding: 4rpx 10rpx;
	}

	.comment-item .comment-right .create-time {
		font-size: 20rpx;
		color: #808080;
	}

	.childComments {
		margin-top: 10rpx;
		font-size: 22rpx;
	}

	.childComments .childItem-time {
		padding-left: 20rpx;
		font-size: 20rpx;
		color: #808080;
	}

	.comment-right .toolbar {
		position: absolute;
		right: 0;
		top: 0;
	}

	.comment-right .toolbar icon {
		display: inline-block;
		height: 36rpx;
		width: 36rpx;
		background-size: 180rpx 180rpx;
	}

	.like-icon {
		background-position: -107rpx 0rpx;
		margin-right: 10rpx;
	}

	.like-selected-icon {
		background-position: -37rpx 1rpx;
		margin-right: 10rpx;
	}

	.likeNum {
		position: relative;
		top: -8rpx;
		color: #808080;
		margin-left: 19rpx;
		margin-right: 26rpx;
	}

	.comment-icon {
		background-position: -73rpx 0rpx;
	}

	.commnet-input-bar {
		height: 100rpx;
		font-size: 0;
		width: 100%;
		padding: 0 20rpx;
		position: fixed;
		bottom: 0;
		background: #fff4ea;
		box-sizing: border-box;
		border-top: 1rpx solid #dcdcdc;
	}

	.comment-input {
		height: 60rpx;
		font-size: 28rpx;
		padding: 0 20rpx;
		margin: 20rpx 0;
		display: inline-block;
		width: 610rpx;
		background: #ffffff;
		border: 1rpx solid #dcdcdc;
		border-radius: 10rpx;
		box-sizing: border-box;
	}

	.commentInputP {
		line-height: 60rpx;
	}

	.commnet-input-bar .cancle {
		display: inline-block;
		width: 100rpx;
		height: 60rpx;
		color: #666666;
		font-size: 28rpx;
		text-align: center;
		float: right;
		margin: 20rpx 0;
	}

	.no-comments {
		padding-top: 50rpx;
		text-align: center;
		height: 100%;
		background: #fff;
		font-size: 30rpx;
	}

	.no-comments>image {
		width: 100rpx;
		height: 100rpx;
		display: block;
		margin: 0rpx auto;
		margin-bottom: 30rpx;
	}
</style>
